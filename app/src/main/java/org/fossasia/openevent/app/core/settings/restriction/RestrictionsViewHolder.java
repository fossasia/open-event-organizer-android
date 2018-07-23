package org.fossasia.openevent.app.core.settings.restriction;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.fossasia.openevent.app.common.Pipe;
import org.fossasia.openevent.app.data.ticket.Ticket;
import org.fossasia.openevent.app.databinding.TicketSettingLayoutBinding;

class RestrictionsViewHolder extends RecyclerView.ViewHolder {
    private final TicketSettingLayoutBinding binding;
    private Pipe<Ticket> updateTicketAction;

    RestrictionsViewHolder(TicketSettingLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void setUpdateTicketAction(Pipe<Ticket> updateTicketAction) {
        this.updateTicketAction = updateTicketAction;
    }

    public void bind(Ticket ticket) {
        binding.setTicket(ticket);
        View.OnClickListener listener = v -> {
            ticket.isCheckinRestricted = ticket.isCheckinRestricted == null || !ticket.isCheckinRestricted;
            binding.ticketCheckbox.setChecked(ticket.isCheckinRestricted);
            updateTicketAction.push(ticket);
            binding.executePendingBindings();
        };
        itemView.setOnClickListener(listener);
        binding.ticketCheckbox.setOnClickListener(listener);
    }
}
