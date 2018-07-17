package org.fossasia.openevent.app.core.orders.create.viewholder;

import android.support.v7.widget.RecyclerView;

import org.fossasia.openevent.app.data.ticket.Ticket;
import org.fossasia.openevent.app.databinding.OrderCreateTicketLayoutBinding;

public class EventTicketsViewHolder extends RecyclerView.ViewHolder {

    private final OrderCreateTicketLayoutBinding binding;

    public EventTicketsViewHolder(OrderCreateTicketLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Ticket ticket) {
        binding.setTicket(ticket);
        binding.executePendingBindings();
    }
}
