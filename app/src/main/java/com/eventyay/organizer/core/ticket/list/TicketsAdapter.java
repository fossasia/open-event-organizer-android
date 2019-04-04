package com.eventyay.organizer.core.ticket.list;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import com.eventyay.organizer.R;
import com.eventyay.organizer.core.ticket.list.viewholder.TicketViewHolder;
import com.eventyay.organizer.data.ticket.Ticket;
import com.eventyay.organizer.databinding.HeaderLayoutBinding;
import com.eventyay.organizer.ui.HeaderViewHolder;

import java.util.List;

public class TicketsAdapter extends RecyclerView.Adapter<TicketViewHolder> implements StickyRecyclerHeadersAdapter<HeaderViewHolder> {

    private final List<Ticket> tickets;
    private final TicketsPresenter ticketsPresenter;

    public TicketsAdapter(TicketsPresenter ticketsPresenter) {
        this.ticketsPresenter = ticketsPresenter;
        this.tickets = ticketsPresenter.getTickets();
    }

    @Override
    public TicketViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        TicketViewHolder ticketViewHolder = new TicketViewHolder(
            DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
            R.layout.ticket_layout, viewGroup, false));
        ticketViewHolder.setDeleteAction(ticketsPresenter::deleteTicket);
        ticketViewHolder.setClickAction(ticketsPresenter::showDetails);

        return ticketViewHolder;
    }

    @Override
    public void onBindViewHolder(TicketViewHolder ticketViewHolder, int position) {
        ticketViewHolder.bind(tickets.get(position));
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup viewGroup) {
        return new HeaderViewHolder(HeaderLayoutBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
    }

    @Override
    public void onBindHeaderViewHolder(HeaderViewHolder headerViewHolder, int position) {
        headerViewHolder.bindHeader(tickets.get(position).getType());
    }

    @Override
    public long getHeaderId(int position) {
        return tickets.get(position).getType().hashCode();
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }
}
