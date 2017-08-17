package org.fossasia.openevent.app.module.attendee.checkin.contract;

import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Erroneous;
import org.fossasia.openevent.app.common.app.lifecycle.contract.view.ItemResult;
import org.fossasia.openevent.app.common.data.models.Attendee;

public interface IAttendeeCheckInView extends Erroneous, ItemResult<Attendee> { }
