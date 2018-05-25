package org.fossasia.openevent.app.core.auth;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.core.auth.login.LoginFragment;
import org.fossasia.openevent.app.ui.BackPressHandler;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class AuthActivity extends AppCompatActivity implements HasSupportFragmentInjector {

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;
    @Inject
    BackPressHandler backPressHandler;
    FragmentManager fm = getSupportFragmentManager();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.auth_activity);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            fm.beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment())
                .commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (fm.getBackStackEntryCount() == 0)
            backPressHandler.onBackPressed(this, super::onBackPressed);
        else
            fm.popBackStack();
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }
}
