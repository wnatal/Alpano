package ch.epfl.alpano;

import java.util.Arrays;
import java.util.Objects;

/**
 * A instance of this class represents a panorama organized in form of samples
 * in which all relevant information are saved and are accessible. As the class
 * itself is immutable, there is a subclass that gives the possibility to build
 * up a panorama step by step.
 * 
 * @author Natal Willisch (262092)
 *
 */
public final class Panorama {
    
    final private PanoramaParameters parameters;
    final private float[] distance;
    final private float[] longitude;
    final private float[] latitude;
    final private float[] elevation;
    final private float[] slope;

    /**
     * The constructor takes the general informations about the panorama and the
     * informations of the samples in form of tables (every index is assign to a
     * single sample).
     * 
     * @param parameters
     *            general parameters/informations of the panorama
     * @param distance
     *            table of distances between observer and the represented points
     *            in landscape by the samples
     * @param longitude
     *            table of longitudes
     * @param latitude
     *            table of latitudes
     * @param elevation
     *            table of elevations
     * @param slope
     *            table of the slopes
     */
    private Panorama(PanoramaParameters parameters, float[] distance,
            float[] longitude, float[] latitude, float[] elevation,
            float[] slope) {
        
        this.parameters = parameters;
        this.distance = distance;
        this.longitude = longitude;
        this.latitude = latitude;
        this.elevation = elevation;
        this.slope = slope;
        
    }

    /**
     * Returns the general parameters of the panorama.
     * 
     * @return parameters
     */
    public PanoramaParameters parameters() {
        
        return parameters;
        
    }

    /**
     * Returns the distance from the observer to a point represented by a sample
     * or throws an exception if the xy-index doesn't correspond to a sample in
     * the panorama.
     * 
     * @param x
     *            horizontal position (of the xy-index)
     * @param y
     *            vertical position
     * @return the distance
     * @throws IndexOutOfBoundsException
     *             if the xy-index is out of the bounds of the panorama
     */
    public float distanceAt(int x, int y) {
        
        checkValidSampleIndex(x, y);
        
        return distance[parameters.linearSampleIndex(x, y)];
        
    }

    /**
     * Returns the distance from the observer to a point represented by a sample
     * or returns an default value if the xy-index doesn't correspond to a
     * sample in the panorama.
     * 
     * @param x
     *            horizontal position
     * @param y
     *            vertical position
     * @param d
     *            default value
     * @return returns the distance or the default value
     */
    public float distanceAt(int x, int y, float d) {
        
        if (parameters.isValidSampleIndex(x, y))
            return distance[parameters.linearSampleIndex(x, y)];
        return d;
        
    }

    /**
     * Returns the longitude of a point represented by a sample or throws an
     * exception if the xy-index doesn't correspond to a sample in the panorama.
     * 
     * @param x
     *            horizontal position (of the xy-index)
     * @param y
     *            vertical position
     * @return the longitude
     * @throws IndexOutOfBoundsException
     *             if the xy-index is out of the bounds of the panorama
     */
    public float longitudeAt(int x, int y) {
        
        checkValidSampleIndex(x, y);
        
        return longitude[parameters.linearSampleIndex(x, y)];
        
    }

    /**
     * Returns the latitude of a point represented by a sample or throws an
     * exception if the xy-index doesn't correspond to a sample in the panorama.
     * 
     * @param x
     *            horizontal position (of the xy-index)
     * @param y
     *            vertical position
     * @return the tatitude
     * @throws IndexOutOfBoundsException
     *             if the xy-index is out of the bounds of the panorama
     */
    public float latitudeAt(int x, int y) {
        
        checkValidSampleIndex(x, y);
        
        return latitude[parameters.linearSampleIndex(x, y)];
        
    }

    /**
     * Returns the elevation at a point represented by a sample or throws an
     * exception if the xy-index doesn't correspond to a sample in the panorama.
     * 
     * @param x
     *            horizontal position (of the xy-index)
     * @param y
     *            vertical position
     * @return the elevation
     * @throws IndexOutOfBoundsException
     *             if the xy-index is out of the bounds of the panorama
     */
    public float elevationAt(int x, int y) {
        
        checkValidSampleIndex(x, y);
        
        return elevation[parameters.linearSampleIndex(x, y)];
        
    }

    /**
     * Returns the slope at a point represented by a sample or throws an
     * exception if the xy-index doesn't correspond to a sample in the panorama.
     * 
     * @param x
     *            horizontal position (of the xy-index)
     * @param y
     *            vertical position
     * @return the slope
     * @throws IndexOutOfBoundsException
     *             if the xy-index is out of the bounds of the panorama
     */
    public float slopeAt(int x, int y) {
        
        checkValidSampleIndex(x, y);
        
        return slope[parameters.linearSampleIndex(x, y)];
        
    }

    // private functions

    private void checkValidSampleIndex(int x, int y) {
        
        if (!parameters.isValidSampleIndex(x, y))
            throw new IndexOutOfBoundsException("not valid sample index");
        
    }

    /**
     * Builder for a panorama
     * 
     * @author Natal Willisch (262092)
     *
     */
    public static final class Builder {
        
        final private PanoramaParameters parameters;
        private float[] distance;
        private float[] longitude;
        private float[] latitude;
        private float[] elevation;
        private float[] slope;
        private boolean set = false; // if buildable

        /**
         * To start building a panorama, the constructor takes the general
         * parameters and initializes from that the number of samples (means:
         * size of the tables).
         * 
         * @param parameters
         *            the general parameters
         * @throws NullPointerException
         *             if {@code parameters} is null
         */
        public Builder(PanoramaParameters parameters) {
            
            this.parameters = Objects.requireNonNull(parameters,
                    "parameters is null");
            
            int size = parameters.height() * parameters.width();
            
            distance = new float[size];
            longitude = new float[size];
            latitude = new float[size];
            elevation = new float[size];
            slope = new float[size];
            
            Arrays.fill(distance, Float.POSITIVE_INFINITY);
            
        }

        /**
         * Sets the distance to the observer for a sample.
         * 
         * @param x
         *            horizontal position of the sample
         * @param y
         *            vertical position of the sample
         * @param distance
         *            the distance
         * @return the object itself
         * @throws IndexOutOfBoundsException
         *             if the xy-index is out of the bounds of the panorama
         * @throws IllegalStateException
         *             if already built
         */
        public Builder setDistanceAt(int x, int y, float distance) {
            
            set(this.distance, x, y, distance);
            return this;
            
        }

        /**
         * Sets the longitude for a sample.
         * 
         * @param x
         *            horizontal position of the sample
         * @param y
         *            vertical position of the sample
         * @param longitude
         *            the longitude
         * @return the object itself
         * @throws IndexOutOfBoundsException
         *             if the xy-index is out of the bounds of the panorama
         * @throws IllegalStateException
         *             if already built
         */
        public Builder setLongitudeAt(int x, int y, float longitude) {
            
            set(this.longitude, x, y, longitude);
            return this;
            
        }

        /**
         * Sets the latitude for a sample.
         * 
         * @param x
         *            horizontal position of the sample
         * @param y
         *            vertical position of the sample
         * @param latitude
         *            the latitude
         * @return the object itself
         * @throws IndexOutOfBoundsException
         *             if the xy-index is out of the bounds of the panorama
         * @throws IllegalStateException
         *             if already built
         */
        public Builder setLatitudeAt(int x, int y, float latitude) {
            
            set(this.latitude, x, y, latitude);
            return this;
            
        }

        /**
         * sets the elevation for a sample.
         * 
         * @param x
         *            horizontal position of the sample
         * @param y
         *            vertical position of the sample
         * @param elevation
         *            the elevation
         * @return the object itself
         * @throws IndexOutOfBoundsException
         *             if the xy-index is out of the bounds of the panorama
         * @throws IllegalStateException
         *             if already built
         */
        public Builder setElevationAt(int x, int y, float elevation) {
            
            set(this.elevation, x, y, elevation);
            return this;
            
        }

        /**
         * Sets the slope for a sample.
         * 
         * @param x
         *            horizontal position of the sample
         * @param y
         *            vertical position of the sample
         * @param slope
         *            the slope
         * @return the object itself
         * @throws IndexOutOfBoundsException
         *             if the xy-index is out of the bounds of the panorama
         * @throws IllegalStateException
         *             if already built
         */
        public Builder setSlopeAt(int x, int y, float slope) {
            
            set(this.slope, x, y, slope);
            return this;
            
        }

        /**
         * Builds the final panorama. In the case the panorma is already built
         * the methode throws an exception.
         * 
         * @return the final panorama
         * @throws IllegalStateException
         *             if already built
         */
        public Panorama build() {
            
            checkBuild();
            
            set = true;
            Panorama panorama = new Panorama(parameters, distance, longitude, latitude,
                    elevation, slope);
            distance = longitude = latitude = elevation = slope = null;
            
            return panorama;
            
        }

        // private functions

        /**
         * Places a value at a given position of a given table
         */
        private void set(float[] table, int x, int y, float val) {
            
            checkBuild();
            checkValidSampleIndex(x, y);
            
            table[parameters.linearSampleIndex(x, y)] = val;
            
        }

        private void checkValidSampleIndex(int x, int y) {
            
            if (!parameters.isValidSampleIndex(x, y))
                throw new IndexOutOfBoundsException("not valid sample index");
            
        }

        private void checkBuild() {
            
            if (set)
                throw new IllegalStateException(
                        "this panorama is already build");
            
        }

    }
}
