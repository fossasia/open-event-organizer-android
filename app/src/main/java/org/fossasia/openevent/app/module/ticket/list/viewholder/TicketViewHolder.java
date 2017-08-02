package org.fossasia.openevent.app.module.ticket.list.viewholder;

import android.support.v7.widget.RecyclerView;

import org.fossasia.openevent.app.common.contract.Pipe;
import org.fossasia.openevent.app.common.data.models.Ticket;
import org.fossasia.openevent.app.databinding.TicketLayoutBinding;

public class TicketViewHolder extends RecyclerView.ViewHolder {
    private final TicketLayoutBinding binding;
    private Ticket ticket;

    private Pipe<Ticket> deleteAction;
    private Pipe<Ticket> clickAction;

    public TicketViewHolder(TicketLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;

        binding.getRoot().setOnClickListener(view -> {
            if (clickAction != null) clickAction.push(ticket);
        });
        binding.deleteBtn.setOnClickListener(view -> {
            if (deleteAction != null) deleteAction.push(ticket);
        });
    }

    public void setDeleteAction(Pipe<Ticket> deleteAction) {
        this.deleteAction = deleteAction;
    }

    public void setClickAction(Pipe<Ticket> clickAction) {
        this.clickAction = clickAction;
    }

    public void bind(Ticket ticket) {
        this.ticket = ticket;
        binding.setTicket(ticket);
        binding.executePendingBindings();
    }

}
