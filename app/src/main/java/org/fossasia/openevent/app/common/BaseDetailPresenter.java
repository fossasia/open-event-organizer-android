package org.fossasia.openevent.app.common;

import org.fossasia.openevent.app.common.contract.presenter.IDetailPresenter;

public  abstract class BaseDetailPresenter<K, V> extends BasePresenter<V> implements IDetailPresenter<K, V> {

    private K id;

    @Override
    public void attachKey(K key) {
        id = key;
    }

    protected K getId() {
        return id;
    }

}
