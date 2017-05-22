package org.fossasia.openevent.app.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UtilsTest {

    @Test
    public void testIsEmpty() {
        String nullString = null;

        assertEquals(Utils.isEmpty(nullString), true);

        String emptyString = "";

        assertEquals(Utils.isEmpty(emptyString), true);

        String nonEmptyString = "Full";

        assertEquals(Utils.isEmpty(nonEmptyString), false);
    }

    @Test
    public void testOptionalString() {
        String nullString = null;

        assertEquals(Utils.optionalString(nullString), "");

        String emptyString = "";

        assertEquals(Utils.optionalString(emptyString), "");

        String nonEmptyString = "Full";

        assertEquals(Utils.optionalString(nonEmptyString), "Full");
    }

    @Test
    public void testOptionalFormat() {
        String nullString = null;
        String emptyString = "";
        String nonEmptyString = "Full";

        assertEquals(Utils.formatOptionalString("%s %s", nonEmptyString, nullString), "Full ");

        assertEquals(Utils.formatOptionalString("%s %s", nonEmptyString, emptyString), "Full ");

        assertEquals(Utils.formatOptionalString("%s %s", emptyString, nonEmptyString), " Full");

        assertEquals(Utils.formatOptionalString("%s %s", nullString, nonEmptyString), " Full");

        assertEquals(Utils.formatOptionalString("%s %s", nonEmptyString, nonEmptyString), "Full Full");
    }

}
