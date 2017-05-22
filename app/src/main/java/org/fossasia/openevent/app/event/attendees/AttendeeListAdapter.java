package org.fossasia.openevent.app.event.attendees;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.event.attendees.contract.IAttendeesPresenter;
import org.fossasia.openevent.app.utils.Constants;
import org.fossasia.openevent.app.utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AttendeeListAdapter extends RecyclerView.Adapter<AttendeeListAdapter.AttendeeListAdapterHolder> {
    private List<Attendee> attendeeList;
    private Context context;
    private IAttendeesPresenter attendeesPresenter;

    public AttendeeListAdapter(Context context, IAttendeesPresenter attendeesPresenter) {
        this.attendeeList = attendeesPresenter.getAttendees();
        this.context = context;
        this.attendeesPresenter = attendeesPresenter;
    }

    @Override
    public AttendeeListAdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendee_layout, parent, false);

        return new AttendeeListAdapterHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AttendeeListAdapterHolder holder, int position) {
        holder.bindAttendee(attendeeList.get(position));
    }

    void showToggleDialog(IAttendeesPresenter attendeesPresenter, Attendee attendee) {
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
    public int getItemCount() {
        return attendeeList.size();
    }

    class AttendeeListAdapterHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tvEmail)
        TextView tvEmail;
        @BindView(R.id.btnCheckedIn)
        AppCompatButton btnCheckedIn;

        private Attendee attendee;

        AttendeeListAdapterHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            btnCheckedIn.setOnClickListener(v -> showToggleDialog(attendeesPresenter, attendee));
        }

        void bindAttendee(Attendee attendee) {
            this.attendee = attendee;

            tvName.setText(Utils.formatOptionalString("%s %s", attendee.getFirstname(), attendee.getLastname()));
            tvEmail.setText(attendee.getEmail());

            if(attendee.isCheckedIn()) {
                btnCheckedIn.setText(R.string.checked_in);
                ViewCompat.setBackgroundTintList(btnCheckedIn, ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.holo_green_light)));
            } else {
                btnCheckedIn.setText(R.string.check_in);
                ViewCompat.setBackgroundTintList(btnCheckedIn, ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.holo_red_light)));
            }
        }


    }

}
