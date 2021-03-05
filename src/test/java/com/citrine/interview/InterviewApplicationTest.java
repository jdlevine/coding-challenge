package com.citrine.interview;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Unit tests for the conversion logic.
 */
@SpringBootTest
@AutoConfigureMockMvc
class InterviewApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testBasicUnit() throws Exception {
        runTest("litre", new ConversionResult("m\u00B3", .001));
    }

    @Test
    void testMultiplicationAndDivision() throws Exception {
        runTest("tonne*minute", new ConversionResult("kg*s", 60000.0));
        runTest("tonne/minute", new ConversionResult("kg/s", 16.666666666667));
    }

    @Test
    void testWithSiUnits() throws Exception {
        runTest("s*litre", new ConversionResult("s*m\u00B3", .001));
    }

    @Test
    void testWIthParens() throws Exception {
        runTest("(tonne)", new ConversionResult("(kg)", 1000.0));
        // TODO Add more
    }

    private void runTest(String units, ConversionResult expectedResult) throws Exception {
        String expectedJson = "{" +
          "\"unit_name\": \"" + expectedResult.getUnit_name() + "\", " +
          "\"multiplication_factor\": " + expectedResult.getMultiplication_factor() + "}";
        mockMvc.perform(get("/units/si?units=" + units)).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

}
