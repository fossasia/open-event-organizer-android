package org.fossasia.openevent.app.event.attendees;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.data.network.api.ApiCall;
import org.fossasia.openevent.app.data.network.interfaces.VolleyCallBack;
import org.fossasia.openevent.app.event.detail.EventDetailsActivity;
import org.fossasia.openevent.app.utils.Constants;
import org.fossasia.openevent.app.utils.Network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AttendeesActivity extends AppCompatActivity {

    @BindView(R.id.rvAttendeeList)
    RecyclerView recyclerView;

    @BindView(R.id.btnScanQr)
    Button btnBarCodeScanner;

    Attendee[] attendeeDetails;
    static ArrayList<Attendee> attendeeArrayList = new ArrayList<>();
    AttendeeListAdapter attendeeListAdapter;

    long id;
    public static final int REQ_CODE = 123;
    public static final String TAG = "AttendeeListActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendee_list);

        ButterKnife.bind(this);

        Intent i = getIntent();
        id = i.getLongExtra("id", 0);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        attendeeListAdapter = new AttendeeListAdapter(attendeeArrayList, this, id);
        recyclerView.setAdapter(attendeeListAdapter);
        recyclerView.setLayoutManager(layoutManager);
        getAttendees();
    }

    public void getAttendees() {
        if (Network.isNetworkConnected(this)) {
            ApiCall.callApi(this, Constants.EVENT_DETAILS + id + Constants.ATTENDEES
                , new VolleyCallBack() {
                    @Override
                    public void onSuccess(String result) {
                        Gson gson = new Gson();
                        attendeeDetails = gson.fromJson(result, Attendee[].class);
                        List<Attendee> attendeeDetailsesList = Arrays.asList(attendeeDetails);
                        attendeeArrayList.addAll(attendeeDetailsesList);
                        attendeeListAdapter.notifyDataSetChanged();
                        btnBarCodeScanner.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(VolleyError error) {

                    }
                });

            btnBarCodeScanner.setOnClickListener(v -> {
                Intent i = new Intent(AttendeesActivity.this, ScanQRActivity.class);
                startActivityForResult(i, 123);
            });
        } else {
            Toast.makeText(this, Constants.NO_NETWORK, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (requestCode == REQ_CODE) {
                String identifier = data.getStringExtra(Constants.SCANNED_IDENTIFIER);
                long id = data.getLongExtra(Constants.SCANNED_ID, 0);
                int index = data.getIntExtra(Constants.SCANNED_INDEX, -1);
                if (index != -1)
                    checkInAlertBuilder(index);
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void checkInAlertBuilder(final int index) {
        AlertDialog.Builder builder
            = new AlertDialog.Builder(this);
        String alertTitle;
        final Attendee thisAttendee = attendeeArrayList.get(index);
        if (thisAttendee.isCheckedIn()) {
            alertTitle = Constants.ATTENDEE_CHECKING_OUT;
        } else {
            alertTitle = Constants.ATTENDEE_CHECKING_IN;
        }

        builder.setTitle(alertTitle).setMessage(thisAttendee.getFirstname() + " "
            + thisAttendee.getLastname() + "\n"
            + "Ticket: " + thisAttendee.getTicket().getType())
            .setPositiveButton("OK", (dialog, which) -> {
                Log.d(TAG, "onClick: inside ok");
                changeCheckStatus(thisAttendee.getId(), index);
            }).setNegativeButton("CANCEL", (dialog, which) -> {

            });
        builder.create();
        builder.show();
    }

    public void changeCheckStatus(Long thisAttendeeId, final int position) {
        if (Network.isNetworkConnected(this)) {
            ApiCall.PostApiCall(this, Constants.EVENT_DETAILS + id + Constants.ATTENDEES_TOGGLE + thisAttendeeId, new VolleyCallBack() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, "onSuccess: " + result);
                    Gson gson = new Gson();
                    Attendee newAttendeeDetails = gson.fromJson(result, Attendee.class);
                    attendeeArrayList.set(position, newAttendeeDetails);
                    attendeeListAdapter.notifyDataSetChanged();
                }

                @Override
                public void onError(VolleyError error) {

                }
            });
        } else {
            Toast.makeText(this, Constants.NO_NETWORK, Toast.LENGTH_SHORT).show();
        }

    }
}
