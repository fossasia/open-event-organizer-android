package org.fossasia.openevent.app.event.tickets.viewholder;

import android.support.v7.widget.RecyclerView;

import org.fossasia.openevent.app.data.models.Ticket;
import org.fossasia.openevent.app.databinding.TicketLayoutBinding;

public class TicketViewHolder extends RecyclerView.ViewHolder {
    private final TicketLayoutBinding binding;

    public TicketViewHolder(TicketLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Ticket ticket) {
        binding.setTicket(ticket);
        binding.executePendingBindings();
    }

}
