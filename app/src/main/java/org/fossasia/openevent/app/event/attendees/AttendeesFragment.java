package org.fossasia.openevent.app.event.attendees;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.databinding.library.baseAdapters.BR;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.HeaderAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.BaseFragment;
import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.databinding.FragmentAttendeesBinding;
import org.fossasia.openevent.app.event.attendees.contract.IAttendeesPresenter;
import org.fossasia.openevent.app.event.attendees.contract.IAttendeesView;
import org.fossasia.openevent.app.event.attendees.listeners.AttendeeItemCheckInEvent;
import org.fossasia.openevent.app.events.EventListActivity;
import org.fossasia.openevent.app.qrscan.ScanQRActivity;
import org.fossasia.openevent.app.utils.Constants;
import org.fossasia.openevent.app.utils.ViewUtils;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AttendeesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AttendeesFragment extends BaseFragment implements IAttendeesView {

    private Context context;

    private long eventId;

    private ItemAdapter<Attendee> itemAdapter;

    public static final int REQ_CODE = 123;

    @Inject
    IUtilModel utilModel;

    @Inject
    IAttendeesPresenter attendeesPresenter;

    private StickyHeaderAdapter stickyHeaderAdapter;
    private RecyclerView.AdapterDataObserver adapterDataObserver;
    private FragmentAttendeesBinding binding;
    private SwipeRefreshLayout refreshLayout;

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
        args.putLong(EventListActivity.EVENT_KEY, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    // Lifecycle methods

    @Override
    public void onCreate(Bundle savedInstanceState) {
        context = getActivity();

        OrgaApplication
            .getAppComponent(context)
            .inject(this);

        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            eventId = getArguments().getLong(EventListActivity.EVENT_KEY);
            attendeesPresenter.attach(eventId, this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_attendees, container, false);

        binding.setAttendees(attendeesPresenter.getAttendees());

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupRecyclerView();
        setupRefreshListener();

        binding.fabScanQr.setOnClickListener(v -> {
            Intent scanQr = new Intent(context, ScanQRActivity.class);
            scanQr.putExtra(EventListActivity.EVENT_KEY, eventId);
            startActivityForResult(scanQr, REQ_CODE);
        });

        attendeesPresenter.start();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        attendeesPresenter.detach();
        refreshLayout.setOnRefreshListener(null);
        stickyHeaderAdapter.unregisterAdapterDataObserver(adapterDataObserver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null || requestCode != REQ_CODE)
            return;
        long attendeeId = data.getLongExtra(Constants.SCANNED_ATTENDEE, -1);

        if (attendeeId != -1) {
            attendeesPresenter.getAttendeeById(attendeeId)
                .subscribe(this::showToggleDialog, Timber::e);
        }
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.rvAttendeeList;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        FastAdapter<Attendee> fastAdapter = new FastAdapter<>();

        stickyHeaderAdapter = new StickyHeaderAdapter();
        final HeaderAdapter headerAdapter = new HeaderAdapter();
        itemAdapter = new ItemAdapter<>();

        fastAdapter.setHasStableIds(true);
        fastAdapter.withEventHook(new AttendeeItemCheckInEvent(this));

        recyclerView.setAdapter(stickyHeaderAdapter.wrap(itemAdapter.wrap(headerAdapter.wrap(fastAdapter))));

        final StickyRecyclerHeadersDecoration decoration = new StickyRecyclerHeadersDecoration(stickyHeaderAdapter);
        recyclerView.addItemDecoration(decoration);

        adapterDataObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                decoration.invalidateHeaders();
            }
        };
        stickyHeaderAdapter.registerAdapterDataObserver(adapterDataObserver);
    }

    private void setupRefreshListener() {
        refreshLayout = binding.swipeContainer;
        refreshLayout.setColorSchemeColors(utilModel.getResourceColor(R.color.color_accent));
        refreshLayout.setOnRefreshListener(() -> attendeesPresenter.loadAttendees(true));
    }

    // View Implementation

    @Override
    public void showToggleDialog(Attendee attendee) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        String alertTitle;
        if(attendee.isCheckedIn())
            alertTitle = Constants.ATTENDEE_CHECKING_OUT;
        else
            alertTitle = Constants.ATTENDEE_CHECKING_IN;

        builder.setTitle(alertTitle).setMessage(attendee.getTicketMessage());
        builder.setPositiveButton("OK", (dialog, which) -> attendeesPresenter.toggleAttendeeCheckStatus(attendee))
            .setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    @Override
    public void showProgressBar(boolean show) {
        ViewUtils.showView(binding.progressBar, show);
    }

    @Override
    public void onRefreshComplete() {
        refreshLayout.setRefreshing(false);
        Snackbar.make(binding.rvAttendeeList, R.string.refresh_complete, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showScanButton(boolean show) {
        if (show)
            binding.fabScanQr.show();
        else
            binding.fabScanQr.hide();
    }

    @Override
    public void showAttendees(List<Attendee> attendees) {
        // The list is loaded from presenter, so we just need
        // to notify RecyclerView to update the data
        itemAdapter.set(attendees);
        binding.setVariable(BR.attendees, attendees);
        binding.executePendingBindings();
    }

    @Override
    public void updateAttendee(int position, Attendee attendee) {
        itemAdapter.set(position, attendee);
    }

    @Override
    public void showErrorMessage(String error) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
    }
}
