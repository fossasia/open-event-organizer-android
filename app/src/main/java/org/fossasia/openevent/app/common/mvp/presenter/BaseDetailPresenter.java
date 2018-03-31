package org.fossasia.openevent.app.common.mvp.presenter;

import android.support.annotation.CallSuper;

public abstract class BaseDetailPresenter<K, V> extends BasePresenter<V> implements IDetailPresenter<K, V> {

    private K id;

    @Override
    @CallSuper
    public void attach(K id, V view) {
        super.attach(view);
        this.id = id;
    }

    protected K getId() {
        return id;
    }

}
