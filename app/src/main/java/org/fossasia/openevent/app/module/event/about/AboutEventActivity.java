package org.fossasia.openevent.app.module.event.about;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.module.event.about.contract.IAboutEventVew;

public class AboutEventActivity extends AppCompatActivity {

    public static final String EVENT_ID = "event_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_event_activity);

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(EVENT_ID)) {
            long id = extras.getLong(EVENT_ID);

            IAboutEventVew aboutEventVew = (IAboutEventVew) getSupportFragmentManager().findFragmentById(R.id.about_event_fragment);
            aboutEventVew.setEventId(id);
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

}
