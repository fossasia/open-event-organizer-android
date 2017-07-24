package org.fossasia.openevent.app.common.app.lifecycle.contract.presenter;

public interface IPresenter<V> extends IBasePresenter {

    void attach(V view);

}
