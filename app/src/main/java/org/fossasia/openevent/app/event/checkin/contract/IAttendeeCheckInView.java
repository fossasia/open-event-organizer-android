package org.fossasia.openevent.app.event.checkin.contract;

import org.fossasia.openevent.app.common.contract.view.Erroneous;
import org.fossasia.openevent.app.common.contract.view.ItemResult;
import org.fossasia.openevent.app.common.contract.view.Progressive;
import org.fossasia.openevent.app.common.contract.view.Successful;
import org.fossasia.openevent.app.data.models.Attendee;

public interface IAttendeeCheckInView extends Progressive, Successful, Erroneous, ItemResult<Attendee> {}
