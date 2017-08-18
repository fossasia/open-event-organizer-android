package org.fossasia.openevent.app.module.event.about.contract;

import org.fossasia.openevent.app.common.app.lifecycle.contract.presenter.IDetailPresenter;

public interface IAboutEventPresenter extends IDetailPresenter<Long, IAboutEventVew> {

    void loadEvent(boolean forceReload);

}
