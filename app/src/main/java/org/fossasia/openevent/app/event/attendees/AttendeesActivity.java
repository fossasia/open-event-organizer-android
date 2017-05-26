package org.fossasia.openevent.app.event.attendees;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.data.EventDataRepository;
import org.fossasia.openevent.app.data.UtilModel;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.event.attendees.contract.IAttendeesPresenter;
import org.fossasia.openevent.app.event.attendees.contract.IAttendeesView;
import org.fossasia.openevent.app.utils.ViewUtils;
import org.fossasia.openevent.app.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AttendeesActivity extends AppCompatActivity implements IAttendeesView {

    @BindView(R.id.rvAttendeeList)
    RecyclerView recyclerView;

    @BindView(R.id.btnScanQr)
    Button btnBarCodeScanner;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private AttendeeListAdapter attendeeListAdapter;

    public static final int REQ_CODE = 123;
    public static final String ATTENDEES_KEY = "attendees";

    private IAttendeesPresenter attendeesPresenter;

    // Lifecycle methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendee_list);

        ButterKnife.bind(this);

        Intent i = getIntent();
        long id = i.getLongExtra("id", 0);

        attendeesPresenter = new AttendeesPresenter(id, this, new EventDataRepository(new UtilModel(this)));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        attendeeListAdapter = new AttendeeListAdapter(this, attendeesPresenter);
        recyclerView.setAdapter(attendeeListAdapter);
        recyclerView.setLayoutManager(layoutManager);

        attendeesPresenter.attach();

        btnBarCodeScanner.setOnClickListener(v -> {
            Intent scanQr = new Intent(AttendeesActivity.this, ScanQRActivity.class);
            scanQr.putParcelableArrayListExtra(ATTENDEES_KEY, (ArrayList<Attendee>) attendeesPresenter.getAttendees());
            startActivityForResult(scanQr, 123);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        attendeesPresenter.detach();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (requestCode == REQ_CODE) {
                String identifier = data.getStringExtra(Constants.SCANNED_IDENTIFIER);
                long id = data.getLongExtra(Constants.SCANNED_ID, 0);
                int index = data.getIntExtra(Constants.SCANNED_INDEX, -1);
                if (index != -1)
                    attendeeListAdapter.showToggleDialog(attendeesPresenter,
                        attendeesPresenter.getAttendees().get(index));
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // View implementation start

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
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }
}
