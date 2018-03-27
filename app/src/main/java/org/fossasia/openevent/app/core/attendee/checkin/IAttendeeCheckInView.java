package org.fossasia.openevent.app.core.attendee.checkin;

import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.ItemResult;
import org.fossasia.openevent.app.data.models.Attendee;

public interface IAttendeeCheckInView extends Erroneous, ItemResult<Attendee> { }
