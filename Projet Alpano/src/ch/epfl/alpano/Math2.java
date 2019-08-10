package ch.epfl.alpano;

import static java.lang.Math.PI;

import java.util.function.DoubleUnaryOperator;

import static ch.epfl.alpano.Preconditions.checkArgument;

/**
 * This class provides basic, often-used calculations.
 * 
 * @author Natal Willisch (262092)
 * 
 */
public interface Math2 {
    
    /**
     * The double of {@link java.lang.Math#PI}
     */
    public static final double PI2 = 2 * PI;

    /**
     * Calculates the square of a number.
     * 
     * @param reel
     *            number
     * @return square of {@code reel}
     */
    public static double sq(double x) { 
        
        return x * x;
        
    }

    /**
     * Returns the floored modulo of an numerator and a denominator.
     * 
     * @param x
     *            numerator
     * @param y
     *            denominator
     * @return returns the floored modulo
     * @throws IllegalArgumentException
     *             if the denominator is 0
     */
    public static double floorMod(double x, double y) {
        
        checkArgument(y != 0, "modulo over 0");
        
        return (x - y * Math.floor(x / y));
        
    }

    /**
     * Calculates the haversin of a number.
     * 
     * @param x
     *            a real number
     * @return haversin of {@code x}
     */
    public static double haversin(double x) {
        
        return sq(Math.sin(x / 2));
        
    }

    /**
     * Calculates the angular distance between two given angles result is
     * between -PI and +PI.
     * 
     * @param a1
     *            first angle
     * @param a2
     *            second angle
     * @return angle between the given angles (from the first angle to the
     *         second angle)
     */
    public static double angularDistance(double a1, double a2) {
        
        return floorMod((a2 - a1 + PI), PI2) - PI;
        
    }

    /**
     * Linear interpolation (1-dimensional)
     * 
     * @param y0
     *            first reference value (at x = 0)
     * @param y1
     *            second reference value (at x = 1)
     * @param x
     *            relative position between the two reference points
     * @return the interpolated value
     */
    public static double lerp(double y0, double y1, double x) {
        
        return y0 + (y1 - y0) * x;
        
    }

    /**
     * Linear interpolation (2-dimensional)
     * 
     * @param z00
     *            reference value at (0,0)
     * @param z10
     *            reference value at (1,0)
     * @param z01
     *            reference value at (0,1)
     * @param z11
     *            reference value at (1,1)
     * @param x
     *            relative position in x direction (means (x,.) )
     * @param y
     *            relative position in y direction (means (.,y) )
     * @return the interpolated value at position (x,y)
     */
    public static double bilerp(double z00, double z10, double z01, double z11,
            double x, double y) {
        
        double z1 = lerp(z00, z10, x);
        double z2 = lerp(z01, z11, x);
        
        return lerp(z1, z2, y);
        
    }

    /**
     * Returns the smaller bound of the first interval of size dX (within the
     * given borders) that contains a zero (f(x) = 0) or returns
     * {@code Double.POSITIVE_INFINITY} if no zero is found.
     * 
     * @param f
     *            the function for which this method searches for a zero
     * @param minX
     *            the lower border
     * @param maxX
     *            the upper border
     * @param dX
     *            the interval-size for which we decide if it includes a zero
     * @return the lower bound of the first interval that includes a zero or
     *         returns {@code Double.POSITIVE_INFINITY}
     */
    public static double firstIntervalContainingRoot(DoubleUnaryOperator f,
            double minX, double maxX, double dX) {
        
        double next;
        double resultLow = f.applyAsDouble(minX);
        
        while (minX + dX < maxX) {
            next = minX + dX;
            if (resultLow * (resultLow = f.applyAsDouble(next)) <= 0) {
                return minX;
            }
            minX = next;
        }
        return Double.POSITIVE_INFINITY;
        
    }

    /**
     * Returns the lower bound of an interval that contains the zero or throws
     * an error if the function doesn't contains a zero within the borders.
     * 
     * @param f
     *            the function for which this method locates the zero
     * @param x1
     *            the lower border
     * @param x2
     *            the upper border
     * @param epsilon
     *            maximal size of the interval that contains the zero (~maximal
     *            error)
     * @return lower bound of the epsilon-interval that contains the zero
     * @throws IllegalArgumentException
     *             if f(x1)*f(x2) >= 0 (~interval doesn't contain a zero)
     */
    public static double improveRoot(DoubleUnaryOperator f, double x1,
            double x2, double epsilon) {
        
        double xm, resultXM, resultX1;
        
        checkArgument(
                (resultX1 = f.applyAsDouble(x1)) * f.applyAsDouble(x2) <= 0,
                "this interval contains no zero");
        
        while (x2 - x1 > epsilon) {
            xm = Math.scalb(x1 + x2, -1);
            resultXM = f.applyAsDouble(xm);
            if (resultXM == 0) {
                return xm;
            } else if (resultXM * resultX1 < 0) {
                x2 = xm;
            } else {
                x1 = xm;
                resultX1 = resultXM;
            }
        }
        return x1;
        
    }
}
