package com.eventyay.organizer.core.auth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.eventyay.organizer.R;
import com.eventyay.organizer.core.auth.reset.ResetPasswordFragment;
import com.eventyay.organizer.core.auth.start.StartFragment;
import com.eventyay.organizer.utils.LinkHandler;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import javax.inject.Inject;

public class AuthActivity extends AppCompatActivity implements HasSupportFragmentInjector {

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @Inject DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_Main_Light);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.auth_activity);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new StartFragment())
                    .commit();
        }

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String appLinkAction = intent.getAction();
        Uri appLinkData = intent.getData();

        if (Intent.ACTION_VIEW.equals(appLinkAction) && appLinkData != null) {
            LinkHandler.Destination destination =
                    LinkHandler.getDestinationAndToken(appLinkData.toString()).getDestination();
            String token = LinkHandler.getDestinationAndToken(appLinkData.toString()).getToken();

            if (destination.equals(LinkHandler.Destination.RESET_PASSWORD)) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, ResetPasswordFragment.newInstance(token))
                        .commit();
            }
        }
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }
}
