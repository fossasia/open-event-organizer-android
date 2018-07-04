package org.fossasia.openevent.app.core.orders.detail.viewholder;

import android.support.v7.widget.RecyclerView;

import org.fossasia.openevent.app.data.ticket.Ticket;
import org.fossasia.openevent.app.databinding.OrderTicketLayoutBinding;

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
