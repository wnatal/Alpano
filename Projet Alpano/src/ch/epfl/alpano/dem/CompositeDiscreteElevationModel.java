package ch.epfl.alpano.dem;

import java.util.Objects;

import ch.epfl.alpano.Interval2D;
import ch.epfl.alpano.Preconditions;

/**
 * This class that is itself a DiscreteEleationModel takes two discrete
 * elevation models and unions them to one.
 * 
 * @author Natal Willisch (262092)
 *
 */
public final class CompositeDiscreteElevationModel implements DiscreteElevationModel {
    
    private final DiscreteElevationModel dem1, dem2;
    private final Interval2D composite;

    /**
     * This Constructor takes two discrete elevation models the create one or
     * throws an exception if the the models are not unionable.
     * 
     * @param dem1
     *            a discrete elevation model
     * @param dem2
     *            another (compatible) discrete elevation model
     * @throws NullPointerException
     *             if one of the objects is null
     * @throws IllegalArgumentException
     *             if the models are not unionable, see
     *             {@link ch.epfl.alpano.Interval2D.union(Interval2D that)} for
     *             detailed informations
     */
    CompositeDiscreteElevationModel(DiscreteElevationModel dem1,
            DiscreteElevationModel dem2) {
        
        this.dem1 = Objects.requireNonNull(dem1);
        this.dem2 = Objects.requireNonNull(dem2);
        composite = dem1.extent().union(dem2.extent());
        
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.AutoCloseable#close()
     */
    @Override
    public void close() throws Exception {
        
        dem1.close();
        dem2.close();
        
    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.epfl.alpano.dem.DiscreteElevationModel#extent()
     */
    @Override
    public Interval2D extent() {
        
        return composite;
        
    }

    /**
     * @see ch.epfl.alpano.dem.DiscreteElevationModel#elevationSample(int, int)
     * @throws IllegalArgumentException
     *             if not a valid sample index
     */
    @Override
    public double elevationSample(int x, int y) {
        
        Preconditions.checkArgument(composite.contains(x, y));

        if (dem1.extent().contains(x, y)){
            return dem1.elevationSample(x, y);
        }
        else{
            return dem2.elevationSample(x, y);
        }
        
    }

}
