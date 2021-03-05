package com.citrine.interview;

/**
 * Container class for the end result we send to the user.
 */
public class ConversionResult {

    private final String unitName;
    private final double multiplicationFactor;

    public ConversionResult(String unitName, double multiplicationFactor) {
        this.unitName = unitName;
        this.multiplicationFactor = multiplicationFactor;
    }

    /**
     * The converted unit name. This is exactly the unit string from the request with each sub-unit being converted
     * to its SI equivalent. Note that for this and the conversion factor, the underscore casing is used so that the
     * output JSON can match the spec. Example value: "(rad/s)"
     */
    public String getUnit_name() {
        return unitName;
    }

    /**
     * A value which, when multiplied with a value of the original unit, will result in that value in its SI equivalent.
     * This value will have 14 significant digits.
     */
    public double getMultiplication_factor() {
        return multiplicationFactor;
    }

}
