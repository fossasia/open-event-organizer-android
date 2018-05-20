package org.fossasia.openevent.app.common.di.module;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import org.fossasia.openevent.app.common.di.OrgaViewModelFactory;
import org.fossasia.openevent.app.core.auth.login.LoginViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel.class)
    public abstract ViewModel bindLoginViewModel(LoginViewModel loginViewModel);

    @Binds
    public abstract ViewModelProvider.Factory bindViewModelFactory(OrgaViewModelFactory factory);

}
