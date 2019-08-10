package ch.epfl.alpano.gui;

import java.util.EnumMap;
import java.util.Map;

import ch.epfl.alpano.GeoPoint;
import ch.epfl.alpano.PanoramaParameters;

/**
 * Represents the panorama parameters in a way that they are near to the form
 * they will be shown to the user.
 * 
 * @author Natal Willisch (262092)
 *
 */
public final class PanoramaUserParameters {

    private final Map<UserParameter, Integer> parameters = new EnumMap<>(
            UserParameter.class);

    private static final int MAX_VERTICAL_FIELD_OF_VIEW = 170;

    /**
     * Construct the PanoramaUserParameters from a map of parameters.
     * 
     * @param parameters
     *            the map of parameters
     */
    public PanoramaUserParameters(Map<UserParameter, Integer> parameters) {

        for (UserParameter d : UserParameter.values()) {
            this.parameters.put(d, d.sanitize(parameters.get(d)));
        }

        this.parameters.put(UserParameter.HEIGHT, Math.min(
                UserParameter.HEIGHT
                        .sanitize(parameters.get(UserParameter.HEIGHT)),
                MAX_VERTICAL_FIELD_OF_VIEW
                        * (this.parameters.get(UserParameter.WIDTH) - 1)
                        / this.parameters.get(UserParameter.HORIZONTAL_FIELD_OF_VIEW)
                        + 1));

    }

    /**
     * Construct the PanoramaUserParameters from the given arguments.
     * 
     * @param longitude
     *            the longitude
     * @param latitude
     *            the latitude
     * @param elevation
     *            the elevation
     * @param centerAzimuth
     *            the azimuth of the center
     * @param horizontalFieldOfView
     *            the horizontal field of view
     * @param maxDistance
     *            the maximal distance
     * @param width
     *            the width of the panorama
     * @param height
     *            the height of the panorama
     * @param superSamplingExponent
     *            the quality factor for the super sampling
     */
    public PanoramaUserParameters(int longitude, int latitude, int elevation,
            int centerAzimuth, int horizontalFieldOfView, int maxDistance,
            int width, int height, int superSamplingExponent) {

        this(createParameters(longitude, latitude, elevation, centerAzimuth,
                horizontalFieldOfView, maxDistance, width, height,
                superSamplingExponent));

    }

    /**
     * Returns the value to the asked UserParameter.
     * 
     * @param parameter
     *            an UserParameter
     * @return the value of the UserParameter for this panorama
     */
    public int get(UserParameter parameter) {

        return this.parameters.get(parameter);

    }

    /**
     * Returns a integer that correspond to the longitude.
     * 
     * @return longitude (as integer)
     */
    public int observerLongitude() {

        return parameters.get(UserParameter.OBSERVER_LONGITUDE);

    }

    /**
     * Returns a integer that correspond to the latitude.
     * 
     * @return latitude (as integer)
     */
    public int observerLatitude() {

        return parameters.get(UserParameter.OBSERVER_LATITUDE);

    }

    /**
     * Returns the elevation of the observer.
     * 
     * @return elevation
     */
    public int observerElevation() {

        return parameters.get(UserParameter.OBSERVER_ELEVATION);

    }

    /**
     * Returns the azimuth of the panorama's center.in degrees.
     * 
     * @return azimuth of the panorama's center
     */
    public int centerAzimuth() {

        return parameters.get(UserParameter.CENTER_AZIMUTH);

    }

    /**
     * Returns the horizontal field of view in degrees.
     * 
     * @return horizontal field of view
     */
    public int horizontalFieldOfView() {

        return parameters.get(UserParameter.HORIZONTAL_FIELD_OF_VIEW);

    }

    /**
     * Returns the maximal distance in km.
     * 
     * @return maximal distance
     */
    public int maxDistance() {

        return parameters.get(UserParameter.MAX_DISTANCE);

    }

    /**
     * Returns the width of the panorama.
     * 
     * @return width
     */
    public int width() {

        return parameters.get(UserParameter.WIDTH);

    }

    /**
     * Returns the height of the panorama.
     * 
     * @return height
     */
    public int height() {

        return parameters.get(UserParameter.HEIGHT);

    }

    /**
     * Returns the super-sampling-Exponent.
     * 
     * @return superSamplingExponent
     */
    public int superSamplingExponent() {

        return parameters.get(UserParameter.SUPER_SAMPLING_EXPONENT);

    }

    /**
     * Returns the parameters in form of a PanoramaParameters-object, taking
     * into account the supersampling-factor. (the panorama, how it is calculated)
     * 
     * 
     * @return a PanoramaParameters-object
     */
    public PanoramaParameters panoramaParameters() {

        return panoramaParameters(superSamplingExponent());

    }

    /**
     * Returns the parameters in form of a PanoramaParameters-object (not taking
     * into account the superSamplingExponent -> the panorama, how it is shown)
     * 
     * @return a PanoramaParameters-object
     */
    public PanoramaParameters panoramaDisplayParameters() {

        return panoramaParameters(0);

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object that0) {

        if (that0 instanceof PanoramaUserParameters) {
            PanoramaUserParameters that = (PanoramaUserParameters) that0;

            return parameters.equals(that.parameters);
        }

        return false;

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        return parameters.hashCode();

    }

    /**
     * maps a set of values to the userparameters and returns a map with the
     * enum UserParameter as key.
     */
    private static Map<UserParameter, Integer> createParameters(int... values) {

        Map<UserParameter, Integer> build = new EnumMap<>(UserParameter.class);

        for (UserParameter d : UserParameter.values()) {
            build.put(d, values[d.ordinal()]);
        }

        return build;

    }

    private double toRadiansAdaptation(int val) {

        return Math.toRadians(val / 10000.0);

    }

    private PanoramaParameters panoramaParameters(int scalbFactor) {

        return new PanoramaParameters(
                new GeoPoint(toRadiansAdaptation(observerLongitude()),
                        toRadiansAdaptation(observerLatitude())),
                observerElevation(), Math.toRadians(centerAzimuth()),
                Math.toRadians(horizontalFieldOfView()), 1000 * maxDistance(),
                (int) Math.scalb(width(), scalbFactor),
                (int) Math.scalb(height(), scalbFactor));

    }
}
