package com.eventyay.organizer.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.utils.service.DateService;
import java.text.ParseException;
import org.junit.Test;
import org.threeten.bp.LocalDateTime;
import timber.log.Timber;

public class DateServiceTest {

    private static final Event LIVE = new Event();
    private static final Event PAST = new Event();
    private static final Event UPCOMING = new Event();

    static {
        String max = DateUtils.formatDateToIso(LocalDateTime.MAX);
        String min = DateUtils.formatDateToIso(LocalDateTime.MIN);

        LIVE.setStartsAt(min);
        LIVE.setEndsAt(max);

        PAST.setStartsAt(min);
        PAST.setEndsAt(min);

        UPCOMING.setStartsAt(max);
        UPCOMING.setEndsAt(max);
    }

    private static void testStatus(Event event, String expected) {
        try {
            assertEquals(expected, DateService.getEventStatus(event));
        } catch (ParseException e) {
            Timber.e(e);
            fail("Parse error should not have occurred");
        }
    }

    @Test
    public void shouldReturnLiveStatus() {
        testStatus(LIVE, DateService.LIVE_EVENT);
    }

    @Test
    public void shouldReturnPastStatus() {
        testStatus(PAST, DateService.PAST_EVENT);
    }

    @Test
    public void shouldReturnUpcomingStatus() {
        testStatus(UPCOMING, DateService.UPCOMING_EVENT);
    }

    @Test
    public void testNaturalOrderLive() {
        // Live should appear on top of upcoming and past
        assertEquals(1, DateService.compareEventDates(LIVE, LIVE));
        assertEquals(-1, DateService.compareEventDates(LIVE, PAST));
        assertEquals(-1, DateService.compareEventDates(LIVE, UPCOMING));
    }

    @Test
    public void testNaturalOrderPast() {
        // Past should always appear in bottom
        assertEquals(1, DateService.compareEventDates(PAST, LIVE));
        assertEquals(1, DateService.compareEventDates(PAST, PAST));
        assertEquals(1, DateService.compareEventDates(PAST, UPCOMING));
    }

    @Test
    public void testNaturalOrderUpcoming() {
        // Upcoming should appear on top of past
        assertEquals(1, DateService.compareEventDates(UPCOMING, LIVE));
        assertEquals(-1, DateService.compareEventDates(UPCOMING, PAST));
        assertEquals(1, DateService.compareEventDates(UPCOMING, UPCOMING));
    }

    @Test
    public void testClashLive() {
        Event liveLatest = new Event();
        String nowMinus5 = DateUtils.formatDateToIso(LocalDateTime.now().minusDays(5));
        String nowPlus5 = DateUtils.formatDateToIso(LocalDateTime.now().plusDays(5));
        liveLatest.setStartsAt(nowMinus5);
        liveLatest.setEndsAt(nowPlus5);

        assertEquals(1, DateService.compareEventDates(LIVE, liveLatest));
    }

    @Test
    public void testClashPast() {
        Event pastLatest = new Event();
        String min = DateUtils.formatDateToIso(LocalDateTime.MIN);
        String minPlus5 = DateUtils.formatDateToIso(LocalDateTime.MIN.plusDays(5));
        pastLatest.setStartsAt(min);
        pastLatest.setEndsAt(minPlus5);

        assertEquals(1, DateService.compareEventDates(PAST, pastLatest));
    }

    @Test
    public void testClashUpcoming() {
        Event upcomingEarliest = new Event();
        String max = DateUtils.formatDateToIso(LocalDateTime.MAX);
        String maxMinus5 = DateUtils.formatDateToIso(LocalDateTime.MAX.minusDays(5));
        upcomingEarliest.setStartsAt(maxMinus5);
        upcomingEarliest.setEndsAt(max);

        assertEquals(1, DateService.compareEventDates(UPCOMING, upcomingEarliest));
    }
}
