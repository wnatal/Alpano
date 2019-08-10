package ch.epfl.alpano;

import java.util.Objects;

/**
 * This class represents a two-dimensional interval (of integers) and provides
 * methods to work with it.
 * 
 * @author Natal Willisch (262092)
 *
 */
public final class Interval2D {
    final private Interval1D iX, iY;

    /**
     * Creates a 2-dimensional interval from two one-dimensional intervals.
     * 
     * @param iX
     *            first interval (x-direction)
     * @param iY
     *            second interval (y-direction)
     */
    public Interval2D(Interval1D iX, Interval1D iY) {
        
        this.iX = Objects.requireNonNull(iX);
        this.iY = Objects.requireNonNull(iY);
        
    }

    /**
     * Returns the interval in x direction.
     * 
     * @return interval in x direction
     */
    public Interval1D iX() {
        
        return iX;
        
    }

    /**
     * Returns the interval in y direction.
     * 
     * @return interval in y direction
     */
    public Interval1D iY() {
        
        return iY;
        
    }

    /**
     * Tells if a value (x, y) is included in the interval.
     * 
     * @param x
     *            x-component of the value
     * @param y
     *            y-component of the value
     * @return returns true if included, else false
     */
    public boolean contains(int x, int y) {
        
        return (iX.contains(x) && iY.contains(y));
        
    }

    /**
     * Returns the size (area) of the interval.
     * 
     * @return the size of the interval
     */
    public int size() {
        
        return iX.size() * iY.size();
        
    }

    /**
     * Returns the size (area) of the intersection between this and another
     * interval.
     * 
     * @param that
     *            the other intersection
     * @return the size of the intersection
     */
    public int sizeOfIntersectionWith(Interval2D that) {
        
        return iX.sizeOfIntersectionWith(that.iX)
                * iY.sizeOfIntersectionWith(that.iY);
        
    }

    /**
     * Creates the smallest interval that contains this and an other interval,
     * called bounding-union.
     * 
     * @param that
     *            the other interval
     * @return the bounding-union
     */
    public Interval2D boundingUnion(Interval2D that) {
        
        return new Interval2D(iX.boundingUnion(that.iX),
                iY.boundingUnion(that.iY));
        
    }

    /**
     * Checks if this interval is unnionable with another interval.
     * 
     * @param that
     *            the other interval
     * @return true if unionable, else false
     */
    public boolean isUnionableWith(Interval2D that) {
        
        return (size() + that.size()
                - sizeOfIntersectionWith(that) == boundingUnion(that).size());
        
    }

    /**
     * Creates an union of this and another interval if it is unionable.
     * 
     * @param that
     *            the other interval
     * @return the union
     * @throws IllegalArgumentException
     *             if the intervals are not unionable
     */
    public Interval2D union(Interval2D that) {
        
        Preconditions.checkArgument(isUnionableWith(that),
                "intervals are not unionable");
        
        return boundingUnion(that);
        
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object that0) {
        
        if (that0 instanceof Interval2D) {
            Interval2D that = (Interval2D) that0;
            
            if (iX.equals(that.iX) && iY.equals(that.iY))
                return true;
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
        
        return Objects.hash(iX, iY);
        
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        
        return iX.toString() + 'x' + iY.toString();
        
    }

}
