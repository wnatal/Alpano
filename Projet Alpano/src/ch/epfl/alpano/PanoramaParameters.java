package ch.epfl.alpano;

import java.util.Objects;
import static ch.epfl.alpano.Preconditions.*;

/**
 * This class is there to store the general information of a panorama. There are
 * methodes that transform view angles into position in the panorama and the
 * other way around.
 * 
 * @author Natal Willisch (262092)
 *
 */
public final class PanoramaParameters {
    
    private final GeoPoint observerPosition;
    private final double centerAzimuth, horizontalFieldOfView,
            verticalFieldOfView;
    private final int observerElevation, maxDistance, width, height;
    private final int reducedWith, reducedHeight;

    /**
     * The Constructor takes all parameters to define a view (=panorama).
     * 
     * @param observerPosition
     *            the position of the observer
     * @param observerElevation
     *            the elevation of the observer
     * @param centerAzimuth
     *            the direction of the view (central point of the panorama) as
     *            an azimuth
     * @param horizontalFieldOfView
     *            the horizontal field of view
     * @param maxDistance
     *            the maximal distance to which things are "visible" in the
     *            panorama
     * @param width
     *            the width of the panorama
     * @param height
     *            the height of the panorama
     * @throws NullPointerException
     *             if {@code oberverPosition} is null
     * @throws IllegalArgumentException
     *             if the azimuth is not canonical or the horizontal field of
     *             view is not bigger 0 and smaller equal 2PI or the maximal
     *             distance or the dimensions of the panorama are not stictly
     *             positive.
     */
    public PanoramaParameters(GeoPoint observerPosition, int observerElevation,
            double centerAzimuth, double horizontalFieldOfView, int maxDistance,
            int width, int height) {
        
        this.observerPosition = Objects.requireNonNull(observerPosition,
                "observerPosition is null");
        
        checkArgument(Azimuth.isCanonical(centerAzimuth),
                "Azimuth is not canonical");
        checkArgument(
                horizontalFieldOfView > 0 && horizontalFieldOfView <= Math2.PI2,
                "HorizontalFieldOfView is out of range");
        checkArgument(maxDistance > 0,
                "the maximal Distance is not strictly positive");
        checkArgument(width > 0 && height > 0,
                "the dimensions or not strictly positive");
        
        this.observerElevation = observerElevation;
        this.centerAzimuth = centerAzimuth;
        this.horizontalFieldOfView = horizontalFieldOfView;
        this.maxDistance = maxDistance;
        this.width = width;
        reducedWith = width - 1;
        this.height = height;
        reducedHeight = height - 1;
        
        if (width != 1)
            verticalFieldOfView = horizontalFieldOfView * (height - 1.)
                    / (width - 1.);
        else
            verticalFieldOfView = horizontalFieldOfView;
        
    }

    /**
     * Returns the position of the observer.
     * 
     * @return position of the observer
     */
    public GeoPoint observerPosition() {
        
        return observerPosition;
        
    }

    /**
     * Returns the central azimuth.
     * 
     * @return central azimuth
     */
    public double centerAzimuth() {
        
        return centerAzimuth;
        
    }

    /**
     * Returns the horizontal field of view.
     * 
     * @return horizontal field of view
     */
    public double horizontalFieldOfView() {
        
        return horizontalFieldOfView;
        
    }

    /**
     * Returns the elevation of the observer.
     * 
     * @return elevation of the observer
     */
    public int observerElevation() {
        
        return observerElevation;
        
    }

    /**
     * Returns the maximal visible distance.
     * 
     * @return maximal distance
     */
    public int maxDistance() {
        
        return maxDistance;
        
    }

    /**
     * Returns the width of the panorama.
     * 
     * @return width
     */
    public int width() {
        
        return width;
        
    }

    /**
     * Returns the height of the panorama.
     * 
     * @return height
     */
    public int height() {
        
        return height;
        
    }

    /**
     * Returns the vertical field of view.
     * 
     * @return vertical field of view
     */
    public double verticalFieldOfView() {
        
        return verticalFieldOfView;
        
    }

    /**
     * Transforms a horizontal (x) index into azimuth (direction of the point in
     * the panorama).
     * 
     * @param x
     *            horizontal position/index
     * @return azimuth
     * @throws IllegalArgumentException
     *             if the {@code x} is out of the boundaries of the panorama
     */
    public double azimuthForX(double x) {
        
        checkArgument(isValidIndex(x, width),
                "x index is not in the panorama!");
        
        return Azimuth.canonicalize(centerAzimuth
                + horizontalFieldOfView * (x / reducedWith - 0.5));
        
    }

    /**
     * Transforms a azimuth into the corresponding horizontal (x) index of the
     * panorama.
     * 
     * @param a
     *            azimuth
     * @return horizontal position/index
     * @throws IllegalArgumentException
     *             if the azimuth is not covered by the panorama
     */
    public double xForAzimuth(double a) {
        
        double delta = Math2.angularDistance(centerAzimuth, a);
        double x = reducedWith * (delta / horizontalFieldOfView + 0.5);
        
        checkArgument(isValidIndex(x, width),
                "Azimuth is not covered by the panorama!");
        
        return x;
        
    }

    /**
     * Transforms a vertical (y) index into a corresponding altitude (an angle
     * in radians).
     * 
     * @param y
     *            vertical position/index
     * @return altitude
     * @throws IllegalArgumentException
     *             if {@code y} is out of the boundaries of the panorama
     */
    public double altitudeForY(double y) {
        
        checkArgument(isValidIndex(y, height),
                "y index is not in the panorama!");
        
        return verticalFieldOfView * (0.5 - y / reducedHeight);
        
    }

    /**
     * Transforms a altitude (an angle in radians) into the corresponding
     * vertical (y) index of the panorama.
     * 
     * @param a
     *            altitude
     * @return vertical position/index
     * @throws IllegalArgumentException
     *             if the altitude is not covered by the panorama
     */
    public double yForAltitude(double a) {
        
        double y = reducedHeight * (0.5 - a / verticalFieldOfView);
        
        checkArgument(isValidIndex(y, height),
                "Altitude is not covered by the panorama!");
        
        return y;
        
    }

    /**
     * Checks if a sample index (x,y) represents a sample in the panorama (means
     * xy-index is within the boundaries of the panorama).
     * 
     * @param x
     *            horizontal position/index
     * @param y
     *            vertical position/index
     * @return true if the sample index is in the boundaries, else false
     */
    boolean isValidSampleIndex(int x, int y) {
        
        return (isValidIndex(x, width) && isValidIndex(y, height));
        
    }

    /**
     * Transforms a two dimensional (x, y) sample index in a linear index
     * (unique for values within the panorama boundaries).
     * 
     * @param x
     *            horizontal position/index
     * @param y
     *            vertical position/index
     * @return linear sample index
     */
    public int linearSampleIndex(int x, int y) { 
        
        return width * y + x;
        
    }

    // private

    private boolean isValidIndex(double val, int ref) {
        
        return (val >= 0 && val <= ref - 1);
        
    }

}
