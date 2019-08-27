package com.eventyay.organizer.core.orders.detail.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import com.eventyay.organizer.data.attendee.Attendee;
import com.eventyay.organizer.databinding.OrderAttendeeLayoutBinding;

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
