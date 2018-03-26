package org.fossasia.openevent.app.common.mvp.presenter;

public interface IDetailPresenter<K, V> extends IBasePresenter {

    void attach(K key, V view);

}
