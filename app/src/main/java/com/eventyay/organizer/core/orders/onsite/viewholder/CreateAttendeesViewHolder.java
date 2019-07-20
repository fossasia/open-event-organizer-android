package com.eventyay.organizer.core.orders.onsite.viewholder;

import androidx.recyclerview.widget.RecyclerView;

import com.eventyay.organizer.common.Pipe;
import com.eventyay.organizer.core.orders.create.CreateOrderViewModel;
import com.eventyay.organizer.core.orders.onsite.CreateAttendeesViewModel;
import com.eventyay.organizer.data.attendee.Attendee;
import com.eventyay.organizer.data.ticket.Ticket;
import com.eventyay.organizer.databinding.FragmentCreateAttendeesBinding;
import com.eventyay.organizer.databinding.ItemCreateAttendeeBinding;
import com.eventyay.organizer.databinding.OrderCreateTicketLayoutBinding;

public class CreateAttendeesViewHolder extends RecyclerView.ViewHolder {

    private final ItemCreateAttendeeBinding binding;
    private Attendee attendee;
    private CreateAttendeesViewModel createAttendeesViewModel;

    public CreateAttendeesViewHolder(ItemCreateAttendeeBinding binding, CreateAttendeesViewModel createAttendeesViewModel) {
        super(binding.getRoot());
        this.binding = binding;
        this.createAttendeesViewModel = createAttendeesViewModel;
    }

    public void bind(Attendee attendee) {
        this.attendee = attendee;
        binding.setAttendee(attendee);
        binding.setCreateAttendeesViewModel(createAttendeesViewModel);
        createAttendeesViewModel.setAttendeeList(attendee);
        binding.executePendingBindings();
    }
}
