package org.fossasia.openevent.app.unit.utils;

import org.fossasia.openevent.app.common.app.ContextManager;
import org.fossasia.openevent.app.common.data.models.Event;
import org.fossasia.openevent.app.common.utils.core.DateUtils;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

public class DateUtilsTest {

    private final Event event = new Event();

    @Before
    public void setUp() {
        event.setTimezone("Asia/Kolkata");
        ContextManager.setSelectedEvent(event);
        DateUtils.setShowLocal(false);
    }

    // Conversion checks
    @Test
    public void shouldConvertToEventTimezoneWithOffset() throws Exception {
        event.setTimezone("Asia/Singapore");

        TimeZone.setDefault(TimeZone.getTimeZone("US/Pacific"));
        String date  = "2017-03-17T14:00:00+08:00";
        assertEquals("17 03 2017 02:00:00 PM", DateUtils.formatDate("dd MM YYYY hh:mm:ss a", date));

        TimeZone.setDefault(TimeZone.getTimeZone("Australia/Sydney"));
        assertEquals("17 03 2017 02:00:00 PM", DateUtils.formatDate("dd MM YYYY hh:mm:ss a", date));

        TimeZone.setDefault(TimeZone.getTimeZone("Amsterdam"));
        assertEquals("17 03 2017 02:00:00 PM", DateUtils.formatDate("dd MM YYYY hh:mm:ss a", date));
    }

    @Test
    public void shouldConvertToEventTimezoneWithoutOffset() throws Exception {
        event.setTimezone("Asia/Singapore");

        String date  = "2017-03-17T06:00:00+00:00";

        TimeZone.setDefault(TimeZone.getTimeZone("US/Pacific"));
        assertEquals("17 03 2017 02:00:00 PM", DateUtils.formatDate("dd MM YYYY hh:mm:ss a", date));

        TimeZone.setDefault(TimeZone.getTimeZone("Australia/Sydney"));
        assertEquals("17 03 2017 02:00:00 PM", DateUtils.formatDate("dd MM YYYY hh:mm:ss a", date));

        TimeZone.setDefault(TimeZone.getTimeZone("Amsterdam"));
        assertEquals("17 03 2017 02:00:00 PM", DateUtils.formatDate("dd MM YYYY hh:mm:ss a", date));
    }

    @Test
    public void shouldConvertToLocalTimezoneWithOffset() throws Exception {
        event.setTimezone("Asia/Singapore");
        DateUtils.setShowLocal(true);

        String date  = "2017-03-17T14:00:00+08:00";

        TimeZone.setDefault(TimeZone.getTimeZone("US/Pacific"));
        assertEquals("16 03 2017 11:00:00 PM", DateUtils.formatDate("dd MM YYYY hh:mm:ss a", date));

        TimeZone.setDefault(TimeZone.getTimeZone("Australia/Sydney"));
        assertEquals("17 03 2017 05:00:00 PM", DateUtils.formatDate("dd MM YYYY hh:mm:ss a", date));

        TimeZone.setDefault(TimeZone.getTimeZone("Amsterdam"));
        assertEquals("17 03 2017 06:00:00 AM", DateUtils.formatDate("dd MM YYYY hh:mm:ss a", date));
    }

    @Test
    public void shouldReturn12HourTimeForGlobalTime() throws Exception {
        event.setTimezone("Asia/Singapore");
        String date = "2017-01-20T16:00:00+08:00";

        assertEquals("Failed for Global Time", "04:00 PM", DateUtils.formatDate(DateUtils.FORMAT_12H, date));
    }

    @Test
    public void shouldReturn24HourTime() throws Exception {
        event.setTimezone("Pacific/Gambier");
        String date = "2017-01-20T23:24:00-09:00";

        assertEquals("Failed for Global Time", "23:24", DateUtils.formatDate(DateUtils.FORMAT_24H, date));
    }

    @Test
    public void shouldReturnCompleteDate() throws Exception {
        String date = "2017-11-09T23:08:06-07:30";

        assertEquals("Failed for Global Time", "Fri, 10 Nov 2017", DateUtils.formatDate(DateUtils.FORMAT_DATE_COMPLETE, date));
    }

    @Test
    public void shouldReturnFormattedDatedWithDefaultString() throws ParseException {
        String date = "Wrong Date";

        assertEquals(date, date, DateUtils.formatDateWithDefault(date, date, date));
    }

    @Test
    public void shouldReturnFormattedDatedWithDefaultStringImplicit() throws ParseException {
        String date = "Wrong Date";

        assertEquals(date, "Invalid Date", DateUtils.formatDateWithDefault(date, date));
    }
}
