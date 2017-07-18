package org.fossasia.openevent.app.common.contract.presenter;

public interface IDetailPresenter<K, V> extends IBasePresenter<V> {

    void attachKey(K key);

}
