package com.eventyay.organizer.core.settings.autocheckin;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.Pipe;
import com.eventyay.organizer.data.ticket.Ticket;

import java.util.List;

public class AutoCheckInAdapter extends RecyclerView.Adapter<AutoCheckInViewHolder> {

    private List<Ticket> tickets;
    private final Pipe<Ticket> updateTicketAction;

    public AutoCheckInAdapter(List<Ticket> tickets, Pipe<Ticket> updateTicketAction) {
        this.tickets = tickets;
        this.updateTicketAction = updateTicketAction;
    }

    @Override
    public AutoCheckInViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        AutoCheckInViewHolder viewHolder = new AutoCheckInViewHolder(
            DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                R.layout.auto_check_in_layout, viewGroup, false));
        viewHolder.setUpdateTicketAction(updateTicketAction);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AutoCheckInViewHolder autoCheckInViewHolder, int position) {
        autoCheckInViewHolder.bind(tickets.get(position));
    }

    @Override
    public int getItemCount() {
        return tickets == null ? 0 : tickets.size();
    }

    public void setTickets(List<Ticket> newTickets) {
        tickets = newTickets;
        notifyDataSetChanged();
    }

}
