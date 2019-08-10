package ch.epfl.alpano;

import static ch.epfl.alpano.Preconditions.checkArgument;

/**
 * This interface provides transformations and checks for an azimuth
 * 
 * @author Natal Willisch (262092)
 * 
 */
public interface Azimuth {
    
    /**
     * only local use
     */
    final static double HALF_OCTANT = Math.PI / 8;

    /**
     * Checks if a angle (more specific a azimuth) is canonicals, that means
     * positive and smaller 2PI.
     * 
     * @param azimuth
     *            a angle in radians
     * @return true if {@code azimuth} is canonical (>=0, <2pi), otherwise false
     */
    public static boolean isCanonical(double azimuth) {

        if (azimuth >= 0 && azimuth < Math2.PI2)
            return true;
        return false;

    }

    /**
     * Canonicalizes a angle/azimuth (in radians): borders the angle between 0
     * and 2*PI.
     * 
     * @param azimuth
     *            a angle in radians
     * @return canonicalized {@code azimuth}
     */
    public static double canonicalize(double azimuth) {

        return Math2.floorMod(azimuth, Math2.PI2);

    }

    /**
     * Transforms a angle defined in a clockwise way (like a azimuth) in a
     * anticlockwise/mathematical-defined angle.
     * 
     * @param azimuth
     *            an angle in radians and canonical (positive and smaller than
     *            2PI)
     * @return angle defined in the mathematical sense
     * @throws IllegalArgumentException
     *             if {@code azimuth} is not canonical
     */
    public static double toMath(double azimuth) {

        checkCanonical(azimuth, "Azimuth is not canonical");

        if (azimuth == 0)
            return 0;

        return Math2.PI2 - azimuth;

    }

    /**
     * Transforms a anticlockwise/mathematical-defined angle in a clockwise
     * defined angle/azimuth.
     * 
     * @param angle
     *            a angle in radians and canonical (positive and smaller than
     *            2PI)
     * @return azimuth/angle defined clockwise
     * @throws IllegalArgumentException
     *             if {@code angle} is not canonical
     */
    public static double fromMath(double angle) {

        checkCanonical(angle, "angle is not canonical");

        if (angle == 0)
            return 0;

        return Math2.PI2 - angle;

    }

    /**
     * Produces a string from the parameters depending on an angle. Composed
     * strings show first the {@code n} or {@code s} string and then the
     * {@code e} or {@code w} value.
     * 
     * @param azimuth
     *            angle/azimuth in radians
     * @param n
     *            String shown for angles smaller 67.5° and bigger 295.5°
     * @param e
     *            String shown for angles between 22.5° and 157.5°
     * @param s
     *            String shown for angles smaller 112.5° and 247.5°
     * @param w
     *            String shown for angles smaller 202.5° and 337.5°
     * @return a String containing one or two of the parameter-strings depending
     *         on the azimuth
     * @throws IllegalArgumentException
     *             if {@code azimuth} is not canonical
     */
    public static String toOctantString(double azimuth, String n, String e,
            String s, String w) {

        checkCanonical(azimuth, "azimuth is not canonical");

        String result = "";

        if (azimuth > 13 * HALF_OCTANT || azimuth < 3 * HALF_OCTANT)
            result = n;
        else if (azimuth > 5 * HALF_OCTANT && azimuth < 11 * HALF_OCTANT)
            result = s;

        if (azimuth > HALF_OCTANT && azimuth < 7 * HALF_OCTANT)
            result = result + e;
        else if (azimuth > 9 * HALF_OCTANT && azimuth < 15 * HALF_OCTANT)
            result = result + w;

        return result;

    }

    /**
     * Throws an exception if the argument is not canonical (expressed in
     * radians)
     * 
     * @param azimuth
     *            an angle in radians
     * @throws IllegalArgumentException
     *             if not canonical
     */
    static void checkCanonical(double azimuth, String message) {

        checkArgument(isCanonical(azimuth), message);

    }
}
