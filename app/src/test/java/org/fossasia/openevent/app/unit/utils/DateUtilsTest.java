package org.fossasia.openevent.app.unit.utils;

import org.fossasia.openevent.app.common.ContextManager;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.utils.DateUtils;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

public class DateUtilsTest {

    private static final Event EVENT = new Event();
    private static final String TIMEZONE_SINGAPORE = "Asia/Singapore";
    private static final String TIMEZONE_US_PACIFIC = "US/Pacific";
    private static final String TIMEZONE_SYDNEY = "Australia/Sydney";
    private static final String TIMEZONE_AMSTERDAM = "Amsterdam";

    private static final String NORMALIZED_DATE = "17 03 2017 02:00:00 PM";
    private static final String NORMALIZED_DATE_FORMAT = "dd MM YYYY hh:mm:ss a";

    static {
        EVENT.setTimezone("Asia/Kolkata");
        ContextManager.setSelectedEvent(EVENT);
    }

    @Before
    public void setUp() {
        DateUtils.setShowLocal(false);
    }

    // Conversion checks
    @Test
    public void shouldConvertToEventTimezoneWithOffset() {
        EVENT.setTimezone(TIMEZONE_SINGAPORE);

        TimeZone.setDefault(TimeZone.getTimeZone(TIMEZONE_US_PACIFIC));
        String date  = "2017-03-17T14:00:00+08:00";
        assertEquals(NORMALIZED_DATE, DateUtils.formatDate(NORMALIZED_DATE_FORMAT, date));

        TimeZone.setDefault(TimeZone.getTimeZone(TIMEZONE_SYDNEY));
        assertEquals(NORMALIZED_DATE, DateUtils.formatDate(NORMALIZED_DATE_FORMAT, date));

        TimeZone.setDefault(TimeZone.getTimeZone(TIMEZONE_AMSTERDAM));
        assertEquals(NORMALIZED_DATE, DateUtils.formatDate(NORMALIZED_DATE_FORMAT, date));
    }

    @Test
    public void shouldConvertToEventTimezoneWithoutOffset() {
        EVENT.setTimezone(TIMEZONE_SINGAPORE);

        String date  = "2017-03-17T06:00:00+00:00";

        TimeZone.setDefault(TimeZone.getTimeZone(TIMEZONE_US_PACIFIC));
        assertEquals(NORMALIZED_DATE, DateUtils.formatDate(NORMALIZED_DATE_FORMAT, date));

        TimeZone.setDefault(TimeZone.getTimeZone(TIMEZONE_SYDNEY));
        assertEquals(NORMALIZED_DATE, DateUtils.formatDate(NORMALIZED_DATE_FORMAT, date));

        TimeZone.setDefault(TimeZone.getTimeZone(TIMEZONE_AMSTERDAM));
        assertEquals(NORMALIZED_DATE, DateUtils.formatDate(NORMALIZED_DATE_FORMAT, date));
    }

    @Test
    public void shouldConvertToLocalTimezoneWithOffset() {
        EVENT.setTimezone(TIMEZONE_SINGAPORE);
        DateUtils.setShowLocal(true);

        String date  = "2017-03-17T14:00:00+08:00";

        TimeZone.setDefault(TimeZone.getTimeZone(TIMEZONE_US_PACIFIC));
        assertEquals("16 03 2017 11:00:00 PM", DateUtils.formatDate(NORMALIZED_DATE_FORMAT, date));

        TimeZone.setDefault(TimeZone.getTimeZone(TIMEZONE_SYDNEY));
        assertEquals("17 03 2017 05:00:00 PM", DateUtils.formatDate(NORMALIZED_DATE_FORMAT, date));

        TimeZone.setDefault(TimeZone.getTimeZone(TIMEZONE_AMSTERDAM));
        assertEquals("17 03 2017 06:00:00 AM", DateUtils.formatDate(NORMALIZED_DATE_FORMAT, date));
    }

    @Test
    public void shouldReturn12HourTimeForGlobalTime() {
        EVENT.setTimezone(TIMEZONE_SINGAPORE);
        String date = "2017-01-20T16:00:00+08:00";

        assertEquals("Failed for Global Time", "04:00 PM", DateUtils.formatDate(DateUtils.FORMAT_12H, date));
    }

    @Test
    public void shouldReturn24HourTime() {
        EVENT.setTimezone("Pacific/Gambier");
        String date = "2017-01-20T23:24:00-09:00";

        assertEquals("Failed for Global Time", "23:24", DateUtils.formatDate(DateUtils.FORMAT_24H, date));
    }

    @Test
    public void shouldReturnCompleteDate() {
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
