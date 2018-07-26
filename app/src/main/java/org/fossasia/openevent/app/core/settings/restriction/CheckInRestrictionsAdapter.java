package org.fossasia.openevent.app.core.settings.restriction;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.Pipe;
import org.fossasia.openevent.app.data.ticket.Ticket;

import java.util.List;

public class CheckInRestrictionsAdapter extends RecyclerView.Adapter<RestrictionsViewHolder> {

    private List<Ticket> tickets;
    private final Pipe<Ticket> updateTicketAction;

    public CheckInRestrictionsAdapter(List<Ticket> tickets, Pipe<Ticket> updateTicketAction) {
        this.tickets = tickets;
        this.updateTicketAction = updateTicketAction;
    }

    @Override
    public RestrictionsViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        RestrictionsViewHolder viewHolder = new RestrictionsViewHolder(
            DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                R.layout.ticket_setting_layout, viewGroup, false));
        viewHolder.setUpdateTicketAction(updateTicketAction);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RestrictionsViewHolder restrictionsViewHolder, int position) {
        restrictionsViewHolder.bind(tickets.get(position));
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
