package org.fossasia.openevent.app.common.app.lifecycle.view;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

import org.fossasia.openevent.app.common.app.lifecycle.contract.presenter.IBasePresenter;

import dagger.Lazy;

public abstract class BaseActivity<P extends IBasePresenter> extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    protected abstract Lazy<P> getPresenterProvider();

    protected P getPresenter() {
        return getPresenterProvider().get();
    }

    @Override
    protected void onStop() {
        super.onStop();
        getPresenter().detach();
    }
}
