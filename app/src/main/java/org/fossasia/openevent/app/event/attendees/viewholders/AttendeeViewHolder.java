package org.fossasia.openevent.app.event.attendees.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.databinding.AttendeeLayoutBinding;

public class AttendeeViewHolder extends RecyclerView.ViewHolder {

    private final AttendeeLayoutBinding binding;

    public AttendeeViewHolder(AttendeeLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bindAttendee(Attendee attendee) {
        binding.setAttendee(attendee);
        binding.executePendingBindings();
    }

    public void unbindAttendee() {
        binding.setAttendee(null);
        binding.executePendingBindings();
    }

    public View getRoot() {
        return binding.getRoot();
    }
}
