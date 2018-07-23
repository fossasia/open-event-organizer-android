package org.fossasia.openevent.app.core.attendee.history;

import org.fossasia.openevent.app.common.mvp.view.Emptiable;
import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.common.mvp.view.Refreshable;
import org.fossasia.openevent.app.data.attendee.CheckInDetail;

public interface CheckInHistoryView extends Progressive, Erroneous, Refreshable, Emptiable<CheckInDetail> {
}
