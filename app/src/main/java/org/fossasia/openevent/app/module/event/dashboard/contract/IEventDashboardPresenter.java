package org.fossasia.openevent.app.module.event.dashboard.contract;

import org.fossasia.openevent.app.common.app.lifecycle.contract.presenter.IDetailPresenter;

public interface IEventDashboardPresenter extends IDetailPresenter<Long, IEventDashboardView> {

    void loadDetails(boolean forceReload);

    void toggleState();
}
