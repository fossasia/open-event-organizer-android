package org.fossasia.openevent.app.common.app.lifecycle.view;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.common.app.lifecycle.contract.presenter.IBasePresenter;
import org.fossasia.openevent.app.common.app.lifecycle.view.loader.IPresenterProvider;
import org.fossasia.openevent.app.common.utils.ui.ViewUtils;

public abstract class BaseFragment<P extends IBasePresenter> extends Fragment implements IPresenterProvider<P> {

    private final LoaderHandler<P> loaderHandler = new LoaderHandler<>();

    protected abstract @StringRes
    int getTitle();

    @Override
    @CallSuper
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loaderHandler.load(getContext(), getLoaderManager(), getLoaderId(), getPresenterProvider());
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitle(getString(getTitle()));
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
        ViewUtils.setTitle(this, title);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OrgaApplication.getRefWatcher(getActivity()).watch(this);
    }

}
