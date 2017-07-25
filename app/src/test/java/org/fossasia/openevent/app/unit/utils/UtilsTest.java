package org.fossasia.openevent.app.unit.utils;

import org.fossasia.openevent.app.common.data.models.Attendee;
import org.fossasia.openevent.app.common.utils.core.Utils;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

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

    @Test
    public void shouldFindIndex() {
        List<Attendee> attendees = Arrays.asList(
            new Attendee(12),
            new Attendee(34),
            new Attendee(10),
            new Attendee(90),
            new Attendee(3)
        );

        Attendee newAttendee = new Attendee(10);

        Utils.PropertyMatcher<Attendee> idEqual = (first, second) -> first.getId() == second.getId();

        Utils.indexOf(attendees, newAttendee, idEqual)
            .test()
            .assertNoErrors()
            .assertValue(2);

        newAttendee.setId(12);
        Utils.indexOf(attendees, newAttendee, idEqual)
            .test()
            .assertNoErrors()
            .assertValue(0);

        newAttendee.setId(3);
        Utils.indexOf(attendees, newAttendee, idEqual)
            .test()
            .assertNoErrors()
            .assertValue(4);

        newAttendee.setId(2);
        Utils.indexOf(attendees, newAttendee, idEqual)
            .test()
            .assertNoErrors()
            .assertValue(-1);
    }

    @Test
    public void shouldFormatToken() {
        String token = "token";

        assertEquals("JWT token", Utils.formatToken(token));
    }

}
