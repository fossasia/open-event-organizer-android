package org.fossasia.openevent.app.common.mvp.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import org.fossasia.openevent.app.common.mvp.presenter.BasePresenter;

import dagger.Lazy;

public abstract class BaseActivity<P extends BasePresenter> extends AppCompatActivity {

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
