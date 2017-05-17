package org.fossasia.openevent.app.ui.adapters;

import android.content.Context;
import android.content.DialogInterface;
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

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.data.network.api.ApiCall;
import org.fossasia.openevent.app.data.network.interfaces.IVolleyCallBack;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.utils.Constants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AttendeeListAdapter extends RecyclerView.Adapter<AttendeeListAdapter.AttendeeListAdapterHolder> {
    private List<Attendee> attendeeList;
    private Context context;
    private long id;

    private static final String TAG = "AttendeeAdapter";

    public AttendeeListAdapter(List<Attendee> attendeeList, Context context, long id) {
        this.attendeeList = attendeeList;
        this.context = context;
        this.id = id;
    }

    @Override
    public AttendeeListAdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendee_layout, parent, false);

        return new AttendeeListAdapterHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AttendeeListAdapterHolder holder, int position) {
        final Attendee thisAttendee = attendeeList.get(position);
        holder.tvLastName.setText(thisAttendee.getLastname());
        holder.tvFirstName.setText(thisAttendee.getFirstname());
        holder.tvEmail.setText(thisAttendee.getEmail());

        if(thisAttendee.isCheckedIn()) {
            holder.btnCheckedIn.setText(R.string.checked_in);
            holder.btnCheckedIn.setBackgroundColor(ContextCompat.getColor(context,android.R.color.holo_green_dark));
        } else {
            holder.btnCheckedIn.setText(R.string.check_in);
            holder.btnCheckedIn.setBackgroundColor(ContextCompat.getColor(context,android.R.color.holo_red_light));
        }
        holder.btnCheckedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: alert builder click");
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                String alertTitle;
                if(thisAttendee.isCheckedIn()){
                    alertTitle = Constants.ATTENDEE_CHECKING_OUT;
                }else{
                    alertTitle = Constants.ATTENDEE_CHECKING_IN;
                }
                builder.setTitle(alertTitle).setMessage(thisAttendee.getFirstname() + " "
                        + thisAttendee.getLastname() + "\n"
                        + "Ticket: " + thisAttendee.getTicket().getType() )
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(TAG, "onClick: inside ok");
                        changeCheckStatus(context, thisAttendee, holder.getAdapterPosition());
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
        return attendeeList.size();
    }

    class AttendeeListAdapterHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tvLastName)
        TextView tvLastName;
        @BindView(R.id.tvFirstName)
        TextView tvFirstName;
        @BindView(R.id.tvEmail)
        TextView tvEmail;
        @BindView(R.id.btnCheckedIn)
        Button btnCheckedIn;

        AttendeeListAdapterHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    private void changeCheckStatus(Context context, Attendee thisAttendee, final int position){
        ApiCall.PostApiCall(context, Constants.EVENT_DETAILS + id + Constants.ATTENDEES_TOGGLE + thisAttendee.getId(), new IVolleyCallBack() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "onSuccess: " + result);
                Gson gson = new Gson();
                Attendee newattendeeDetailses = gson.fromJson(result,Attendee.class);
                attendeeList.set(position , newattendeeDetailses);
                notifyDataSetChanged();
            }

            @Override
            public void onError(VolleyError error) {
                // No Action Required
            }
        });
    }

}
