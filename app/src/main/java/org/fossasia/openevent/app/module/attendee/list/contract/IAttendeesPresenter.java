package org.fossasia.openevent.app.module.attendee.list.contract;

import org.fossasia.openevent.app.common.app.lifecycle.contract.presenter.IDetailPresenter;
import org.fossasia.openevent.app.common.data.models.Attendee;

import java.util.List;

public interface IAttendeesPresenter extends IDetailPresenter<Long, IAttendeesView> {

    List<Attendee> getAttendees();

    void loadAttendees(boolean forceReload);

}
