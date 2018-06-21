package org.fossasia.openevent.app.common.mvp.view;

import android.support.design.widget.BottomSheetDialogFragment;

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

    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    protected Lazy<P> getPresenterProvider() {
        return null;
    }

    @SuppressWarnings("PMD.NullAssignment")
    protected P getPresenter() {
        Lazy<P> provider = getPresenterProvider();
        return (provider == null) ? null : provider.get();
    }

    @Override
    public void onStop() {
        super.onStop();
        P presenter = getPresenter();
        if (presenter != null)
            presenter.detach();
    }
}
