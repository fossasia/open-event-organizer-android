package org.fossasia.openevent.app.common.app.lifecycle.view.loader;

import org.fossasia.openevent.app.common.app.lifecycle.contract.presenter.IBasePresenter;

import dagger.Lazy;

public interface IPresenterProvider<P extends IBasePresenter> {
    Lazy<P> getPresenterProvider();

    int getLoaderId();

    P getPresenter();
}
