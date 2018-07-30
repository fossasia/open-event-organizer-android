package com.eventyay.organizer.core.attendee.list.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.eventyay.organizer.data.attendee.Attendee;
import com.eventyay.organizer.databinding.AttendeeLayoutBinding;

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
