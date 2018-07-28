package com.eventyay.organizer.common.di.module.android;

import com.eventyay.organizer.core.auth.login.LoginFragment;
import com.eventyay.organizer.core.auth.reset.ResetPasswordFragment;
import com.eventyay.organizer.core.auth.signup.SignUpFragment;
import com.eventyay.organizer.core.auth.start.StartFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class AuthFragmentBuildersModule {

    @ContributesAndroidInjector
    abstract StartFragment contributeStartFragment();

    @ContributesAndroidInjector
    abstract ResetPasswordFragment contributeResetPasswordFragment();

    @ContributesAndroidInjector
    abstract LoginFragment contributeLoginFragment();

    @ContributesAndroidInjector
    abstract SignUpFragment contributeSignupFragment();

}
