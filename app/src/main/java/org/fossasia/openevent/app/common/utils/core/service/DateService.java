package org.fossasia.openevent.app.common.utils.core.service;

import org.fossasia.openevent.app.common.data.models.Event;
import org.fossasia.openevent.app.common.utils.core.DateUtils;
import org.threeten.bp.ZonedDateTime;

import java.text.ParseException;

@SuppressWarnings("PMD.DataflowAnomalyAnalysis") // Bug in PMD related to DU anomaly
public final class DateService {

    private static final String LIVE_EVENT = "LIVE";
    private static final String PAST_EVENT = "PAST";
    private static final String UPCOMING_EVENT = "UPCOMING";

    private DateService() {
        // Never Called
    }

    /**
     * Compare events for sorting
     * the list will be in order of live events, upcoming events, past events
     *
     * for both live events latest will be before in list
     * for both past events lately ended will be before in list
     * for both upcoming lately started will be before in list
     *
     * @return int
     */
    public static int compareEventDates(Event one, Event two) {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime startDate = DateUtils.getDate(one.getStartsAt());
        ZonedDateTime endDate = DateUtils.getDate(one.getEndsAt());
        ZonedDateTime otherStartDate = DateUtils.getDate(two.getEndsAt());
        ZonedDateTime otherEndDate = DateUtils.getDate(two.getEndsAt());
        if (endDate.isBefore(now) || otherEndDate.isBefore(now)) {
            // one of them is past and other can be past or live or upcoming
            return endDate.isAfter(otherEndDate) ? -1 : 1;
        } else {
            if (startDate.isAfter(now) || otherStartDate.isAfter(now)) {
                // one of them is upcoming other can be upcoming or live
                return startDate.isBefore(otherStartDate) ? -1 : 1;
            } else {
                // both are live
                return startDate.isAfter(otherStartDate) ? -1 : 1;
            }
        }
    }

    public static String getEventStatus(Event event) throws ParseException {
        ZonedDateTime startDate = DateUtils.getDate(event.getEndsAt());
        ZonedDateTime endDate = DateUtils.getDate(event.getEndsAt());
        ZonedDateTime now = ZonedDateTime.now();
        if (now.isAfter(startDate)) {
            if (now.isBefore(endDate)) {
                return LIVE_EVENT;
            } else {
                return PAST_EVENT;
            }
        } else {
            return UPCOMING_EVENT;
        }
    }
}
