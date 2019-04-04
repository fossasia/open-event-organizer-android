package com.eventyay.organizer.core.settings.restriction;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.eventyay.organizer.common.Pipe;
import com.eventyay.organizer.data.ticket.Ticket;
import com.eventyay.organizer.databinding.TicketSettingLayoutBinding;

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
            ticket.isCheckinRestricted = !ticket.isCheckinRestricted;
            binding.ticketCheckbox.setChecked(ticket.isCheckinRestricted);
            updateTicketAction.push(ticket);
            binding.executePendingBindings();
        };
        itemView.setOnClickListener(listener);
        binding.ticketCheckbox.setOnClickListener(listener);
    }
}
