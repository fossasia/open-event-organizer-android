package org.fossasia.openevent.app.common;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import org.fossasia.openevent.app.common.contract.presenter.IBasePresenter;
import org.fossasia.openevent.app.common.loader.PresenterLoader;

import dagger.Lazy;

public abstract class BaseActivity<P extends IBasePresenter> extends AppCompatActivity {

    protected P presenter;

    protected abstract Lazy<P> getPresenterProvider();

    protected abstract int getLoaderId();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Loader<P> loader = getSupportLoaderManager().getLoader(getLoaderId());
        if (loader == null) {
            initLoader();
        } else {
            presenter = ((PresenterLoader<P>) loader).getPresenter();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.detach();
    }

    private void initLoader() {
        getSupportLoaderManager().initLoader(getLoaderId(), null, new LoaderManager.LoaderCallbacks<P>() {
            @Override
            public Loader<P> onCreateLoader(int id, Bundle args) {
                return new PresenterLoader<>(BaseActivity.this, getPresenterProvider().get());
            }

            @Override
            public void onLoadFinished(Loader<P> loader, P presenter) {
                BaseActivity.this.presenter = presenter;
            }

            @Override
            public void onLoaderReset(Loader<P> loader) {
                BaseActivity.this.presenter = null;
            }
        });
    }
}
