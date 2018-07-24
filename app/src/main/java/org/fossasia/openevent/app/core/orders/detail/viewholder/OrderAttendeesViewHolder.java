package org.fossasia.openevent.app.core.orders.detail.viewholder;

import android.support.v7.widget.RecyclerView;

import org.fossasia.openevent.app.data.attendee.Attendee;
import org.fossasia.openevent.app.databinding.OrderAttendeeLayoutBinding;

public class OrderAttendeesViewHolder extends RecyclerView.ViewHolder {

    private final OrderAttendeeLayoutBinding binding;

    public OrderAttendeesViewHolder(OrderAttendeeLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Attendee attendee) {
        binding.setAttendee(attendee);
        binding.executePendingBindings();
    }
}
