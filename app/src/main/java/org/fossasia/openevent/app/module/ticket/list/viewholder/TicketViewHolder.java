package org.fossasia.openevent.app.module.ticket.list.viewholder;

import android.support.v7.widget.RecyclerView;

import org.fossasia.openevent.app.common.contract.Pipe;
import org.fossasia.openevent.app.common.data.models.Ticket;
import org.fossasia.openevent.app.databinding.TicketLayoutBinding;

public class TicketViewHolder extends RecyclerView.ViewHolder {
    private final TicketLayoutBinding binding;
    private Ticket ticket;

    public TicketViewHolder(TicketLayoutBinding binding, Pipe<Ticket> pipe) {
        super(binding.getRoot());
        this.binding = binding;

        binding.deleteBtn.setOnClickListener(view -> pipe.push(ticket));
    }

    public void bind(Ticket ticket) {
        this.ticket = ticket;
        binding.setTicket(ticket);
        binding.executePendingBindings();
    }

}
