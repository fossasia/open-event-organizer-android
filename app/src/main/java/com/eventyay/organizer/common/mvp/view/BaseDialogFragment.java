package com.eventyay.organizer.common.mvp.view;

import android.support.v4.app.DialogFragment;

import com.eventyay.organizer.OrgaApplication;
import com.eventyay.organizer.common.di.Injectable;
import com.eventyay.organizer.common.mvp.presenter.BasePresenter;

import dagger.Lazy;

public class BaseDialogFragment<P extends BasePresenter> extends DialogFragment implements Injectable {

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

