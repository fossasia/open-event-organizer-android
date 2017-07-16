package org.fossasia.openevent.app.event.detail.contract;

import org.fossasia.openevent.app.common.contract.presenter.IDetailPresenter;

public interface IEventDetailPresenter extends IDetailPresenter<Long, IEventDetailView> {

    void loadDetails(boolean forceReload);

}
