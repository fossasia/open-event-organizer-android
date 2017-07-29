package org.fossasia.openevent.app.module.auth;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.utils.ui.BackPressHandler;
import org.fossasia.openevent.app.module.auth.login.LoginFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AuthActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Inject
    BackPressHandler backPressHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OrgaApplication
            .getAppComponent()
            .inject(this);

        setContentView(R.layout.home_activity);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new LoginFragment())
                .commit();
        }
    }

    @Override
    public void onBackPressed() {
        backPressHandler.onBackPressed(this, super::onBackPressed);
    }
}
