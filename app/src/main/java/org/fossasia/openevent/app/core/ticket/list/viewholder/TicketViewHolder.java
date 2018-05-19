package org.fossasia.openevent.app.core.ticket.list.viewholder;

import androidx.recyclerview.widget.RecyclerView;

import org.fossasia.openevent.app.common.Pipe;
import org.fossasia.openevent.app.data.ticket.Ticket;
import org.fossasia.openevent.app.databinding.TicketLayoutBinding;

public class TicketViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
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
