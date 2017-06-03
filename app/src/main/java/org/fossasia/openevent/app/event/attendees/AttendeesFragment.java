package org.fossasia.openevent.app.event.attendees;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.BaseFragment;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.event.attendees.contract.IAttendeesPresenter;
import org.fossasia.openevent.app.event.attendees.contract.IAttendeesView;
import org.fossasia.openevent.app.events.EventListActivity;
import org.fossasia.openevent.app.qrscan.ScanQRActivity;
import org.fossasia.openevent.app.utils.Constants;
import org.fossasia.openevent.app.utils.ViewUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AttendeesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AttendeesFragment extends BaseFragment implements IAttendeesView {

    @BindView(R.id.rvAttendeeList)
    RecyclerView recyclerView;

    @BindView(R.id.btnScanQr)
    Button btnBarCodeScanner;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private Context context;

    private long eventId;

    private AttendeeListAdapter attendeeListAdapter;

    public static final int REQ_CODE = 123;

    @Inject
    IAttendeesPresenter attendeesPresenter;

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
        View view = inflater.inflate(R.layout.fragment_attendees, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        attendeeListAdapter = new AttendeeListAdapter(context, attendeesPresenter);
        recyclerView.setAdapter(attendeeListAdapter);
        recyclerView.setLayoutManager(layoutManager);

        btnBarCodeScanner.setOnClickListener(v -> {
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;
        if (requestCode == REQ_CODE) {
            Attendee attendee = data.getParcelableExtra(Constants.SCANNED_ATTENDEE);
            if (attendee != null)
                attendeeListAdapter.showToggleDialog(attendeesPresenter, attendee);
        }
    }

    @Override
    public void showProgressBar(boolean show) {
        ViewUtils.showView(progressBar, show);
    }

    @Override
    public void showScanButton(boolean show) {
        ViewUtils.showView(btnBarCodeScanner, show);
    }

    @Override
    public void showAttendees(List<Attendee> attendees) {
        // The list is loaded from presenter, so we just need
        // to notify RecyclerView to update the data
        attendeeListAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateAttendee(int position, Attendee attendee) {
        // The attendee is saved correctly in list by presenter, so we
        // just need to notify RecyclerView that an item has changed
        if(position == -1) {
            attendeeListAdapter.notifyDataSetChanged();
            return;
        }
        attendeeListAdapter.notifyItemChanged(position);
    }

    @Override
    public void showErrorMessage(String error) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
    }

}
