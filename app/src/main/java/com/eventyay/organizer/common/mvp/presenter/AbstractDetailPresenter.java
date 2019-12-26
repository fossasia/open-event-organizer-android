package com.eventyay.organizer.common.mvp.presenter;

import androidx.annotation.CallSuper;

public abstract class AbstractDetailPresenter<K, V> extends AbstractBasePresenter<V> implements DetailPresenter<K, V> {

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
