package org.fossasia.openevent.app.common.di.module.android;

import org.fossasia.openevent.app.core.auth.forgot.request.ForgotPasswordFragment;
import org.fossasia.openevent.app.core.auth.forgot.submit.ResetPasswordByTokenFragment;
import org.fossasia.openevent.app.core.auth.login.LoginFragment;
import org.fossasia.openevent.app.core.auth.signup.SignUpFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class AuthFragmentBuildersModule {

    @ContributesAndroidInjector
    abstract ForgotPasswordFragment contributeForgotPasswordfragment();

    @ContributesAndroidInjector
    abstract ResetPasswordByTokenFragment contributeResetPasswordFragment();

    @ContributesAndroidInjector
    abstract LoginFragment contributeLoginFragment();

    @ContributesAndroidInjector
    abstract SignUpFragment contributeSignupFragment();

}
