package ch.epfl.alpano;

import java.util.Objects;

/**
 * This class represents an one-dimensional interval (of integers) and provides
 * methods to work with it.
 * 
 * @author Natal Willisch (262092)
 *
 */
public final class Interval1D {

    private final int includedFrom;
    private final int includedTo;

    /**
     * The constructor creates a new instance if the parameters represent a
     * valid interval.
     * 
     * @param includedFrom
     *            lower bound of the interval
     * @param includedTo
     *            upper bound of the interval
     * @throws IllegalArgumentException
     *             if the lower bound is bigger than the upper bound
     */
    public Interval1D(int includedFrom, int includedTo) {

        Preconditions.checkArgument(includedFrom <= includedTo,
                "lower bound is bigger than upper bound");

        this.includedFrom = includedFrom;
        this.includedTo = includedTo;

    }

    /**
     * Getter for the lower bound of the interval.
     * 
     * @return lower bound
     */
    public int includedFrom() {

        return includedFrom;

    }

    /**
     * Getter for the upper bound of the interval.
     * 
     * @return upper bound
     */
    public int includedTo() {

        return includedTo;

    }

    /**
     * Tells if a value is included in the interval.
     * 
     * @param v
     *            value
     * @return returns true if included, else false
     */
    public boolean contains(int v) {

        return (v >= includedFrom() && v <= includedTo());

    }

    /**
     * Returns the size of the intervals
     * 
     * @return the size of the interval
     */
    public int size() {

        return includedTo() - includedFrom() + 1;

    }

    /**
     * Returns the size of the intersection between itself an an other interval.
     * 
     * @param that
     *            the other interval
     * @return size of the intersection
     */
    public int sizeOfIntersectionWith(Interval1D that) {

        int intervalFrom = includedFrom() > that.includedFrom() ? includedFrom()
                : that.includedFrom();
        int intervalTo = includedTo() > that.includedTo() ? that.includedTo()
                : includedTo();

        if (intervalTo < intervalFrom)
            return 0;
        return intervalTo - intervalFrom + 1;

    }

    /**
     * Returns the smallest interval that includes itself and another interval,
     * called bounding union.
     * 
     * @param that
     *            the other interval
     * @return the bounding union
     */
    public Interval1D boundingUnion(Interval1D that) {
        
        int intervalFrom = includedFrom() < that.includedFrom() ? includedFrom()
                : that.includedFrom();
        int intervalTo = includedTo() < that.includedTo() ? that.includedTo()
                : includedTo();
  
        return new Interval1D(intervalFrom, intervalTo);
        
    }

    /**
     * Checks if two intervals are unionable: a interval with only two (upper
     * and lower) bounds.
     * 
     * @param that
     *            an other interval
     * @return true if unionable, else false
     */
    public boolean isUnionableWith(Interval1D that) {
        
        return (size() + that.size()
                - sizeOfIntersectionWith(that) == boundingUnion(that).size());
        
    }

    /**
     * If the interval and another are unionable this method creates the
     * corresponding new interval (the union).
     * 
     * @param that
     *            the other interval
     * @return the union of the two intervals
     * @throws IllegalArgumentException
     *             if not unionable, see {@link isUnionableWith}
     */
    public Interval1D union(Interval1D that) {
        
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
        
        if (that0 instanceof Interval1D) {
            Interval1D that = (Interval1D) that0;
            
            if (that.includedFrom() == includedFrom()
                    && that.includedTo() == includedTo())
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
        
        return Objects.hash(includedFrom(), includedTo());
        
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        
        return ("[" + includedFrom() + ".." + includedTo() + "]");
        
    }

}
