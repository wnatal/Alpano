package ch.epfl.alpano.gui;

import static java.lang.Math.max;

import ch.epfl.alpano.Panorama;
import javafx.scene.paint.Color;

/**
 * This functional interface represents a image painter. the image painter is
 * there to associate a color to a (x/y)-position. The interface offers overs
 * static functions to create those Image-Painters.
 * 
 * @author Natal Willisch (262092)
 *
 */
@FunctionalInterface
public interface ImagePainter {

    /**
     * Returns a color depending on the coordinates.
     * 
     * @param x
     *            horizontal position
     * @param y
     *            vertical position
     * @return color
     */
    public Color colorAt(int x, int y);

    /**
     * Returns a ImagePainter for colored pictures.
     * 
     * @param hue
     *            hue (basic/pure color) of the color
     * @param saturation
     *            intensity of the color
     * @param brightness
     *            brightness of the color
     * @param opacity
     *            opacity
     * @return a ImagePainter
     */
    public static ImagePainter hsb(ChannelPainter hue,
            ChannelPainter saturation, ChannelPainter brightness,
            ChannelPainter opacity) {

        return (x, y) -> Color.hsb(hue.valueAt(x, y), saturation.valueAt(x, y),
                brightness.valueAt(x, y), opacity.valueAt(x, y));

    }

    /**
     * Returns a ImagePainter for monochromatic pictures.
     * 
     * @param gray
     *            "grayness" (=brightness of the grey)
     * 
     * @param opacity
     *            opacity
     * @return a ImagePainter
     */
    public static ImagePainter gray(ChannelPainter gray,
            ChannelPainter opacity) {

        return (x, y) -> Color.gray(gray.valueAt(x, y), opacity.valueAt(x, y));

    }

    /**
     * ImagePainter for the standard painting.
     * 
     * @param panorama
     *          a Panorama
     * @return a ImagePainter
     */
    public static ImagePainter rainbow(Panorama panorama) {

        ChannelPainter distance = panorama::distanceAt;
        ChannelPainter slope = panorama::slopeAt;
        ChannelPainter h = distance.div(100000).cycling().mul(360);
        ChannelPainter s = distance.div(200000).clamped().inverted();

        ChannelPainter b = slope.mul(2 / Math.PI).inverted().mul(0.7).add(0.3);
        ChannelPainter o = distance
                .map(d -> d == Float.POSITIVE_INFINITY ? 0 : 1);

        return hsb(h, s, b, o);

    }

    /**
     * ImagePainter for a greyish painting.
     * 
     * @param panorama
     *          a Panorama
     * @return a ImagePainter
     */
    public static ImagePainter grayish(Panorama panorama) {

        ChannelPainter distance = panorama::distanceAt;
        ChannelPainter slope = panorama::slopeAt;

        ChannelPainter b = slope.mul(2 / Math.PI).inverted().sub(0.12)
                .clamped();
        ChannelPainter o = distance
                .map(d -> d == Float.POSITIVE_INFINITY ? 0 : 1);

        return gray(b, o);

    }

    /**
     * ImagePainter for a painting that indicates the isohypses for all 200m.
     * 
     * @param panorama
     *          a Panorama
     * @return a ImagePainter
     */
    public static ImagePainter layer(Panorama panorama) {

        ChannelPainter distance = panorama::distanceAt;
        ChannelPainter slope = panorama::slopeAt;
        ChannelPainter elevation = panorama::elevationAt;
        ChannelPainter h = elevation.div(1000).cycling().mul(360);
        ChannelPainter s = (x,
                y) -> elevation.div(200).cycling().valueAt(x, y) < 0.05 ? 1 : 0;

        ChannelPainter b = slope.mul(2 / Math.PI).inverted().sub(0.12)
                .clamped();
        ChannelPainter o = distance
                .map(d -> d == Float.POSITIVE_INFINITY ? 0 : 1);
        
        return hsb(h, s, b, o);

    }

    /**
     * ImagePainter for a painting that looks like a drawing.
     * 
     * @param panorama
     *          a Panorama
     * @return a ImagePainter
     */
    public static ImagePainter draw(Panorama panorama) {
        ChannelPainter gray = ChannelPainter.maxDistanceToNeighbors(panorama)
                .sub(500).div(4500).clamped().inverted();

        ChannelPainter distance = panorama::distanceAt;
        ChannelPainter opacity = distance
                .map(d -> d == Float.POSITIVE_INFINITY ? 0 : 1);

        return ImagePainter.gray(gray, opacity);
    }

    /**
     * ImagePainter that produces a image with realistic colors.
     * 
     * @param panorama
     *          a Panorama
     * @return a ImagePainter
     */
    public static ImagePainter photorealistic(Panorama panorama) {

        ChannelPainter distance = panorama::distanceAt;
        ChannelPainter slope = panorama::slopeAt;
        ChannelPainter h = ChannelPainter.photorealistic(panorama).mul(360);
        ChannelPainter s = distance.div(200000).clamped().inverted()
                .fade(panorama);

        ChannelPainter b = slope.mul(2 / Math.PI).inverted().mul(0.7).add(0.3)
                .snowB(panorama).clamped();
        ChannelPainter o = distance
                .map(d -> d == Float.POSITIVE_INFINITY ? 0 : 1);

        return ImagePainter.hsb(h, s, b, o);

    }

    /**
     * ImagePainter that produces a image in a "historic" realistic style.
     * 
     * @param panorama
     *          a Panorama
     * @return a ImagePainter
     */
    public static ImagePainter photorealisticHisto(Panorama panorama) {
        return (x, y) -> photorealistic(panorama).sky(1).colorAt(x, y)
                .deriveColor(330, 0.5, 1, 1);
    }

    /**
     * ImagePainter that produces a image with realistic colors and blue sky.
     * 
     * @param panorama
     *          a Panorama
     * @return a ImagePainter
     */
    public static ImagePainter photorealisticBlue(Panorama panorama) {
        return photorealistic(panorama).sky(0);
    }

    /**
     * ImagePainter that produces a image of the landscape at night.
     * 
     * @param panorama
     *          a Panorama
     * @return a ImagePainter
     */
    public static ImagePainter photorealisticNight(Panorama panorama) {
        
        return ((ImagePainter) ((x, y) -> photorealistic(panorama).colorAt(x, y)
                .deriveColor(0, 0.5, 0.4, 1)
                .interpolate(Color.hsb(200, 1, 0.3, 1), 0.3))).sky(2);
    }

    /**
     * ImagePainter (Filter) that adds a sky (different colors depending on the given argument)..
     * 
     * @param mode
     *          mode (indicates wanted sky color)
     * @return a ImagePainter
     */
    public default ImagePainter sky(int mode) {

        return (x, y) -> {
            if (this.colorAt(x, y).getOpacity() < 1) {
                if (mode == 0)

                    return Color.hsb(180, 0.2, 1, 1);

                if (mode == 1)
                    return Color.hsb(70, 0.2, 1, 1);

                if (mode == 2) {
                    if (Math.random() < 0.9985)
                        return Color.hsb(70, 0, 0, 1);

                    return Color.hsb(70, 0, 1, 1);
                }
            }
            return this.colorAt(x, y);
        };

    }

}
