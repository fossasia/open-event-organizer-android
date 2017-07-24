package org.fossasia.openevent.app.module.main.contract;

import org.fossasia.openevent.app.common.app.lifecycle.contract.presenter.IPresenter;

public interface IMainPresenter extends IPresenter<IMainView> {

    void logout();

}
