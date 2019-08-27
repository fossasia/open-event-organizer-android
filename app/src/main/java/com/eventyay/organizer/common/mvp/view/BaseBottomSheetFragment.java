package com.eventyay.organizer.common.mvp.view;

import com.eventyay.organizer.common.di.Injectable;
import com.eventyay.organizer.common.mvp.presenter.BasePresenter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import dagger.Lazy;

public abstract class BaseBottomSheetFragment<P extends BasePresenter>
        extends BottomSheetDialogFragment implements Injectable {

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
        if (presenter != null) presenter.detach();
    }
}
