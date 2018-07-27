package com.eventyay.organizer.core.speaker.details;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.eventyay.organizer.R;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class SpeakerDetailsActivity extends AppCompatActivity implements HasSupportFragmentInjector {
    public static final String SPEAKER_ID = "speaker_id";

    private final FragmentManager fragmentManager = getSupportFragmentManager();

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speaker_details_activity);

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(SPEAKER_ID) && savedInstanceState == null) {
            fragmentManager.beginTransaction()
                .add(R.id.fragment, SpeakerDetailsFragment.newInstance(extras.getLong(SPEAKER_ID)))
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
}
