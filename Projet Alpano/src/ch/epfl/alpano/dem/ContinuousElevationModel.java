package ch.epfl.alpano.dem;

import static java.lang.Math.*;
import static ch.epfl.alpano.Math2.*;
import static ch.epfl.alpano.Distance.*;
import static ch.epfl.alpano.dem.DiscreteElevationModel.*;

import java.util.Objects;
import ch.epfl.alpano.GeoPoint;

/**
 * From a discrete elevation Model
 * {@link ch.epfl.alpano.dem.DiscreteElevationModel} this class creates a
 * continuous elevation model. So the model is not anymore only accessible by a
 * index (integer values), the model is now accessible by geo-point
 * ({@link GeoPoint}) and values that are not in the discrete elevation model
 * will be interpolated.
 * 
 * @author Natal Willisch (262092)
 *
 */
public final class ContinuousElevationModel {
    private final DiscreteElevationModel dem;

    /**
     * The Constructor takes a discrete elevation model.
     * 
     * @param dem
     *            a {@link DiscreteElevationModel}
     * @throws NullPointerException
     *             if {@code dem} is null
     */
    public ContinuousElevationModel(DiscreteElevationModel dem) {
        this.dem = Objects.requireNonNull(dem);
    }

    /**
     * Returns the elevation at a given point.
     * 
     * @param p
     *            a {@link GeoPoint}
     * @return a elevation
     */
    public double elevationAt(GeoPoint p) {
        Values x = new Values(p.longitude());
        Values y = new Values(p.latitude());
        double z00 = elevationAtDEM(x.floor(), y.floor());
        double z10 = elevationAtDEM(x.ceiling(), y.floor());
        double z01 = elevationAtDEM(x.floor(), y.ceiling());
        double z11 = elevationAtDEM(x.ceiling(), y.ceiling());

        return bilerp(z00, z10, z01, z11, x.relPos(), y.relPos());
    }

    /**
     * Returns the slope at a given point.
     * 
     * @param p
     *            a {@link GeoPoint}
     * @return a slope
     */
    public double slopeAt(GeoPoint p) {
        Values x = new Values(p.longitude());
        Values y = new Values(p.latitude());

        double z00 = slopeAtDEM(x.floor(), y.floor());
        double z10 = slopeAtDEM(x.ceiling(), y.floor());
        double z01 = slopeAtDEM(x.floor(), y.ceiling());
        double z11 = slopeAtDEM(x.ceiling(), y.ceiling());

        return bilerp(z00, z10, z01, z11, x.relPos(), y.relPos());
    }

    // private functions

    /**
     * Returns the elevation obtained from DEM or returns 0
     */
    private double elevationAtDEM(int x, int y) {
        if (dem.extent().contains(x, y)){
            return dem.elevationSample(x, y);
        }
        return 0;

    }

    /**
     * returns the slope obtained from the DEM
     */
    private double slopeAtDEM(int x, int y) {
        double a = elevationAtDEM(x, y);
        double za = elevationAtDEM(x + 1, y) - a;
        double zb = elevationAtDEM(x, y + 1) - a;
        double d = toMeters(1 / SAMPLES_PER_RADIAN);

        return acos(d / sqrt(za * za + zb * zb + d * d));
    }

    /**
     * This class is there to stock the x-value and it's rounded values and the
     * relative position of the value between its floored and its ceiled value..
     * 
     * @author Natal Willisch (262092)
     *
     */
    private final class Values {
        
        private final double x;
        private final int floor_x;
        private final int ceiling_x;
        private final double relative_pos;

        Values(double angle) {
            
            x = sampleIndex(angle);
            floor_x = (int) Math.floor(x);
            ceiling_x = floor_x + 1;
            relative_pos = x - floor_x;
            
        }

        double val() {
            return x;
        }

        int floor() {
            return floor_x;
        }

        int ceiling() {
            return ceiling_x;
        }

        double relPos() {
            return relative_pos;
        }

    }
}
