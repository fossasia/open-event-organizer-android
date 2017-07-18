package org.fossasia.openevent.app.common.contract.presenter;

public interface IBasePresenter<V> {

    void start();

    void detach();

    void attach(V view);

}
