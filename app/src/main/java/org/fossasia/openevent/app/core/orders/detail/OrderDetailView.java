package org.fossasia.openevent.app.core.orders.detail;

import org.fossasia.openevent.app.common.mvp.view.Emptiable;
import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.common.mvp.view.Refreshable;
import org.fossasia.openevent.app.data.attendee.Attendee;

public interface OrderDetailView extends Progressive, Erroneous, Refreshable, Emptiable<Attendee> {
}
