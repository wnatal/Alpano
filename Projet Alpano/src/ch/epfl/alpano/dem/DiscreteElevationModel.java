package ch.epfl.alpano.dem;

import ch.epfl.alpano.GeoPoint;
import ch.epfl.alpano.Interval2D;

/**
 * From a discrete elevation Model
 * {@link ch.epfl.alpano.dem.DiscreteElevationModel} this class creates a
 * continuous elevation model. So the model is not anymore only accessible by a
 * index (integer values), the model is now accessible by a
 * ({@link GeoPoint}) and values that are not in the discrete elevation model
 * will be interpolated.
 * 
 * @author Natal Willisch (262092)
 *
 */
public interface DiscreteElevationModel extends AutoCloseable {
    
    /**
     * Samples per degree is 3600, so one sample per second
     */
    public static final int SAMPLES_PER_DEGREE = 3600;
    /**
     * Samples per radian
     */
    public static final double SAMPLES_PER_RADIAN = SAMPLES_PER_DEGREE * 180
            / Math.PI;

    /**
     * Transforms an angle in an index.
     * 
     * @param angle
     *            angle in radians
     * @return index
     */
    public static double sampleIndex(double angle) {
        
        return SAMPLES_PER_RADIAN * angle;
        
    }

    /**
     * Returns the 2D interval covered by the model.
     * 
     * @return covered interval
     */
    public Interval2D extent();

    /**
     * Returns the elevation at a sample-index.
     * 
     * @param x
     *            index in west-east direction
     * @param y
     *            index in south-north direction
     * @return the elevation at the sample-index
     */
    public double elevationSample(int x, int y);

    /**
     * Retruns the union between itself and another
     * {@link DiscreteElevationModel}.
     * 
     * @param that
     *            the other {@link DiscreteElevationModel}
     * @return the united model
     */
    public default DiscreteElevationModel union(DiscreteElevationModel that) {
        
        return new CompositeDiscreteElevationModel(this, that);
        
    }

}
