package org.fossasia.openevent.app.utils;

import org.fossasia.openevent.app.data.models.Event;

import java.text.ParseException;
import java.util.Date;

public class DateService {

    private static final String LIVE_EVENT = "LIVE";
    private static final String PAST_EVENT = "PAST";
    private static final String UPCOMING_EVENT = "UPCOMING";

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
        Date now = new Date();
        try {
            Date startDate = DateUtils.getDate(one.getStartsAt());
            Date endDate = DateUtils.getDate(one.getEndsAt());
            Date otherStartDate = DateUtils.getDate(two.getEndsAt());
            Date otherEndDate = DateUtils.getDate(two.getEndsAt());
            if (endDate.before(now) || otherEndDate.before(now)) {
                // one of them is past and other can be past or live or upcoming
                return endDate.after(otherEndDate) ? -1 : 1;
            } else {
                if (startDate.after(now) || otherStartDate.after(now)) {
                    // one of them is upcoming other can be upcoming or live
                    return startDate.before(otherStartDate) ? -1 : 1;
                } else {
                    // both are live
                    return startDate.after(otherStartDate) ? -1 : 1;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public static String getEventStatus(Event event) throws ParseException {
        Date startDate = DateUtils.getDate(event.getEndsAt());
        Date endDate = DateUtils.getDate(event.getEndsAt());
        Date now = new Date();
        if (now.after(startDate)) {
            if (now.before(endDate)) {
                return LIVE_EVENT;
            } else {
                return PAST_EVENT;
            }
        } else {
            return UPCOMING_EVENT;
        }
    }
}
