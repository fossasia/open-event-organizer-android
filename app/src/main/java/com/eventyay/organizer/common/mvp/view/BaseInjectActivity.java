package com.eventyay.organizer.common.mvp.view;

import androidx.fragment.app.Fragment;
import com.eventyay.organizer.common.mvp.presenter.BasePresenter;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import javax.inject.Inject;

public abstract class BaseInjectActivity<P extends BasePresenter> extends BaseActivity<P>
        implements HasSupportFragmentInjector {

    @Inject DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }
}
