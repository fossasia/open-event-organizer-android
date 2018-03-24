package org.fossasia.openevent.app.module.attendee.list;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
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

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.app.lifecycle.view.BaseFragment;
import org.fossasia.openevent.app.common.data.contract.IUtilModel;
import org.fossasia.openevent.app.common.data.models.Attendee;
import org.fossasia.openevent.app.common.utils.core.SearchUtils;
import org.fossasia.openevent.app.common.utils.ui.ViewUtils;
import org.fossasia.openevent.app.databinding.FragmentAttendeesBinding;
import org.fossasia.openevent.app.module.attendee.checkin.AttendeeCheckInFragment;
import org.fossasia.openevent.app.module.attendee.list.contract.IAttendeesPresenter;
import org.fossasia.openevent.app.module.attendee.list.contract.IAttendeesView;
import org.fossasia.openevent.app.module.attendee.list.listeners.AttendeeItemCheckInEvent;
import org.fossasia.openevent.app.module.attendee.qrscan.ScanQRActivity;
import org.fossasia.openevent.app.module.main.MainActivity;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AttendeesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AttendeesFragment extends BaseFragment<IAttendeesPresenter> implements IAttendeesView {

    private Context context;

    private long eventId;

    @Inject
    IUtilModel utilModel;

    @Inject
    Lazy<IAttendeesPresenter> presenterProvider;

    private ItemAdapter<Attendee> fastItemAdapter;
    private FragmentAttendeesBinding binding;
    private SwipeRefreshLayout refreshLayout;
    private SearchView searchView;

    private boolean initialized;

    private static final String FILTER_SYNC = "FILTER_SYNC";

    public AttendeesFragment() {
        OrgaApplication
            .getAppComponent()
            .inject(this);
    }

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
            default:
                return super.onOptionsItemSelected(item);
        }
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
    public Lazy<IAttendeesPresenter> getPresenterProvider() {
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
                        return attendee.checking.get();
                    }
                    return !SearchUtils.filter(
                        query.toString(),
                        attendee.getFirstname(),
                        attendee.getLastname(),
                        attendee.getEmail());
                }
            );

            StickyHeaderAdapter<Attendee> stickyHeaderAdapter = new StickyHeaderAdapter<>();

            FastAdapter<Attendee> fastAdapter = FastAdapter.with(Arrays.asList(fastItemAdapter, stickyHeaderAdapter));
            fastAdapter.setHasStableIds(true);
            fastAdapter.withEventHook(new AttendeeItemCheckInEvent(this));

            RecyclerView recyclerView = binding.rvAttendeeList;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(fastAdapter);

            final StickyRecyclerHeadersDecoration decoration = new StickyRecyclerHeadersDecoration(stickyHeaderAdapter);
            recyclerView.addItemDecoration(decoration);

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
