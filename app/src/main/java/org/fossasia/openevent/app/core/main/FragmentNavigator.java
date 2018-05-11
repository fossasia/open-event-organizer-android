package org.fossasia.openevent.app.core.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.core.attendee.list.AttendeesFragment;
import org.fossasia.openevent.app.core.event.dashboard.EventDashboardFragment;
import org.fossasia.openevent.app.core.event.list.EventListFragment;
import org.fossasia.openevent.app.core.faq.list.FaqListFragment;
import org.fossasia.openevent.app.core.feedback.list.FeedbackListFragment;
import org.fossasia.openevent.app.core.settings.SettingsFragment;
import org.fossasia.openevent.app.core.sponsor.list.SponsorsFragment;
import org.fossasia.openevent.app.core.ticket.list.TicketsFragment;
import org.fossasia.openevent.app.core.track.list.TracksFragment;

class FragmentNavigator {

    private final FragmentManager fragmentManager;
    private long eventId;

    private boolean dashboardActive = true;
    private int lastSelectedNavItemId;

    FragmentNavigator(FragmentManager fragmentManager, long eventId) {
        this.fragmentManager = fragmentManager;
        this.eventId = eventId;
    }

    public boolean isDashboardActive() {
        return dashboardActive;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    void back() {
        fragmentManager.popBackStack();
        lastSelectedNavItemId = R.id.nav_dashboard;
        dashboardActive = true;
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.StdCyclomaticComplexity"})
    void loadFragment(int navItemId) {
        if (lastSelectedNavItemId == navItemId)
            return;

        lastSelectedNavItemId = navItemId;

        Fragment fragment;
        switch (navItemId) {
            case R.id.nav_dashboard:
                fragment = EventDashboardFragment.newInstance(eventId);
                break;
            case R.id.nav_attendees:
                fragment = AttendeesFragment.newInstance(eventId);
                break;
            case R.id.nav_tickets:
                fragment = TicketsFragment.newInstance(eventId);
                break;
            case R.id.nav_events:
                fragment = EventListFragment.newInstance();
                break;
            case R.id.nav_settings:
                fragment = SettingsFragment.newInstance();
                break;
            case R.id.nav_faq:
                fragment = FaqListFragment.newInstance(eventId);
                break;
            case R.id.nav_feedback:
                fragment = FeedbackListFragment.newInstance(eventId);
                break;
            case R.id.nav_track:
                fragment = TracksFragment.newInstance(eventId);
                break;
            case R.id.nav_sponsor:
                fragment = SponsorsFragment.newInstance(eventId);
                break;
            default:
                fragment = EventDashboardFragment.newInstance(eventId);
                break;
        }

        fragmentManager.popBackStack();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        dashboardActive = navItemId == R.id.nav_dashboard;
        if (dashboardActive) {
            transaction.replace(R.id.fragment_container, fragment);
        } else {
            transaction.add(R.id.fragment_container, fragment).addToBackStack(null);
        }
        transaction.commit();
    }
}
