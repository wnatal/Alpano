package ch.epfl.alpano;

import java.util.Locale;

import static java.lang.Math.*;
import static ch.epfl.alpano.Math2.haversin;
import static ch.epfl.alpano.Preconditions.checkArgument;

/**
 * The objects of this class represent a geo-location (longitude/latitude) and
 * the class provides functions to find the relative position between two of
 * this objects.
 * 
 * @author Natal Willisch (262092)
 * 
 */
public final class GeoPoint {

    private final double longitude;
    private final double latitude;

    /**
     * The constructor checks if the parameters respect the ranch of a longitude
     * respectively a latitude.
     * 
     * @param longitude
     *            an angle in radians between -Pi and Pi (included both of them)
     * @param latitude
     *            an angle in radians between -Pi/2 and Pi/2 (included both of
     *            them)
     * @throws IllegalArgumentException
     *             if one of the both parameters doesn't respect the boundaries
     */
    public GeoPoint(double longitude, double latitude) {

        checkArgument(isInSymetricRange(longitude, PI),
                "Longitude out of range!");
        checkArgument(isInSymetricRange(latitude, scalb(PI, -1)),
                "Latitude out of range!");

        this.longitude = longitude;
        this.latitude = latitude;

    }

    /**
     * Getter for the longitude.
     * 
     * @return the longitude
     */
    public double longitude() {

        return longitude;

    }

    /**
     * Getter for the latitude.
     * 
     * @return the latitude
     */
    public double latitude() {

        return latitude;

    }

    /**
     * Calculates the distance in meters of two geo-locations.
     * 
     * @param that
     *            second {@link GeoPoint}
     * @return distance in meters
     */
    public double distanceTo(GeoPoint that) {

        return Distance
                .toMeters(2 * asin(sqrt(haversin(latitude() - that.latitude())
                        + cos(latitude()) * cos(that.latitude())
                                * haversin(longitude() - that.longitude()))));

    }

    /**
     * Determines the azimuth from THIS point to an other geo-location (so in
     * other words the direction where the other location can be found).
     * 
     * @param that
     *            the other geo-location
     * @return the direction expressed as azimuth
     */
    public double azimuthTo(GeoPoint that) {

        double cosThatLat = cos(that.latitude());
        double difference = longitude() - that.longitude();
        double ratio_num = sin(difference) * cosThatLat;
        double ratio_denum = (cos(latitude()) * sin(that.latitude())
                - sin(latitude()) * cosThatLat * cos(difference));

        return Azimuth
                .fromMath(Azimuth.canonicalize(atan2(ratio_num, ratio_denum)));

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        Locale l = null;

        return String.format(l, "(%.4f; %.4f)", Math.toDegrees(longitude()), Math.toDegrees(latitude()));

    }

    /**
     * Checks if the argument {@code x} is between -{@code bound} and
     * +{@code bound}
     */
    private boolean isInSymetricRange(double x, double bound) {

        return (x >= -bound && x <= bound);

    }
}
