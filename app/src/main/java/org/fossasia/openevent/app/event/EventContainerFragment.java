package org.fossasia.openevent.app.event;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.BaseFragment;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.event.attendees.AttendeesFragment;
import org.fossasia.openevent.app.event.detail.EventDetailsFragment;
import org.fossasia.openevent.app.events.EventListActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventContainerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventContainerFragment extends BaseFragment {

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

    public EventContainerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param initialEvent an event for which the fragment is created.
     * @return A new instance of fragment EventContainerFragment.
     */
    public static EventContainerFragment newInstance(Event initialEvent) {
        EventContainerFragment fragment = new EventContainerFragment();
        Bundle args = new Bundle();
        args.putParcelable(EventListActivity.EVENT_KEY, initialEvent);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            initialEvent = getArguments().getParcelable(EventListActivity.EVENT_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_event_container, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if(savedInstanceState == null) {
            loadFragment(FRAG_DETAILS);
        }
    }

    private void loadFragment(String tag) {
        FragmentManager fm = getChildFragmentManager();
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
