package org.fossasia.openevent.app.event.attendees;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.databinding.AttendeeLayoutBinding;
import org.fossasia.openevent.app.event.attendees.contract.IAttendeesPresenter;
import org.fossasia.openevent.app.utils.Constants;

import java.util.List;

class AttendeeListAdapter extends RecyclerView.Adapter<AttendeeListAdapter.AttendeeListAdapterHolder> {
    private List<Attendee> attendeeList;
    private Context context;
    private IAttendeesPresenter attendeesPresenter;

    AttendeeListAdapter(Context context, IAttendeesPresenter attendeesPresenter) {
        this.attendeeList = attendeesPresenter.getAttendees();
        this.context = context;
        this.attendeesPresenter = attendeesPresenter;
    }

    @Override
    public AttendeeListAdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        AttendeeLayoutBinding binding = AttendeeLayoutBinding.inflate(layoutInflater, parent, false);

        return new AttendeeListAdapterHolder(binding);
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
        private final AttendeeLayoutBinding binding;

        private Attendee attendee;

        AttendeeListAdapterHolder(AttendeeLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.btnCheckedIn.setOnClickListener(v -> showToggleDialog(attendeesPresenter, attendee));
        }

        void bindAttendee(Attendee attendee) {
            this.attendee = attendee;
            binding.setAttendee(attendee);
            binding.executePendingBindings();
        }
    }

}
