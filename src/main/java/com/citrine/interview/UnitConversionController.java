package com.citrine.interview;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Class for handling the HTTP requests. This is also where almost all of the logic resides.
 */
@RestController
public class UnitConversionController {

    private static final char MULTIPLICATION_SYMBOL = '*';
    private static final char DIVISION_SYMBOL = '/';

    @GetMapping("/units/si")
    public ConversionResult convertUnit(@RequestParam(value = "units") String units) {
        // Call our recursive helper after trimming any spaces. [Note from Jake: Trimming spaces from the input is the
        // only sort of input correction done here. The spec notes that this endpoint should convert well-formed input
        // strings, but it doesn't mention what to do if the input is bad (eg "(degrees" or even something like "foo")
        // This converter won't throw an error but will return something incorrect in that case]
        ConversionResult result = convertUnitHelper(units.replace(" ", ""));

        // Round to 14 signficant figures, per spec.
        double multiplicationFactor = result.getMultiplication_factor();
        BigDecimal bigDecimal = new BigDecimal(multiplicationFactor);
        bigDecimal = bigDecimal.round(new MathContext(14));

        return new ConversionResult(result.getUnit_name(), bigDecimal.doubleValue());
    }

    /**
     * Recursive helper function for converting a well-formed unit string. It's helpful to view this string as matching
     * one of six patterns ("U" refers recursively to another unit string): (1) A base unit, eg "degrees", (2) "U*U",
     * (3) "U/U", (4) "(U)", (5) "(U)*U" and (6) "(U)/U"
     */
    private ConversionResult convertUnitHelper(String units) {
        if (units.startsWith("(")) {
            // Figure out which of the parens cases we're seeing.
            int indexOfMatchingParen = findMatchingParenIndex(units);
            if (indexOfMatchingParen == -1) {
                return new ConversionResult("", 1.0);
            }
            ConversionResult innerResult = convertUnitHelper(units.substring(1, indexOfMatchingParen));
            ConversionResult parenthesizedResult =
                    new ConversionResult("(" + innerResult.getUnit_name() + ")",
                            innerResult.getMultiplication_factor());

            // If our matching paren is the end of the units string, we're in the "(U)" case.
            if (indexOfMatchingParen == units.length() - 1) {
                return parenthesizedResult;
            }
            // Otherwise, look at the character after our matching paren to see if we're in the "(U)*U" case or the
            // "(U)/U" case.
            char nextOperation = units.charAt(indexOfMatchingParen + 1);
            if (nextOperation == MULTIPLICATION_SYMBOL) {
                return multiply(parenthesizedResult, convertUnitHelper(units.substring(indexOfMatchingParen + 2)));
            }
            return divide(parenthesizedResult, convertUnitHelper(units.substring(indexOfMatchingParen + 2)));
        }
        // If we're not in one of the parens cases, we can scan the string for either multiplication or division
        // symbols. Whichever comes first (or if there are neither) determines what kind of case we're in.
        int nextMultiplicationIndex = units.indexOf(MULTIPLICATION_SYMBOL);
        int nextDivisionIndex = units.indexOf(DIVISION_SYMBOL);
        // "U*U"
        if (nextMultiplicationIndex != -1 &&
                (nextDivisionIndex == -1 || nextMultiplicationIndex < nextDivisionIndex)) {
            return multiply(convertUnitHelper(units.substring(0, nextMultiplicationIndex)),
                    convertUnitHelper(units.substring(nextMultiplicationIndex + 1)));
        }
        // "U/U"
        if (nextDivisionIndex != -1) {
            return divide(convertUnitHelper(units.substring(0, nextDivisionIndex)),
                    convertUnitHelper(units.substring(nextDivisionIndex + 1)));
        }
        // Finally, the base case.
        return BaseUnitConverter.convertBaseUnit(units);
    }

    /**
     * Find the index of the matching right paren to a unit which starts with a left paren.
     */
    private int findMatchingParenIndex(String unit) {
        // Finding the first or last right paren of the strings fails in some cases (consider "((a))" and "(a)*(b)"
        // respectively). Instead, we traverse the string, tracking how many unmatched left parens there are. When we
        // come to a right paren and the only unmatched left paren is the first character of the string, we've found
        // our match.
        int numUnmatchedLeftParens = 0;
        for (int i = 1; i < unit.length(); i++) {
            char c = unit.charAt(i);
            if (c == '(') {
                numUnmatchedLeftParens++;
            }
            if (c == ')') {
                if (numUnmatchedLeftParens == 0) {
                    return i;
                }
                numUnmatchedLeftParens--;
            }
        }
        return -1;
    }

    /**
     * Combine two ConversionResults that are being multiplied together.
     */
    private ConversionResult multiply(ConversionResult r1, ConversionResult r2) {
        return new ConversionResult(r1.getUnit_name() + MULTIPLICATION_SYMBOL + r2.getUnit_name(),
                r1.getMultiplication_factor() * r2.getMultiplication_factor());
    }

    /**
     * Combine two ConversionResults that are being divided.
     */
    private ConversionResult divide(ConversionResult numerator, ConversionResult denominator) {
        return new ConversionResult(numerator.getUnit_name() + DIVISION_SYMBOL + denominator.getUnit_name(),
                numerator.getMultiplication_factor() / denominator.getMultiplication_factor());
    }

}
