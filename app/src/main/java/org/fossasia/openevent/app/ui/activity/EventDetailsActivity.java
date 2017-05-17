package org.fossasia.openevent.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.models.Ticket;
import org.fossasia.openevent.app.data.network.api.ApiCall;
import org.fossasia.openevent.app.data.network.interfaces.IVolleyCallBack;
import org.fossasia.openevent.app.utils.Constants;
import org.fossasia.openevent.app.utils.Network;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventDetailsActivity extends AppCompatActivity {

    @BindView(R.id.tvEventTitle)
    TextView tvEventTitle;
    @BindView(R.id.tvStartDate)
    TextView tvStartDate;
    @BindView(R.id.tvEndDate)
    TextView tvEndDate;
    @BindView(R.id.tvHour)
    TextView tvTime;
    @BindView(R.id.tvTicketSold)
    TextView tvTicketSold;
    @BindView(R.id.tvAttendance)
    TextView tvAttendees;
    @BindView(R.id.tvTicketTotal)
    TextView tvTicketTotal;
    @BindView(R.id.progressTicketSold)
    ProgressBar pbTickets;
    @BindView(R.id.progressAttendance)
    ProgressBar pbAttendees;
    @BindView(R.id.btnCheckIn)
    Button btnCheckIn;
    public static final String TAG = "EventDetailActivity";
    long quantity = 0;
    int attendeeTrue = 0;
    int attendeeTotal = 0;
    static Attendee[] attendeeDetails;
    static String urlTickets;
    static String urlAttendees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        ButterKnife.bind(this);

        Intent i = getIntent();

        int position = i.getIntExtra("position",0);
        final long id = i.getLongExtra("id",0);
        final Gson gson = new Gson();
        urlTickets = Constants.EVENT_DETAILS + id + Constants.TICKETS;
        urlAttendees = Constants.EVENT_DETAILS + EventsActivity.events.get(position).getId() + Constants.ATTENDEES;
        ApiCall.callApi(this, urlTickets, new IVolleyCallBack() {
            @Override
            public void onSuccess(String result) {
                Event event = gson.fromJson(result , Event.class);
                List<Ticket> tickets = event.getTickets();

                String[] startDate = event.getStartTime().split("T");
                String[] endDate = event.getEndTime().split("T");

                if(tickets != null) {
                    for (Ticket thisTicket : tickets)
                        quantity += thisTicket.getQuantity();
                }

                tvEventTitle.setText(event.getName());
                tvStartDate.setText(startDate[0]);
                tvEndDate.setText(endDate[0]);
                tvTime.setText(endDate[1]);
                tvTicketTotal.setText(String.valueOf(quantity));
            }

            @Override
            public void onError(VolleyError error) {
                // No action to be taken
            }
        });
        if(Network.isNetworkConnected(this)) {
            ApiCall.callApi(this, urlAttendees, new IVolleyCallBack() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, "onSuccess: " + result);
                    attendeeTotal = 0;
                    attendeeTrue = 0;
                    attendeeDetails = gson.fromJson(result, Attendee[].class);
                    for (Attendee thisAttendee : attendeeDetails) {
                        if (thisAttendee.isCheckedIn()) {
                            attendeeTrue++;
                        }
                        attendeeTotal++;
                    }

                    tvAttendees.setText(String.valueOf(attendeeTrue) + "/" + String.valueOf(attendeeTotal));
                    tvTicketSold.setText(String.valueOf(attendeeTotal));

                    if(attendeeTotal != 0)
                        pbAttendees.setProgress( attendeeTrue / attendeeTotal);
                    if (quantity != 0) {
                        Log.d(TAG, "onSuccess: " + (int) ((attendeeTotal / quantity) * pbTickets.getMax()));
                        pbTickets.setProgress((int) ((attendeeTotal / quantity) * pbTickets.getMax()));
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    // No Action to be taken
                }
            });
        } else {
            Toast.makeText(this, Constants.NO_NETWORK, Toast.LENGTH_SHORT).show();
        }

        btnCheckIn.setOnClickListener(v -> {
            Intent i1 = new Intent(EventDetailsActivity.this , AttendeeListActivity.class);
            i1.putExtra("id" , id);
            startActivity(i1);
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }
}
