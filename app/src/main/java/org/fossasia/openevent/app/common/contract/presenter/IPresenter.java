package org.fossasia.openevent.app.common.contract.presenter;

public interface IPresenter<V> extends IBasePresenter {

    void attach(V view);

}
