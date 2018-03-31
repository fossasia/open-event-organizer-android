package org.fossasia.openevent.app.common.mvp.presenter;

public interface IPresenter<V> extends IBasePresenter {

    void attach(V view);

}
