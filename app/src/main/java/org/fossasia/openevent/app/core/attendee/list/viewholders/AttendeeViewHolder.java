package org.fossasia.openevent.app.core.attendee.list.viewholders;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import org.fossasia.openevent.app.data.attendee.Attendee;
import org.fossasia.openevent.app.databinding.AttendeeLayoutBinding;

public class AttendeeViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {

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
