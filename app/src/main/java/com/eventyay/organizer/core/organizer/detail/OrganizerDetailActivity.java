package com.eventyay.organizer.core.organizer.detail;

import static com.eventyay.organizer.core.organizer.detail.OrganizerDetailFragment.INFO_FRAGMENT_TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.eventyay.organizer.R;
import com.eventyay.organizer.core.organizer.update.UpdateOrganizerInfoFragment;
import com.eventyay.organizer.utils.LinkHandler;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import javax.inject.Inject;

public class OrganizerDetailActivity extends AppCompatActivity
        implements HasSupportFragmentInjector {

    private final FragmentManager fragmentManager = getSupportFragmentManager();

    @Inject DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_detail_activity);

        if (savedInstanceState == null) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment, new OrganizerDetailFragment())
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

            if (destination.equals(LinkHandler.Destination.VERIFY_EMAIL)) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment, OrganizerDetailFragment.newInstance(token))
                        .commit();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (fragmentManager.getBackStackEntryCount() > 0) fragmentManager.popBackStack();
                else finish();
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }

    @Override
    public void onBackPressed() {
        UpdateOrganizerInfoFragment infoFragment =
                (UpdateOrganizerInfoFragment) fragmentManager.findFragmentByTag(INFO_FRAGMENT_TAG);
        if (infoFragment != null) {
            infoFragment.backPressed();
        } else {
            super.onBackPressed();
        }
    }
}
