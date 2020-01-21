package com.eventyay.organizer.core.settings.autocheckin;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.eventyay.organizer.common.Pipe;
import com.eventyay.organizer.data.ticket.Ticket;
import com.eventyay.organizer.databinding.AutoCheckInLayoutBinding;

class AutoCheckInViewHolder extends RecyclerView.ViewHolder {
    private final AutoCheckInLayoutBinding binding;
    private Pipe<Ticket> updateTicketAction;

    AutoCheckInViewHolder(AutoCheckInLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void setUpdateTicketAction(Pipe<Ticket> updateTicketAction) {
        this.updateTicketAction = updateTicketAction;
    }

    public void bind(Ticket ticket) {
        binding.setTicket(ticket);
        View.OnClickListener listener = v -> {
            ticket.autoCheckinEnabled = !ticket.autoCheckinEnabled;
            binding.ticketCheckbox.setChecked(ticket.autoCheckinEnabled);
            updateTicketAction.push(ticket);
            binding.executePendingBindings();
        };
        itemView.setOnClickListener(listener);
        binding.ticketCheckbox.setOnClickListener(listener);
    }
}
