package ch.epfl.alpano.gui;

import java.util.Arrays;
import java.util.List;

import ch.epfl.alpano.Preconditions;
import javafx.util.StringConverter;

/**
 * Converts from string to integer and the other way around by taking the
 * position of the string in a internal list as "translation".
 * 
 * @author Natal Willisch (262092)
 * 
 */
public final class LabeledListStringConverter extends StringConverter<Integer> {

    private final List<String> list;

    /**
     * The constructor takes a list of strings, the position of the string in
     * the list will correspond later to the integer to/from which the string
     * will be converted (first element ~ 0)
     * 
     * @param list
     *            list of strings
     */
    public LabeledListStringConverter(String... list) {

        this.list = Arrays.asList(list);

    }

    /**
     * 
     * @see javafx.util.StringConverter#fromString(java.lang.String)
     * @throws IllegalArgumentException
     *             if the string is not in the stored list of possible strings
     */
    @Override
    public Integer fromString(String arg0) {

        int index = list.indexOf(arg0);
        Preconditions.checkArgument(index >= 0,
                "fromString: argument doesn't exist");

        return index;

    }

    /*
     * (non-Javadoc)
     * 
     * @see javafx.util.StringConverter#toString(java.lang.Object)
     */
    @Override
    public String toString(Integer arg0) {

        return (arg0 >= 0 && arg0 < list.size()) ? list.get(arg0) : "";

    }

}
