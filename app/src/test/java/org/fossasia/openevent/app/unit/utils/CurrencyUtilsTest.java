package org.fossasia.openevent.app.unit.utils;

import org.fossasia.openevent.app.utils.CurrencyUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CurrencyUtilsTest {

    @Test
    public void testDollar() {
        assertEquals("US$", CurrencyUtils.getCurrencySymbol("USD").blockingGet());
    }

    @Test
    public void testRupee() {
        assertEquals("Rs.", CurrencyUtils.getCurrencySymbol("INR").blockingGet());
    }

    @Test
    public void testPound() {
        assertEquals("£", CurrencyUtils.getCurrencySymbol("GBP").blockingGet());
    }

}
