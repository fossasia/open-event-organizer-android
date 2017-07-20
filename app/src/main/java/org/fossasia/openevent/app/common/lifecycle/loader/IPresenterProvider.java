package org.fossasia.openevent.app.common.lifecycle.loader;

import org.fossasia.openevent.app.common.contract.presenter.IBasePresenter;

import dagger.Lazy;

public interface IPresenterProvider<P extends IBasePresenter> {
    Lazy<P> getPresenterProvider();

    int getLoaderId();

    P getPresenter();
}
