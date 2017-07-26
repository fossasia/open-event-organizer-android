package org.fossasia.openevent.app.common.app.lifecycle.view;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;

import com.squareup.leakcanary.RefWatcher;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.common.app.lifecycle.contract.presenter.IBasePresenter;
import org.fossasia.openevent.app.common.app.lifecycle.view.loader.IPresenterProvider;

public abstract class BaseBottomSheetFragment<P extends IBasePresenter> extends BottomSheetDialogFragment implements IPresenterProvider<P> {

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = OrgaApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

}
