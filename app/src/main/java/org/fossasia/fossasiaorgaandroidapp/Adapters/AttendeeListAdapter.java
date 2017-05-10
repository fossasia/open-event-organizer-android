package org.fossasia.fossasiaorgaandroidapp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.internal.bind.ArrayTypeAdapter;

import org.fossasia.fossasiaorgaandroidapp.Api.ApiCall;
import org.fossasia.fossasiaorgaandroidapp.Api.LoginCall;
import org.fossasia.fossasiaorgaandroidapp.Interfaces.VolleyCallBack;
import org.fossasia.fossasiaorgaandroidapp.R;
import org.fossasia.fossasiaorgaandroidapp.Utils.Constants;
import org.fossasia.fossasiaorgaandroidapp.model.AttendeeDetails;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by rishabhkhanna on 27/04/17.
 */

public class AttendeeListAdapter extends RecyclerView.Adapter<AttendeeListAdapter.AttendeeListAdapterHolder> {
    ArrayList<AttendeeDetails> attendeeDetailses;
    Activity activity;
    long id;
    public static final String TAG = "AttendeeAdapter";

    public AttendeeListAdapter(ArrayList<AttendeeDetails> attendeeDetailses, Activity activity, long id) {
        this.attendeeDetailses = attendeeDetailses;
        this.activity = activity;
        this.id = id;
    }

    @Override
    public AttendeeListAdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = activity.getLayoutInflater();
        View itemView = li.inflate(R.layout.attendee_layout,null);

        AttendeeListAdapterHolder attendeeListAdapterHolder = new AttendeeListAdapterHolder(itemView);
        return attendeeListAdapterHolder;
    }

    @Override
    public void onBindViewHolder(final AttendeeListAdapterHolder holder, final int position) {
        final AttendeeDetails thisAttendee = attendeeDetailses.get(position);
        holder.tvLastName.setText(thisAttendee.getLastname());
        holder.tvFirstName.setText(thisAttendee.getFirstname());
        holder.tvEmail.setText(thisAttendee.getEmail());
        if(thisAttendee.getCheckedIn()) {
            holder.btnCheckedIn.setText("Checked In");
            holder.btnCheckedIn.setBackgroundColor(ContextCompat.getColor(activity,android.R.color.holo_green_dark));
        }else{
            holder.btnCheckedIn.setText("Check In");
            holder.btnCheckedIn.setBackgroundColor(ContextCompat.getColor(activity,android.R.color.holo_red_light));
        }
        holder.btnCheckedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: alert builder click");
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                String alertTitle  = "";
                if(thisAttendee.getCheckedIn()){
                    alertTitle = Constants.AttendeeCheckingOut;
                }else{
                    alertTitle = Constants.attendeeChechingIn;
                }
                builder.setTitle(alertTitle).setMessage(thisAttendee.getFirstname() + " "
                        + thisAttendee.getLastname() + "\n"
                        + "Ticket: " + thisAttendee.getTicket().getType() )
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(TAG, "onClick: inside ok");
                        changeCheckStatus(activity,thisAttendee,holder.btnCheckedIn,attendeeDetailses,position);
                        }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create();
                builder.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return attendeeDetailses.size();
    }

    public class AttendeeListAdapterHolder extends RecyclerView.ViewHolder{

        TextView tvLastName;
        TextView tvFirstName;
        TextView tvEmail;
        Button btnCheckedIn;

        public AttendeeListAdapterHolder(View itemView) {
            super(itemView);
            tvLastName = (TextView) itemView.findViewById(R.id.tvLastName);
            tvFirstName = (TextView) itemView.findViewById(R.id.tvFirstName);
            tvEmail = (TextView) itemView.findViewById(R.id.tvEmail);
            btnCheckedIn = (Button) itemView.findViewById(R.id.btnCheckedin);
        }
    }

    public void changeCheckStatus(Activity activity, AttendeeDetails thisAttendee, final Button btnCheckedIn, final ArrayList<AttendeeDetails> attendeeDetailses, final int position){
        ApiCall.PostApiCall(activity, Constants.eventDetails +id + Constants.attendeesToggle + thisAttendee.getId(), new VolleyCallBack() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "onSuccess: " + result);
                Gson gson = new Gson();
                AttendeeDetails newattendeeDetailses = gson.fromJson(result,AttendeeDetails.class);
                attendeeDetailses.set(position , newattendeeDetailses);
                notifyDataSetChanged();
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }

}
