package org.fossasia.openevent.app.utils;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.fossasia.openevent.app.utils.DateUtils.getDate;
import static org.junit.Assert.assertEquals;

public class DateUtilsTest {

    @Before
    public void setUp() {
        DateUtils.setForTest();
    }

    // Conversion checks
    @Test
    public void shouldFormatArbitraryWithoutTimeZone() throws Exception {
        TimeZone.setDefault(TimeZone.getTimeZone("US/Pacific"));
        DateUtils.setShowLocalTimeZone(false);
        String date  = "2017-03-17T14:00:00+08:00";
        assertEquals("17 03 2017 02:00:00 PM", DateUtils.formatDate("dd MM YYYY hh:mm:ss a", date));

        TimeZone.setDefault(TimeZone.getTimeZone("Australia/Sydney"));
        DateUtils.setShowLocalTimeZone(false);
        assertEquals("17 03 2017 02:00:00 PM", DateUtils.formatDate("dd MM YYYY hh:mm:ss a", date));

        TimeZone.setDefault(TimeZone.getTimeZone("Amsterdam"));
        DateUtils.setShowLocalTimeZone(false);
        assertEquals("17 03 2017 02:00:00 PM", DateUtils.formatDate("dd MM YYYY hh:mm:ss a", date));
    }

    @Test
    public void shouldReturn12HourTime() throws Exception {
        String date = "2017-01-20T16:00:00+10:00";

        assertEquals("Failed for Global Time", "04:00 PM", DateUtils.formatDate(DateUtils.FORMAT_12H, date));
    }

    @Test
    public void shouldReturn24HourTime() throws Exception {
        String date = "2017-01-20T24:24:00-09:00";

        assertEquals("Failed for Global Time", "00:24", DateUtils.formatDate(DateUtils.FORMAT_24H, date));
    }

    @Test
    public void shouldReturnCompleteDate() throws Exception {
        String date = "2017-11-09T23:08:06-07:30";

        assertEquals("Failed for Global Time", "Thu, 09 Nov 2017", DateUtils.formatDate(DateUtils.FORMAT_DATE_COMPLETE, date));
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

    private static void assertDateEquals(Date date, int day, int month, int year, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        assertEquals(day, calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(month, calendar.get(Calendar.MONTH) + 1);
        assertEquals(year, calendar.get(Calendar.YEAR));
        assertEquals(hour, calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(minute, calendar.get(Calendar.MINUTE));
        assertEquals(second, calendar.get(Calendar.SECOND));
    }

    @Test
    public void shouldReturnDay() throws ParseException {
        String date = "2017-11-09T23:08:56-07:30";

        Date date1 = getDate(date);
        assertDateEquals(date1, 9, 11, 2017, 23, 8, 56);
    }
}
