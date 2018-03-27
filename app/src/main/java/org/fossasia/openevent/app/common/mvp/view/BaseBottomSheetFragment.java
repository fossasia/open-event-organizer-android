package org.fossasia.openevent.app.common.mvp.view;

import android.support.design.widget.BottomSheetDialogFragment;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.common.mvp.presenter.IBasePresenter;

import dagger.Lazy;

public abstract class BaseBottomSheetFragment<P extends IBasePresenter> extends BottomSheetDialogFragment {

    @Override
    public void onDestroy() {
        super.onDestroy();
        OrgaApplication.getRefWatcher(getActivity()).watch(this);
    }

    protected abstract Lazy<P> getPresenterProvider();

    protected P getPresenter() {
        return getPresenterProvider().get();
    }

    @Override
    public void onStop() {
        super.onStop();
        getPresenter().detach();
    }
}
