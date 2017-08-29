package org.fossasia.openevent.app.unit.utils;

import org.fossasia.openevent.app.common.data.contract.IUtilModel;
import org.fossasia.openevent.app.common.data.db.DatabaseRepository;
import org.fossasia.openevent.app.common.data.models.Attendee;
import org.fossasia.openevent.app.common.data.repository.EventRepository;
import org.fossasia.openevent.app.common.utils.core.Utils;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import retrofit2.Retrofit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class UtilsTest {

    @Test
    public void testIsEmpty() {
        String nullString = null;

        assertTrue(Utils.isEmpty(nullString));

        String emptyString = "";

        assertTrue(Utils.isEmpty(emptyString));

        String nonEmptyString = "Full";

        assertFalse(Utils.isEmpty(nonEmptyString));
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
            Attendee.builder().id(12).build(),
            Attendee.builder().id(34).build(),
            Attendee.builder().id(10).build(),
            Attendee.builder().id(90).build(),
            Attendee.builder().id(3).build()
        );

        Attendee newAttendee = Attendee.builder().id(10).build();

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
