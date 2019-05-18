package com.eventyay.organizer.common.mvp.view;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import com.eventyay.organizer.common.di.Injectable;
import com.eventyay.organizer.common.mvp.presenter.BasePresenter;
import com.eventyay.organizer.ui.ViewUtils;

import dagger.Lazy;

public abstract class BaseFragment<P extends BasePresenter> extends Fragment implements Injectable {

    protected abstract @StringRes int getTitle();

    @Override
    public void onResume() {
        super.onResume();
        setTitle(getString(getTitle()));
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

    protected void setTitle(String title) {
        ViewUtils.setTitle(this, title);
    }

    @Override
    public void onStop() {
        super.onStop();
        P presenter = getPresenter();
        if (presenter != null)
            presenter.detach();
    }
}
