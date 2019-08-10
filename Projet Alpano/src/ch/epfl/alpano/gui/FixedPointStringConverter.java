package ch.epfl.alpano.gui;

import java.math.BigDecimal;
import javafx.util.StringConverter;

import static java.math.RoundingMode.*;

/**
 * Converts from string to integer and the other way around by "shifting" an
 * integer to a decimal and convert it to a string and the other way around.
 * example: 2 decimals, "3654.34" <-> 365434
 * 
 * @author Natal Willisch (262092)
 * 
 */
public final class FixedPointStringConverter extends StringConverter<Integer> {
    final int decimals;

    /**
     * takes as argument the number position after the decimal point that should
     * be also saved in the integer. Or in other word by how much the decimal
     * point should be shifted.
     * 
     * @param decimals
     *            position of the point
     */
    public FixedPointStringConverter(int decimals) {

        this.decimals = decimals;

    }

    /**
     * (non-Javadoc)
     * 
     * @see javafx.util.StringConverter#fromString(java.lang.String)
     */
    @Override
    public Integer fromString(String arg0) {

        BigDecimal decimal = new BigDecimal(arg0);

        return decimal.movePointRight(decimals).setScale(0, HALF_UP)
                .intValueExact();

    }

    /*
     * (non-Javadoc)
     * 
     * @see javafx.util.StringConverter#toString(java.lang.Object)
     */
    @Override

    public String toString(Integer arg0) {

        BigDecimal decimal = new BigDecimal(arg0);

        return decimal.movePointLeft(decimals).stripTrailingZeros()
                .toPlainString();

    }

}
