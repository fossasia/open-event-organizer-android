package org.fossasia.openevent.app.common;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.squareup.leakcanary.RefWatcher;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.common.contract.presenter.IBasePresenter;
import org.fossasia.openevent.app.common.loader.PresenterLoader;

import dagger.Lazy;
import timber.log.Timber;

public abstract class BaseFragment<P extends IBasePresenter> extends Fragment {

    protected P presenter;

    protected abstract Lazy<P> getPresenterProvider();

    protected abstract int getLoaderId();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Loader<P> loader = getLoaderManager().getLoader(getLoaderId());
        if (loader == null) {
            initLoader();
        } else {
            presenter = ((PresenterLoader<P>) loader).getPresenter();
        }
    }

    private void initLoader() {
        getLoaderManager().initLoader(getLoaderId(), null, new LoaderManager.LoaderCallbacks<P>() {
            @Override
            public Loader<P> onCreateLoader(int id, Bundle args) {
                return new PresenterLoader<>(getContext(), getPresenterProvider().get());
            }

            @Override
            public void onLoadFinished(Loader<P> loader, P presenter) {
                BaseFragment.this.presenter = presenter;
            }

            @Override
            public void onLoaderReset(Loader<P> loader) {
                BaseFragment.this.presenter = null;
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.detach();
    }

    protected void setTitle(String title) {
        Activity activity = getActivity();

        if (activity != null && activity instanceof AppCompatActivity) {
            ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
            if (actionBar != null)
                actionBar.setTitle(title);
            else
                Timber.e("No ActionBar found in Activity %s for Fragment %s", activity, this);
        } else {
            Timber.e("Fragment %s is not attached to any Activity", this);
        }

    }

    @Override public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = OrgaApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

}
