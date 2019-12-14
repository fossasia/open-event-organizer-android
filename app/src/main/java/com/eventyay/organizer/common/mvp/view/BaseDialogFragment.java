package com.eventyay.organizer.common.mvp.view;

import androidx.fragment.app.DialogFragment;

import com.eventyay.organizer.common.di.Injectable;
import com.eventyay.organizer.common.mvp.presenter.BasePresenter;

import dagger.Lazy;

public class BaseDialogFragment<P extends BasePresenter> extends DialogFragment implements Injectable {

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
