package org.fossasia.openevent.app.event;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.event.attendees.AttendeesFragment;
import org.fossasia.openevent.app.event.detail.EventDetailsFragment;
import org.fossasia.openevent.app.events.EventsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventContainerActivity extends AppCompatActivity {

    @BindView(R.id.navigation)
    BottomNavigationView navigation;

    private Event initialEvent;

    public static final String FRAG_DETAILS = "details";
    public static final String FRAG_ATTENDEES = "attendees";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = item -> {
            switch (item.getItemId()) {
                case R.id.navigation_details:
                    loadFragment(FRAG_DETAILS);
                    return true;
                case R.id.navigation_attendees:
                    loadFragment(FRAG_ATTENDEES);
                    return true;
                default:
                    loadFragment(FRAG_DETAILS);
                    return true;
            }
        };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_container);

        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initialEvent = getIntent().getParcelableExtra(EventsActivity.EVENT_KEY);

        setTitle(initialEvent.getName());

        if(savedInstanceState == null) {
            loadFragment(FRAG_DETAILS);
        }

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void loadFragment(String tag) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fg = fm.findFragmentByTag(tag);
        if(fg == null) {
            switch (tag) {
                case FRAG_DETAILS:
                    fg = EventDetailsFragment.newInstance(initialEvent);
                    break;
                case FRAG_ATTENDEES:
                    fg = AttendeesFragment.newInstance(initialEvent.getId());
                    break;
                default:
                    fg = EventDetailsFragment.newInstance(initialEvent);
                    break;
            }
        }
        fm.beginTransaction()
            .replace(R.id.event_container, fg, tag)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit();
    }
}
