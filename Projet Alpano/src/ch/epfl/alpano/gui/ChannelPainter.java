package ch.epfl.alpano.gui;

import ch.epfl.alpano.Math2;
import ch.epfl.alpano.Panorama;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;

/**
 * This functional interface represents a channel painter. the channel painter
 * is there to associate a (channel) value to a (x/y)-point. the interface
 * provides also static methods to manipulate or create this value.
 * 
 * @author Natal Willisch (262092)
 *
 */
@FunctionalInterface
public interface ChannelPainter {

    /**
     * Returns the value (of a channel) depending on the coordinates.
     * 
     * @param x
     *            horizontal position
     * @param y
     *            vertical position
     * @return channel value
     */
    public double valueAt(int x, int y);

    /**
     * Returns a ChannelPainter that return a value that correspond to the
     * distance between a coordinate and to its neighbor-coordinate that is most
     * fare away from the observer.
     * 
     * @param panorama
     *            the panorama from where the function takes the distances of
     *            the coordinates
     * @return ChannelPainter that return the above described value.
     */
    public static ChannelPainter maxDistanceToNeighbors(Panorama panorama) {

        return (x, y) -> {
            float maxHorizontal, maxVertical, maxAround;

            maxHorizontal = max(panorama.distanceAt(x - 1, y, 0),
                    panorama.distanceAt(x + 1, y, 0));
            maxVertical = max(panorama.distanceAt(x, y - 1, 0),
                    panorama.distanceAt(x, y + 1, 0));
            maxAround = max(maxHorizontal, maxVertical);

            return maxAround - panorama.distanceAt(x, y, 0);
        };

    }

    /**
     * Creates a ChannelPainter that adds a fix value to the output of the
     * Channel-painter to which this function is applied.
     * 
     * @param val
     *            value to add
     * @return transformed Channel-painter
     */
    public default ChannelPainter add(double val) {

        return (x, y) -> this.valueAt(x, y) + val;

    }

    /**
     * Creates a ChannelPainter that subtracts a fix value to the output of the
     * Channel-painter to which this function is applied.
     * 
     * @param val
     *            value to subtract
     * @return transformed Channel-painter
     */
    public default ChannelPainter sub(double val) {

        return (x, y) -> this.valueAt(x, y) - val;

    }

    /**
     * Creates a ChannelPainter that multiplies a fix value to the output of the
     * Channel-painter to which this function is applied.
     * 
     * @param val
     *            multiplier
     * @return transformed Channel-painter
     */
    public default ChannelPainter mul(double val) {

        return (x, y) -> this.valueAt(x, y) * val;

    }

    /**
     * Creates a ChannelPainter that divides the output of the Channel-painter
     * to which this function is applied with a fix value.
     * 
     * @param val
     *            divisor
     * @return transformed Channel-painter
     */
    public default ChannelPainter div(double val) {

        return (x, y) -> this.valueAt(x, y) / val;

    }

    /**
     * Transforms a ChannelPainter output according to the given function.
     * 
     * @param f
     *            manipulating function
     * @return transformed Channel-painter
     */
    public default ChannelPainter map(DoubleUnaryOperator f) {

        return (x, y) -> f.applyAsDouble(this.valueAt(x, y));

    }

    /**
     * Inverters the ChannelPainter's output.
     * 
     * @return transformed Channel-painter
     */
    public default ChannelPainter inverted() {

        return (x, y) -> 1 - this.valueAt(x, y);

    }

    /**
     * Restrict the output of the ChannelPainter to a value between 0 and 1.
     * 
     * @return transformed Channel-painter
     */
    public default ChannelPainter clamped() {

        return (x, y) -> max(0, min(this.valueAt(x, y), 1));

    }

    /**
     * returns a ChannelPainter which output correspond to the mod1 of the
     * original output, so a value between 0 and 1.
     * 
     * @return transformed Channel-painter
     */
    public default ChannelPainter cycling() {

        return (x, y) -> Math2.floorMod(this.valueAt(x, y), 1);

    }

    /**
     * Choose a realistic colors depending on the properties of a point in a
     * panorama.
     * 
     * @param panorama
     *            a Panorama
     * @return ChannelPainter
     */
    public static ChannelPainter photorealistic(Panorama panorama) {
        IntUnaryOperator x_cut = x -> max(0,
                min(x, panorama.parameters().width() - 1));
        IntUnaryOperator y_cut = y -> max(0,
                min(y, panorama.parameters().height() - 1));
        BiFunction<Integer, Integer, Float> slopF = panorama::slopeAt;
        BiFunction<Integer, Integer, Float> elevF = panorama::elevationAt;

        final double flat = 0.00001;

        return (x, y) -> {

            double slope = slopF.apply(x, y);
            double elevation = elevF.apply(x, y);

            double facteur = (panorama.parameters().width()
                    / panorama.parameters().horizontalFieldOfView()
                    / panorama.distanceAt(x, y) * 1000);

            int vFactor = (int) (Math
                    .sqrt((panorama.parameters().altitudeForY(y) / Math2.PI2)
                            * facteur))
                    + 1;
            int hFactor = (int) facteur + 1;

            IntUnaryOperator checkAround = (multi) -> {
                int xLeft = x_cut.applyAsInt(x - multi * hFactor);
                int xRight = x_cut.applyAsInt(x + multi * hFactor);
                int yTop = y_cut.applyAsInt(y - multi * vFactor);
                int yButtom = y_cut.applyAsInt(y + multi * vFactor);
                int count = 0;

                if (slopF.apply(xLeft, y) < flat
                        && elevF.apply(xLeft, y) == elevation)
                    count++;
                if (slopF.apply(xLeft, yTop) < flat
                        && elevF.apply(xLeft, yTop) == elevation)
                    count++;
                if (slopF.apply(xLeft, yButtom) < flat
                        && elevF.apply(xLeft, yButtom) == elevation)
                    count++;
                if (slopF.apply(x, yTop) < flat
                        && elevF.apply(x, yTop) == elevation)
                    count++;
                if (slopF.apply(x, yButtom) < flat
                        && elevF.apply(x, yButtom) == elevation)
                    count++;
                if (slopF.apply(xRight, yTop) < flat
                        && elevF.apply(xRight, yTop) == elevation)
                    count++;
                if (slopF.apply(xRight, yButtom) < flat
                        && elevF.apply(xRight, yButtom) == elevation)
                    count++;
                if (slopF.apply(xRight, y) < flat
                        && elevF.apply(xRight, y) == elevation)
                    count++;

                return count;
            };

            if (snow1.test(elevation, slope))
                return 0.5;

            else if (slope < flat
                    && slopF.apply(x_cut.applyAsInt(x + 1), y) < flat
                    && slopF.apply(x_cut.applyAsInt(x - 1), y) < flat
                    && (slopF.apply(x, y_cut.applyAsInt(y + 1)) < flat || slopF
                            .apply(x, y_cut.applyAsInt(y - 1)) < flat)) {

                int count = checkAround.applyAsInt(1);

                if (count == 8)
                    return 0.56;
                else if (count > 3) {
                    count = checkAround.applyAsInt(3);

                    if (count > 2)
                        return 0.55;
                }
            }

            return 0.35;
        };
    }

    /**
     * changes the saturation depending of the properties of the point in the
     * Panorama (Higher altitude leads normally to a lose of saturation (that's
     * why the name is fade))
     * 
     * @param panorama
     *            a Panorama
     * @return a ChannelPainter
     */
    default ChannelPainter fade(Panorama panorama) {

        return (x, y) -> {
            double slope = panorama.slopeAt(x, y);
            double elevation = panorama.elevationAt(x, y);
            double value = this.valueAt(x, y);

            if (snow1.test(elevation, slope) || snow2.test(elevation, slope))
                return 0;
            if (stone.test(elevation, slope))
                return Math.max(0, value * Math.pow(1 - elevation / 6000, 1.5))
                        * 0.5;

            return Math.max(0, value * Math.pow(1 - elevation / 6000, 1.5))
                    * 0.9;
        };
    }

    /**
     * Changes the brightness depending of the properties of the point in the
     * Panorama ("snow areas" have a higher brightness, as they reflect the
     * light)
     * 
     * @param panorama
     *            a Panorama
     * @return a ChannelPainter
     */
    default ChannelPainter snowB(Panorama panorama) {

        return (x, y) -> {
            double slope = panorama.slopeAt(x, y);
            double elevation = panorama.elevationAt(x, y);
            double value = this.valueAt(x, y);
            if (snow1.test(elevation, slope))
                return value * 1.4;
            if (snow2.test(elevation, slope))
                return value * 1.35;
            return value * 0.8;
        };
    }

    /*
     * Some BiPredicate-function-classes, to be sure that in all function that help to
     * create a realistic colored have the same criteria.
     */
    /**
     * Criteria for snow areas. (higher 2800m)
     */
    static BiPredicate<Double, Double> snow1 = (elevation,
            slope) -> elevation > 2800 && slope < 1 && slope > 0.8;
    /**
     * Criteria for snow areas. (higher 2500m)
     */
    static BiPredicate<Double, Double> snow2 = (elevation,
            slope) -> elevation > 2500 && slope < 1 && slope > 0.9;
    /**
     * Criteria for stone areas.
     */
    static BiPredicate<Double, Double> stone = (elevation,
            slope) -> slope > 0.8 && elevation > 1900 || slope > 0.9;

}
