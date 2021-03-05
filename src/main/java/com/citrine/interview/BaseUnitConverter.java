package com.citrine.interview;

import java.util.HashMap;
import java.util.Map;

public final class BaseUnitConverter {

    private static final Map<String, ConversionResult> baseUnitsToSIUnits = new HashMap<>();

    /**
     * Helper method to convert a base unit (eg "minute" or "L") into an SI unit. Note that this base unit
     * can also be an SI unit, in which case the response is just the base unit + 1.0 as the multiplication factor.
     */
    public static ConversionResult convertBaseUnit(String baseUnit) {
        synchronized (baseUnitsToSIUnits) {
            if (baseUnitsToSIUnits.isEmpty()) {
                populateBaseUnitsToSIUnits();
            }
        }
        return baseUnitsToSIUnits.getOrDefault(baseUnit, new ConversionResult("", 1));
    }

    /**
     * Initialize our conversion data for base units. In a production setting, it might make more sense for these
     * to live in a configuration file - it's easier to read in that format and it's more convenient to modify
     * the data without making an explicit code change. However, for the purposes of this exercise,
     */
    private static void populateBaseUnitsToSIUnits() {
        addUnitToMap("minute", "min", new ConversionResult("s", 60));
        addUnitToMap("hour", "h", new ConversionResult("s", 3600));
        addUnitToMap("day", "d", new ConversionResult("s", 86400));
        addUnitToMap("degree", "\u00B0", new ConversionResult("rad", .0174532925199433));
        addUnitToMap("arcminute", "'", new ConversionResult("rad", .0002908882086657f));
        addUnitToMap("arcsecond", "\"", new ConversionResult("rad", .0000048481368111));
        addUnitToMap("hectare", "ha", new ConversionResult("m\u00B2", 10000));
        addUnitToMap("litre", "L", new ConversionResult("m\u00B3", .001));
        addUnitToMap("tonne", "t", new ConversionResult("kg", 1000));
    }

    /**
     * Adds a base unit (as well as its symbol abbreviation) to our conversion map. This also adds the SI unit as
     * a mirror conversion result (ie just itself as the name and 1 as the conversion factor).
     */
    private static void addUnitToMap(String baseUnit, String symbol, ConversionResult conversionResult) {
        baseUnitsToSIUnits.put(baseUnit, conversionResult);
        baseUnitsToSIUnits.put(symbol, conversionResult);
        baseUnitsToSIUnits.put(conversionResult.getUnit_name(),
          new ConversionResult(conversionResult.getUnit_name(), 1));
    }

}
