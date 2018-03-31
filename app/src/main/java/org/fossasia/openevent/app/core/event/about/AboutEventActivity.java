package org.fossasia.openevent.app.core.event.about;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import org.fossasia.openevent.app.R;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class AboutEventActivity extends AppCompatActivity implements HasSupportFragmentInjector {

    public static final String EVENT_ID = "event_id";

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_event_activity);

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(EVENT_ID) && savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, AboutEventFragment.newInstance(extras.getLong(EVENT_ID)))
                .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
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
