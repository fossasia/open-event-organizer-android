package org.fossasia.openevent.app.module.attendee.checkin.contract;

import org.fossasia.openevent.app.common.app.lifecycle.contract.presenter.IDetailPresenter;

public interface IAttendeeCheckInPresenter extends IDetailPresenter<Long, IAttendeeCheckInView> {

    void toggleCheckIn();

}
