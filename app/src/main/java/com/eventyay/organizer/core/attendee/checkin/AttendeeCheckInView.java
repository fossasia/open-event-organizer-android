package com.eventyay.organizer.core.attendee.checkin;

import com.eventyay.organizer.common.mvp.view.Erroneous;
import com.eventyay.organizer.common.mvp.view.ItemResult;
import com.eventyay.organizer.data.attendee.Attendee;

public interface AttendeeCheckInView extends Erroneous, ItemResult<Attendee> {}
