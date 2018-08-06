package com.eventyay.organizer.core.attendee.history;

import com.eventyay.organizer.common.mvp.view.Emptiable;
import com.eventyay.organizer.common.mvp.view.Erroneous;
import com.eventyay.organizer.common.mvp.view.Progressive;
import com.eventyay.organizer.common.mvp.view.Refreshable;
import com.eventyay.organizer.data.attendee.CheckInDetail;

public interface CheckInHistoryView extends Progressive, Erroneous, Refreshable, Emptiable<CheckInDetail> {
}
