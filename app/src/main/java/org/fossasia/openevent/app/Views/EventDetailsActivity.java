package org.fossasia.openevent.app.Views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.fossasia.openevent.app.Api.ApiCall;
import org.fossasia.openevent.app.Interfaces.VolleyCallBack;
import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.Utils.Constants;
import org.fossasia.openevent.app.Utils.Network;
import org.fossasia.openevent.app.model.AttendeeDetails;
import org.fossasia.openevent.app.model.EventDetails;
import org.fossasia.openevent.app.model.Ticket;

import java.util.ArrayList;
import java.util.List;

public class EventDetailsActivity extends AppCompatActivity {
    TextView tvEventTitle;
    TextView tvStartDate;
    TextView tvEndDate;
    TextView tvTime;
    TextView tvTicketSold;
    TextView tvAttendees;
    TextView tvTicketTotal;
    ProgressBar pbTickets;
    ProgressBar pbAttendees;
    Button btnCheckin;
    public static final String TAG = "EventDetailActivity";
    long quantity = 0;
    int attendeeTrue = 0;
    int attendeeTotal = 0;
    static AttendeeDetails[] attendeeDetailses;
    static String urlTickets;
    static String urlAttendees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        Intent i = getIntent();

        tvEventTitle = (TextView) findViewById(R.id.tvEventTitle);
        tvStartDate = (TextView) findViewById(R.id.tvStartDate);
        tvEndDate = (TextView) findViewById(R.id.tvEndDate);
        tvTime = (TextView) findViewById(R.id.tvHour);
        tvTicketSold = (TextView) findViewById(R.id.tvTicketSold);
        tvAttendees = (TextView) findViewById(R.id.tvAttendance);
        tvTicketTotal = (TextView) findViewById(R.id.tvTicketTotal);
        pbTickets = (ProgressBar) findViewById(R.id.progressTicketSold);
        pbAttendees = (ProgressBar) findViewById(R.id.progressAttendance);
        btnCheckin = (Button) findViewById(R.id.btnCheckin);

        int position = i.getIntExtra("position",0);
        final long id = i.getLongExtra("id",0);
        final Gson gson = new Gson();
        urlTickets = Constants.eventDetails + id + Constants.tickets;
         urlAttendees = Constants.eventDetails + EventsActivity.userEventsArrayList.get(position).getId() + Constants.attendees;
        ApiCall.callApi(this, urlTickets, new VolleyCallBack() {
            @Override
            public void onSuccess(String result) {
                EventDetails eventDetails = gson.fromJson(result , EventDetails.class);
                List<Ticket> tickets = (ArrayList<Ticket>) eventDetails.getTickets();

                String[] startDate = eventDetails.getStartTime().split("T");
                String[] endDate = eventDetails.getEndTime().split("T");

                for(Ticket thisTicket : tickets){
                    quantity += thisTicket.getQuantity();
                }
                tvEventTitle.setText(eventDetails.getName());
                tvStartDate.setText(startDate[0]);
                tvEndDate.setText(endDate[0]);
                tvTime.setText(endDate[1]);
                tvTicketTotal.setText(String.valueOf(quantity));



            }
            @Override
            public void onError(VolleyError error) {

            }
        });
        if(Network.isNetworkConnected(this)) {
            ApiCall.callApi(this, urlAttendees, new VolleyCallBack() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, "onSuccess: " + result);
                    attendeeTotal = 0;
                    attendeeTrue = 0;
                    attendeeDetailses = gson.fromJson(result, AttendeeDetails[].class);
                    for (AttendeeDetails thisAttendee : attendeeDetailses) {
                        if (thisAttendee.getCheckedIn()) {
                            attendeeTrue++;
                        }
                        attendeeTotal++;

                    }


                    tvAttendees.setText(String.valueOf(attendeeTrue) + "/" + String.valueOf(attendeeTotal));
                    tvTicketSold.setText(String.valueOf(attendeeTotal));

                    pbAttendees.setProgress((int) attendeeTrue / attendeeTotal);
                    Log.d(TAG, "onSuccess: " + (int) ((attendeeTotal / quantity) * pbTickets.getMax()));
                    if (quantity != 0)
                        pbTickets.setProgress((int) ((attendeeTotal / quantity) * pbTickets.getMax()));

                }

                @Override
                public void onError(VolleyError error) {

                }
            });
        }else{
            Toast.makeText(this, Constants.noNetwork, Toast.LENGTH_SHORT).show();
        }

        btnCheckin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EventDetailsActivity.this , AttendeeListActivity.class);
                i.putExtra("id" , id);
                startActivity(i);
            }
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
