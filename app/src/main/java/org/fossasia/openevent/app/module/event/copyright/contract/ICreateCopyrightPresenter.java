package org.fossasia.openevent.app.module.event.copyright.contract;

import org.fossasia.openevent.app.common.app.lifecycle.contract.presenter.IPresenter;
import org.fossasia.openevent.app.common.data.models.Copyright;

public interface ICreateCopyrightPresenter extends IPresenter<ICreateCopyrightView> {

    void createCopyright();

    Copyright getCopyright();
}
