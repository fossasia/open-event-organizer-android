package org.fossasia.openevent.app.common.lifecycle;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.squareup.leakcanary.RefWatcher;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.common.contract.presenter.IBasePresenter;
import org.fossasia.openevent.app.common.lifecycle.loader.IPresenterProvider;

import timber.log.Timber;

public abstract class BaseFragment<P extends IBasePresenter> extends Fragment implements IPresenterProvider<P> {

    private LoaderHandler<P> loaderHandler = new LoaderHandler<>();

    @Override
    @CallSuper
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loaderHandler.load(getContext(), getLoaderManager(), getLoaderId(), getPresenterProvider());
    }

    @Override
    @CallSuper
    public void onStop() {
        super.onStop();
        loaderHandler.presenter.detach();
    }

    @Override
    public P getPresenter() {
        return loaderHandler.getPresenter();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = OrgaApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

}
