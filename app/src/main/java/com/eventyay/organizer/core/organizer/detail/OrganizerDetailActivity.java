package com.eventyay.organizer.core.organizer.detail;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;

import com.eventyay.organizer.R;
import com.eventyay.organizer.core.organizer.update.UpdateOrganizerInfoFragment;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

import static com.eventyay.organizer.core.organizer.detail.OrganizerDetailFragment.INFO_FRAGMENT_TAG;

public class OrganizerDetailActivity extends AppCompatActivity implements HasSupportFragmentInjector {

    private final FragmentManager fragmentManager = getSupportFragmentManager();

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_detail_activity);

        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                .replace(R.id.fragment, new OrganizerDetailFragment())
                .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (fragmentManager.getBackStackEntryCount() > 0)
                    fragmentManager.popBackStack();
                else
                    finish();
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
        UpdateOrganizerInfoFragment infoFragment = (UpdateOrganizerInfoFragment) fragmentManager.findFragmentByTag(INFO_FRAGMENT_TAG);
        if (infoFragment != null) {
            infoFragment.backPressed();
        } else {
            super.onBackPressed();
        }
    }
}
