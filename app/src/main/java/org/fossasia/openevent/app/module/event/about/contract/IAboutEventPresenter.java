package org.fossasia.openevent.app.module.event.about.contract;

import org.fossasia.openevent.app.common.app.lifecycle.contract.presenter.IDetailPresenter;
import org.fossasia.openevent.app.common.data.models.Copyright;

public interface IAboutEventPresenter extends IDetailPresenter<Long, IAboutEventVew> {

    void loadEvent(boolean forceReload);

    void loadCopyright(boolean forceReload);

    Copyright getCopyright();
}
