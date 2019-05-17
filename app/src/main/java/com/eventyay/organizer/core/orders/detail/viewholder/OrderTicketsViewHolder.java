package com.eventyay.organizer.core.orders.detail.viewholder;

import androidx.recyclerview.widget.RecyclerView;

import com.eventyay.organizer.data.ticket.Ticket;
import com.eventyay.organizer.databinding.OrderTicketLayoutBinding;

public class OrderTicketsViewHolder extends RecyclerView.ViewHolder {

    private final OrderTicketLayoutBinding binding;

    public OrderTicketsViewHolder(OrderTicketLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Ticket ticket) {
        binding.setTicket(ticket);
        binding.executePendingBindings();
    }
}
