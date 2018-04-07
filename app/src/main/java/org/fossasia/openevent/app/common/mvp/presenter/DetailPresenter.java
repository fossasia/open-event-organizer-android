package org.fossasia.openevent.app.common.mvp.presenter;

public interface DetailPresenter<K, V> extends BasePresenter {

    void attach(K key, V view);

}
