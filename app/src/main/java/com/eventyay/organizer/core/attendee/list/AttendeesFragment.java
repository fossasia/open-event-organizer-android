package com.eventyay.organizer.core.attendee.list;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.databinding.library.baseAdapters.BR;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseFragment;
import com.eventyay.organizer.core.attendee.checkin.AttendeeCheckInFragment;
import com.eventyay.organizer.core.attendee.list.listeners.AttendeeItemCheckInEvent;
import com.eventyay.organizer.core.attendee.qrscan.ScanQRActivity;
import com.eventyay.organizer.core.main.MainActivity;
import com.eventyay.organizer.data.ContextUtils;
import com.eventyay.organizer.data.attendee.Attendee;
import com.eventyay.organizer.databinding.FragmentAttendeesBinding;
import com.eventyay.organizer.ui.ViewUtils;
import com.eventyay.organizer.utils.SearchUtils;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AttendeesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

@SuppressWarnings("PMD.TooManyMethods")
public class AttendeesFragment extends BaseFragment<AttendeesPresenter> implements AttendeesView {

    private Context context;

    private long eventId;

    @Inject
    ContextUtils utilModel;

    @Inject
    Lazy<AttendeesPresenter> presenterProvider;

    private static final int SORTBYTICKET = 1;
    private static final int SORTBYNAME = 0;

    private FastAdapter<Attendee> fastAdapter;
    private StickyHeaderAdapter<Attendee> stickyHeaderAdapter;

    private ItemAdapter<Attendee> fastItemAdapter;
    private FragmentAttendeesBinding binding;
    private SwipeRefreshLayout refreshLayout;
    private SearchView searchView;

    private boolean initialized;

    private RecyclerView.AdapterDataObserver observer;

    private static final String FILTER_SYNC = "FILTER_SYNC";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param eventId id of the event.
     * @return A new instance of fragment AttendeesFragment.
     */
    public static AttendeesFragment newInstance(long eventId) {
        AttendeesFragment fragment = new AttendeesFragment();
        Bundle args = new Bundle();
        args.putLong(MainActivity.EVENT_KEY, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    // Lifecycle methods

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();
        setHasOptionsMenu(true);

        if (getArguments() != null)
            eventId = getArguments().getLong(MainActivity.EVENT_KEY);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (observer != null) {
            fastAdapter.unregisterAdapterDataObserver(observer);
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_attendees, menu);
        MenuItem search = menu.findItem(R.id.search);
        searchView = (SearchView) search.getActionView();
        setupSearchListener();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filterByNone:
                fastItemAdapter.filter("");
                return true;
            case R.id.filterBySync:
                fastItemAdapter.filter(FILTER_SYNC);
                return true;
            case R.id.sortByTicket:
                sortAttendees(SORTBYTICKET);
                return true;
            case R.id.sortByName:
                sortAttendees(SORTBYNAME);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sortAttendees(int sortBy) {
        if (sortBy == SORTBYTICKET) {
            fastItemAdapter.withComparator((Attendee a1, Attendee a2) -> a1.getTicket().getType().compareTo(a2.getTicket().getType()));
        } else {
            fastItemAdapter.withComparator((Attendee a1, Attendee a2) -> a1.getFirstname().compareTo(a2.getFirstname()), true);
        }
        fastItemAdapter.setNewList(getPresenter().getAttendees());
        stickyHeaderAdapter.setSortByName(sortBy == SORTBYTICKET);
        binding.setVariable(BR.attendees, getPresenter().getAttendees());
        binding.executePendingBindings();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_attendees, container, false);
        binding.fabScanQr.setOnClickListener(v -> {
            Intent scanQr = new Intent(context, ScanQRActivity.class);
            scanQr.putExtra(MainActivity.EVENT_KEY, eventId);
            startActivity(scanQr);
        });

        binding.fabScanQr.getDrawable().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        setupSearchListener();
        setupRefreshListener();
        setupRecyclerView();
        getPresenter().attach(eventId, this);
        binding.setAttendees(getPresenter().getAttendees());
        getPresenter().start();

        initialized = true;
    }

    @Override
    protected int getTitle() {
        return R.string.attendees;
    }

    @Override
    public void onStop() {
        super.onStop();
        refreshLayout.setOnRefreshListener(null);
        //stickyHeaderAdapter.unregisterAdapterDataObserver(adapterDataObserver);
        if (searchView != null)
            searchView.setOnQueryTextListener(null);
    }

    @Override
    public Lazy<AttendeesPresenter> getPresenterProvider() {
        return presenterProvider;
    }

    private void setupSearchListener() {
        if (searchView == null)
            return;

        searchView.setQueryHint(getString(R.string.search_placeholder));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                fastItemAdapter.filter(s.trim());
                return true;
            }
        });
    }

    private void setupRecyclerView() {
        if (!initialized) {
            fastItemAdapter = new ItemAdapter<>();
            fastItemAdapter.getItemFilter().withFilterPredicate(
                (attendee, query) -> {
                    if (query == null)
                        return true;

                    if (query.equals(FILTER_SYNC)) {
                        return attendee.checking;
                    }
                    return !SearchUtils.filter(
                        query.toString(),
                        attendee.getFirstname(),
                        attendee.getLastname(),
                        attendee.getEmail());
                }
            );

            stickyHeaderAdapter = new StickyHeaderAdapter<>();
            stickyHeaderAdapter.setSortByName(false);
            fastAdapter = FastAdapter.with(Arrays.asList(fastItemAdapter, stickyHeaderAdapter));
            fastAdapter.setHasStableIds(true);
            fastAdapter.withEventHook(new AttendeeItemCheckInEvent(this));

            RecyclerView recyclerView = binding.rvAttendeeList;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(fastAdapter);

            final StickyRecyclerHeadersDecoration decoration = new StickyRecyclerHeadersDecoration(stickyHeaderAdapter);
            recyclerView.addItemDecoration(decoration);
            observer = new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    decoration.invalidateHeaders();
                }
            };
            fastAdapter.registerAdapterDataObserver(observer);
            ViewUtils.setRecyclerViewScrollAwareFabBehaviour(recyclerView, binding.fabScanQr);
        }
    }

    private void setupRefreshListener() {
        refreshLayout = binding.swipeContainer;
        refreshLayout.setColorSchemeColors(utilModel.getResourceColor(R.color.color_accent));
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
            getPresenter().loadAttendees(true);
        });
    }

    // View Implementation

    @Override
    public void showToggleDialog(long attendeeId) {
        BottomSheetDialogFragment bottomSheetDialogFragment = AttendeeCheckInFragment.newInstance(attendeeId);
        bottomSheetDialogFragment.show(getFragmentManager(), bottomSheetDialogFragment.getTag());
    }

    @Override
    public void showProgress(boolean show) {
        ViewUtils.showView(binding.progressBar, show);
    }

    @Override
    public void onRefreshComplete(boolean success) {
        if (success)
            ViewUtils.showSnackbar(binding.rvAttendeeList, R.string.refresh_complete);
    }

    @Override
    public void showScanButton(boolean show) {
        ViewUtils.showView(binding.fabScanQr, show);
    }

    @Override
    public void showResults(List<Attendee> attendees) {
        fastItemAdapter.setNewList(attendees);
        binding.setVariable(BR.attendees, attendees);
        binding.executePendingBindings();
    }

    @Override
    public void showEmptyView(boolean show) {
        ViewUtils.showView(binding.emptyView, show);
    }

    @Override
    public void updateAttendee(Attendee attendee) {
        int position = fastItemAdapter.getAdapterPosition(attendee);
        fastItemAdapter.getItemFilter().set(position, attendee);
    }

    @Override
    public void showError(String error) {
        ViewUtils.showSnackbar(binding.getRoot(), error);
    }

}
