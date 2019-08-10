package ch.epfl.alpano.summit;


import java.util.Objects;

import ch.epfl.alpano.GeoPoint;

/**
 * A instance of this class stores the information about a mountain-summit.
 * 
 * @author Natal Willisch (262092)
 *
 */
public final class Summit {
    
    private final String name;
    private final GeoPoint position;
    private final int elevation;

    /**
     * Constructs a new summit with all necessary informations
     * 
     * @param name
     *            name of the mountain
     * @param position
     *            position of the summit
     * @param elevation
     *            height of the mountain
     * @throws NullPointerException
     *             if the name or the location is null
     */
    public Summit(String name, GeoPoint position, int elevation) {
        
        this.name = Objects.requireNonNull(name);
        this.position = Objects.requireNonNull(position);
        this.elevation = elevation;
        
    }

    /**
     * Returns the name of the mountain.
     * 
     * @return the name
     */
    public String name() {
        
        return name;
        
    }

    /**
     * Returns the location of the summit.
     * 
     * @return the location
     */
    public GeoPoint position() {
        
        return position;
        
    }

    /**
     * Returns the height of the summit
     * 
     * @return the height
     */
    public int elevation() {
        
        return elevation;
        
    }

    /**
     * Remark: The representation of the location is in degrees.
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        
        String pos = position.toString();
        
        return name + pos + elevation;
        
    }
}
