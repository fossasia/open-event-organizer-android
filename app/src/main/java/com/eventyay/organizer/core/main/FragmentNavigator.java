package com.eventyay.organizer.core.main;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.eventyay.organizer.R;
import com.eventyay.organizer.core.attendee.list.AttendeesFragment;
import com.eventyay.organizer.core.event.dashboard.EventDashboardFragment;
import com.eventyay.organizer.core.event.list.EventListFragment;
import com.eventyay.organizer.core.faq.list.FaqListFragment;
import com.eventyay.organizer.core.feedback.list.FeedbackListFragment;
import com.eventyay.organizer.core.orders.create.CreateOrderFragment;
import com.eventyay.organizer.core.orders.list.OrdersFragment;
import com.eventyay.organizer.core.role.list.RoleListFragment;
import com.eventyay.organizer.core.settings.EventSettingsFragment;
import com.eventyay.organizer.core.settings.SettingsFragment;
import com.eventyay.organizer.core.share.ShareEventFragment;
import com.eventyay.organizer.core.speaker.list.SpeakersFragment;
import com.eventyay.organizer.core.speakerscall.detail.SpeakersCallFragment;
import com.eventyay.organizer.core.sponsor.list.SponsorsFragment;
import com.eventyay.organizer.core.ticket.list.TicketsFragment;
import com.eventyay.organizer.core.track.list.TracksFragment;

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

    int back() {
        int count = fragmentManager.getBackStackEntryCount();
        fragmentManager.popBackStack();
        if(count == 1) {
            lastSelectedNavItemId = R.id.nav_dashboard;
            dashboardActive = true;
        }
        return lastSelectedNavItemId;
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
            /*case R.id.nav_sell:
                fragment = CreateOrderFragment.newInstance(eventId);
                break;*/
            case R.id.nav_attendees:
                fragment = AttendeesFragment.newInstance(eventId);
                break;
            case R.id.nav_tickets:
                fragment = TicketsFragment.newInstance(eventId);
                break;
            case R.id.nav_orders:
                fragment = OrdersFragment.newInstance(eventId);
                break;
            case R.id.nav_event_settings:
                fragment = EventSettingsFragment.newInstance(eventId);
                break;
            case R.id.nav_events:
                fragment = EventListFragment.newInstance();
                break;
            case R.id.nav_settings:
                fragment = SettingsFragment.newInstance();
                break;
            /*case R.id.nav_faq:
                fragment = FaqListFragment.newInstance(eventId);
                break;
            case R.id.nav_feedback:
                fragment = FeedbackListFragment.newInstance(eventId);
                break;
            case R.id.nav_roles:
                fragment = RoleListFragment.newInstance(eventId);
                break;
            case R.id.nav_track:
                fragment = TracksFragment.newInstance(eventId);
                break;
            case R.id.nav_sponsor:
                fragment = SponsorsFragment.newInstance(eventId);
                break;
            case R.id.nav_speaker:
                fragment = SpeakersFragment.newInstance(eventId);
                break;
            case R.id.nav_speakers_call:
                fragment = SpeakersCallFragment.newInstance(eventId);
                break;*/
            case R.id.nav_share:
                fragment = ShareEventFragment.newInstance(eventId);
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
            transaction.replace(R.id.fragment_container, fragment).addToBackStack(null);
        }
        transaction.commit();
    }
}
