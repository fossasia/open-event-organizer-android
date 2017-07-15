package org.fossasia.openevent.app.event.attendees.contract;

import org.fossasia.openevent.app.common.contract.presenter.IDetailPresenter;
import org.fossasia.openevent.app.data.models.Attendee;

import java.util.List;

public interface IAttendeesPresenter extends IDetailPresenter<Long, IAttendeesView> {

    List<Attendee> getAttendees();

    void loadAttendees(boolean forceReload);

}
