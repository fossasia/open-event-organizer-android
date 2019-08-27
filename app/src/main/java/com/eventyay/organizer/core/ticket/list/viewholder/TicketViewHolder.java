package com.eventyay.organizer.core.ticket.list.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import com.eventyay.organizer.common.Pipe;
import com.eventyay.organizer.data.ticket.Ticket;
import com.eventyay.organizer.databinding.TicketLayoutBinding;

public class TicketViewHolder extends RecyclerView.ViewHolder {
    private final TicketLayoutBinding binding;
    private Ticket ticket;

    private Pipe<Ticket> deleteAction;
    private Pipe<Ticket> clickAction;

    public TicketViewHolder(TicketLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;

        binding.getRoot()
                .setOnClickListener(
                        view -> {
                            if (clickAction != null) clickAction.push(ticket);
                        });
        binding.deleteBtn.setOnClickListener(
                view -> {
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
