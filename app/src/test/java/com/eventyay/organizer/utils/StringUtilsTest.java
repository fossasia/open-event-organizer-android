package com.eventyay.organizer.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StringUtilsTest {

    @Test
    public void testNullifyEmptyStrings() {
        String emptyString = "";
        String nullString = null;

        String outputString = StringUtils.emptyToNull(emptyString);
        assertEquals(nullString, outputString);
    }

    @Test
    public void shouldReturnNullOnNullStrings() {
        String nullString = null;

        String outputString = StringUtils.emptyToNull(nullString);
        assertEquals(nullString, outputString);
    }

    @Test
    public void shouldReturnNonEmptyOrNonNullStrings() {
        String dataString = "This is not empty";

        String outputString = StringUtils.emptyToNull(dataString);
        assertEquals(dataString, outputString);
    }
}
