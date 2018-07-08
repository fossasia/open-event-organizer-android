package org.fossasia.openevent.app.core.auth;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.fossasia.openevent.app.R;

import javax.inject.Inject;

import androidx.navigation.Navigation;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.auth_activity);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            Navigation.findNavController(this, R.id.auth_fragment_container).navigate(R.id.loginFragment);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return Navigation.findNavController(this, R.id.auth_fragment_container).navigateUp();
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }
}
