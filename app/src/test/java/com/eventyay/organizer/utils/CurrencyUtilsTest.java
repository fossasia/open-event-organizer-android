package com.eventyay.organizer.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class CurrencyUtilsTest {

    private CurrencyUtils currencyUtils;

    @Before
    public void setUp() {
        currencyUtils = new CurrencyUtils();
    }

    @Test
    public void testDollar() {
        assertEquals("US$", currencyUtils.getCurrencySymbol("USD").blockingGet());
    }

    @Test
    public void testRupee() {
        assertEquals("Rs.", currencyUtils.getCurrencySymbol("INR").blockingGet());
    }

    @Test
    public void testPound() {
        assertEquals("£", currencyUtils.getCurrencySymbol("GBP").blockingGet());
    }
}
