package ch.epfl.alpano.gui;

/**
 * UserParameter is there to cut/sanitize values according to given minimal and
 * maximal values. For this reason it provides predefined values for certain
 * situations as a enumeration.
 * 
 * @author Natal Willisch (262092)
 *
 */
public enum UserParameter {

    // enumeration
    OBSERVER_LONGITUDE(60000, 120000), 
    OBSERVER_LATITUDE(450000, 480000), 
    OBSERVER_ELEVATION(300, 10000), 
    CENTER_AZIMUTH(0, 359), 
    HORIZONTAL_FIELD_OF_VIEW(1, 360), 
    MAX_DISTANCE(10, 600), 
    WIDTH(30, 16000), 
    HEIGHT(10, 4000), 
    SUPER_SAMPLING_EXPONENT(0, 2);

    private int max, min;

    /**
     * Constructor 
     * 
     * @param min
     *          minimal value
     * @param max
     *          maximal value
     */
    private UserParameter(int min, int max) {
        this.max = max;
        this.min = min;
    }

    /**
     * returns the same value or the bound (max/min) that the value violates.
     * 
     * @param val
     *          value
     * @return sanitize value
     */
    public int sanitize(int val) {
        if (val > max)
            val = max;
        else if (val < min)
            val = min;
        return val;
    }
}
