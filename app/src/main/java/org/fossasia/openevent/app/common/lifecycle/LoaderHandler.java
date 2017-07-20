package org.fossasia.openevent.app.common.lifecycle;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import org.fossasia.openevent.app.common.contract.presenter.IBasePresenter;
import org.fossasia.openevent.app.common.lifecycle.loader.PresenterLoader;

import dagger.Lazy;

class LoaderHandler<P extends IBasePresenter> {

    protected P presenter;

    void load(Context context, LoaderManager loaderManager, int loaderId, Lazy<P> presenterProvider) {
        Loader<P> loader = loaderManager.getLoader(loaderId);
        if (loader == null) {
            initLoader(context, loaderManager, loaderId, presenterProvider);
        } else {
            presenter = ((PresenterLoader<P>) loader).getPresenter();
        }
    }

    private void initLoader(Context context, LoaderManager loaderManager, int loaderId, Lazy<P> presenterProvider) {
        loaderManager.initLoader(loaderId, null, new LoaderManager.LoaderCallbacks<P>() {
            @Override
            public Loader<P> onCreateLoader(int id, Bundle args) {
                return new PresenterLoader<>(context, presenterProvider.get());
            }

            @Override
            public void onLoadFinished(Loader<P> loader, P presenter) {
                LoaderHandler.this.presenter = presenter;
            }

            @Override
            public void onLoaderReset(Loader<P> loader) {
                LoaderHandler.this.presenter = null;
            }
        });
    }

    protected P getPresenter() {
        return presenter;
    }
}
