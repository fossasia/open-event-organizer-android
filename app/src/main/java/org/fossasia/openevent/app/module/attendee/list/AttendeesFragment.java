package org.fossasia.openevent.app.module.attendee.list;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.Snackbar;
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
import android.widget.Toast;

import com.android.databinding.library.baseAdapters.BR;
import com.mikepenz.fastadapter.adapters.HeaderAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
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
import org.fossasia.openevent.app.module.main.MainActivity;
import org.fossasia.openevent.app.module.attendee.qrscan.ScanQRActivity;

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

    private FastItemAdapter<Attendee> fastItemAdapter;
    private StickyHeaderAdapter<Attendee> stickyHeaderAdapter;
    private RecyclerView.AdapterDataObserver adapterDataObserver;
    private FragmentAttendeesBinding binding;
    private SwipeRefreshLayout refreshLayout;

    private SearchView searchView;

    public AttendeesFragment() {
        // Required empty public constructor
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
        context = getActivity();

        OrgaApplication
            .getAppComponent()
            .inject(this);

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_attendees, menu);
        MenuItem search = menu.findItem(R.id.search);
        searchView = (SearchView) search.getActionView();
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
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_attendees, container, false);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {
            eventId = getArguments().getLong(MainActivity.EVENT_KEY);
            getPresenter().attach(eventId, this);
        }
        binding.setAttendees(getPresenter().getAttendees());
        getPresenter().start();

        setupRecyclerView();
        setupRefreshListener();

        binding.fabScanQr.setOnClickListener(v -> {
            Intent scanQr = new Intent(context, ScanQRActivity.class);
            scanQr.putExtra(MainActivity.EVENT_KEY, eventId);
            startActivity(scanQr);
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        refreshLayout.setOnRefreshListener(null);
        stickyHeaderAdapter.unregisterAdapterDataObserver(adapterDataObserver);
        if (searchView != null)
            searchView.setOnQueryTextListener(null);
    }

    @Override
    public Lazy<IAttendeesPresenter> getPresenterProvider() {
        return presenterProvider;
    }

    @Override
    public int getLoaderId() {
        return R.layout.fragment_attendees;
    }

    private void setupRecyclerView() {
        fastItemAdapter = new FastItemAdapter<>();
        fastItemAdapter.setHasStableIds(true);
        fastItemAdapter.withPositionBasedStateManagement(false);
        fastItemAdapter.withEventHook(new AttendeeItemCheckInEvent(this));
        fastItemAdapter.getItemFilter().withFilterPredicate(
            (attendee, query) ->
                SearchUtils.filter(
                    query.toString(),
                    attendee.getFirstname(),
                    attendee.getLastname(),
                    attendee.getEmail())
        );

        stickyHeaderAdapter = new StickyHeaderAdapter<>();
        final HeaderAdapter headerAdapter = new HeaderAdapter();

        RecyclerView recyclerView = binding.rvAttendeeList;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(stickyHeaderAdapter.wrap((headerAdapter.wrap(fastItemAdapter))));

        final StickyRecyclerHeadersDecoration decoration = new StickyRecyclerHeadersDecoration(stickyHeaderAdapter);
        recyclerView.addItemDecoration(decoration);

        adapterDataObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                decoration.invalidateHeaders();
            }
        };
        stickyHeaderAdapter.registerAdapterDataObserver(adapterDataObserver);

        ViewUtils.setRecyclerViewScrollAwareFabBehaviour(recyclerView, binding.fabScanQr);
    }

    private void setupRefreshListener() {
        refreshLayout = binding.swipeContainer;
        refreshLayout.setColorSchemeColors(utilModel.getResourceColor(R.color.color_accent));
        refreshLayout.setOnRefreshListener(() -> getPresenter().loadAttendees(true));
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
    public void onRefreshComplete() {
        refreshLayout.setRefreshing(false);
        Snackbar.make(binding.rvAttendeeList, R.string.refresh_complete, Snackbar.LENGTH_SHORT).show();
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
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
    }

}
