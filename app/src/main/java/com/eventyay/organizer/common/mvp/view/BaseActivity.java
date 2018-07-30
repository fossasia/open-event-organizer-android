package com.eventyay.organizer.common.mvp.view;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

import com.eventyay.organizer.common.mvp.presenter.BasePresenter;

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
