package org.fossasia.openevent.app.event.checkin.contract;

import org.fossasia.openevent.app.common.contract.presenter.IDetailPresenter;

public interface IAttendeeCheckInPresenter extends IDetailPresenter<Long, IAttendeeCheckInView> {

    void toggleCheckIn();

}
