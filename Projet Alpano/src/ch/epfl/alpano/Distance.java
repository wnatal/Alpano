package ch.epfl.alpano;

/**
 * provides transformations of distances
 * 
 * @author Natal Willisch (262092)
 * 
 */
public interface Distance {
    
    /**
     * Radius of the earth (6371000 m)
     */
    public static final double EARTH_RADIUS = 6371000;

    /**
     * Transforms a distance from meters to radians.
     * 
     * @param distanceInMeters
     *            a distance in meters (on the surface of a earth-sized sphere)
     * @return the distance in radians
     */
    public static double toRadians(double distanceInMeters) {

        return distanceInMeters / EARTH_RADIUS;

    }

    /**
     * Transforms a distance from radians to meters.
     * 
     * @param distanceInRadians
     *            a distance in radians (on the surface of a earth-sized sphere)
     * @return the distance in meters
     */
    public static double toMeters(double distanceInRadians) {

        return distanceInRadians * EARTH_RADIUS;

    }

}
