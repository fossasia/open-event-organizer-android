package org.fossasia.openevent.app.common.mvp.view;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.common.di.Injectable;
import org.fossasia.openevent.app.common.mvp.presenter.BasePresenter;

import dagger.Lazy;

public abstract class BaseBottomSheetFragment<P extends BasePresenter> extends BottomSheetDialogFragment implements Injectable {

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
