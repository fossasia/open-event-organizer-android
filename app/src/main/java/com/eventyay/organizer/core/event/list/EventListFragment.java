package com.eventyay.organizer.core.event.list;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseFragment;
import com.eventyay.organizer.core.event.create.CreateEventActivity;
import com.eventyay.organizer.core.event.list.pager.ListPageFragment;
import com.eventyay.organizer.core.notification.list.NotificationsFragment;
import com.eventyay.organizer.data.ContextUtils;
import com.eventyay.organizer.databinding.EventListFragmentBinding;
import com.eventyay.organizer.ui.ViewUtils;
import com.google.android.material.appbar.AppBarLayout;

import javax.inject.Inject;

import static com.eventyay.organizer.core.event.list.EventsViewModel.SORTBYDATE;
import static com.eventyay.organizer.core.event.list.EventsViewModel.SORTBYNAME;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class EventListFragment extends BaseFragment implements EventsView {

    private static final int ACTION_BAR_ELEVATION = 4;

    @Inject
    ContextUtils utilModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    EventsViewModel eventsViewModel;

    private EventListFragmentBinding binding;
    private SwipeRefreshLayout refreshLayout;
    private AppBarLayout appBarLayout;

    public static final String[] EVENT_TYPE = {"live", "past", "draft"};
    public static final String POSITION = "position";

    public static EventListFragment newInstance() {
        return new EventListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.event_list_fragment, container, false);

        appBarLayout = getActivity().findViewById(R.id.main_app_bar);

        eventsViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(EventsViewModel.class);
        eventsViewModel.getSuccess().observe(this, this::onRefreshComplete);
        eventsViewModel.getError().observe(this, this::showError);
        eventsViewModel.getProgress().observe(this, this::showProgress);

        eventsViewModel.loadUserEvents(false);

        binding.tab.setupWithViewPager(binding.pager);

        binding.pager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public int getCount() {
                return EVENT_TYPE.length;
            }

            @Override
            public Fragment getItem(int position) {
                Fragment fragment = ListPageFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putInt(POSITION, position);
                fragment.setArguments(bundle);
                return fragment;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return EVENT_TYPE[position];
            }
        });

        binding.pager.setOnTouchListener((v, event) -> {
            binding.swipeContainer.setEnabled(false);
            if (event.getAction() == MotionEvent.ACTION_UP) {
                    binding.swipeContainer.setEnabled(true);
            }
            return false;
        });

        binding.createEventFab.setOnClickListener(view -> openCreateEventFragment());

        setHasOptionsMenu(true);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        setupRefreshListener();
        appBarLayout.setElevation(0);
        appBarLayout.setStateListAnimator(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        eventsViewModel.loadUserEvents(false);
    }

    public void openCreateEventFragment() {
        Intent intent = new Intent(getActivity(), CreateEventActivity.class);
        startActivity(intent);
    }

    public void openNotificationsFragment() {

        getFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, NotificationsFragment.newInstance())
            .addToBackStack(null)
            .commit();
    }

    @Override
    public void onStop() {
        super.onStop();
        refreshLayout.setOnRefreshListener(null);
        appBarLayout.setElevation(ViewUtils.dpToPx(getContext(), ACTION_BAR_ELEVATION));
    }

    private void setupRefreshListener() {
        refreshLayout = binding.swipeContainer;
        refreshLayout.setColorSchemeColors(utilModel.getResourceColor(R.color.color_accent));
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
            eventsViewModel.loadUserEvents(true);
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_events, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sortByEventName:
                eventsViewModel.sortBy(SORTBYNAME);
                return true;
            case R.id.sortByEventDate:
                eventsViewModel.sortBy(SORTBYDATE);
                return true;
            case R.id.notifications:
                openNotificationsFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected int getTitle() {
        return R.string.events;
    }

    @Override
    public void showProgress(boolean show) {
        ViewUtils.showView(binding.progressBar, show);
    }

    @Override
    public void onRefreshComplete(boolean success) {
        if (success) {
            ViewUtils.showSnackbar(binding.getRoot(), R.string.refresh_complete);
        }
    }

    @Override
    public void showError(String error) {
        ViewUtils.showSnackbar(binding.getRoot(), error);
    }

}
