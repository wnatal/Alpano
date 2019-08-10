package ch.epfl.alpano;

/**
 * This class is for checking preconditions
 * 
 * @author Natal Willisch (262092)
 * 
 */
public interface Preconditions {

    /**
     * Checks an argument and throws an IllegalArgumentExceptation if the
     * argument is equivalent to false.
     * 
     * @param b
     *            boolean argument
     * @throws IllegalArgumentException
     *             if argument b is false
     */
    public static void checkArgument(boolean b) {
        
        if (!b)
            throw new IllegalArgumentException();
        
    }

    /**
     * Checks an argument and throws an IllegalArgumentExceptation with message
     * if the argument is equivalent to false.
     * 
     * @param b
     *            boolean argument
     * @param message
     *            the error message
     * @throws IllegalArgumentException
     *             if argument b is false
     */
    public static void checkArgument(boolean b, String message) {
        
        if (!b)
            throw new IllegalArgumentException(message);
        
    }
}
