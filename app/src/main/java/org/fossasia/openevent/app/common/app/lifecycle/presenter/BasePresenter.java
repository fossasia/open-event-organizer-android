package org.fossasia.openevent.app.common.app.lifecycle.presenter;

import android.support.annotation.CallSuper;

import org.fossasia.openevent.app.common.app.lifecycle.contract.presenter.IPresenter;

import io.reactivex.disposables.CompositeDisposable;

public abstract class BasePresenter<V> implements IPresenter<V> {
    private V view;
    private CompositeDisposable compositeDisposable;

    private int attachCount;

    @Override
    @CallSuper
    public void attach(V view) {
        this.view = view;
        this.compositeDisposable = new CompositeDisposable();
        attachCount++;
    }

    @Override
    @CallSuper
    public void detach() {
        view = null;
        compositeDisposable.dispose();
    }

    protected V getView() {
        return view;
    }

    protected CompositeDisposable getDisposable() {
        return compositeDisposable;
    }

    protected boolean isRotated() {
        return attachCount > 1;
    }

}
