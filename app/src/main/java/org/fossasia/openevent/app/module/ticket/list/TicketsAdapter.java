package org.fossasia.openevent.app.module.ticket.list;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.app.viewholder.HeaderViewHolder;
import org.fossasia.openevent.app.common.data.models.Ticket;
import org.fossasia.openevent.app.databinding.HeaderLayoutBinding;
import org.fossasia.openevent.app.module.ticket.list.contract.ITicketsPresenter;
import org.fossasia.openevent.app.module.ticket.list.viewholder.TicketViewHolder;

import java.util.List;

public class TicketsAdapter extends RecyclerView.Adapter<TicketViewHolder> implements StickyRecyclerHeadersAdapter<HeaderViewHolder> {

    private final List<Ticket> tickets;
    private final ITicketsPresenter ticketsPresenter;

    public TicketsAdapter(ITicketsPresenter ticketsPresenter) {
        this.ticketsPresenter = ticketsPresenter;
        this.tickets = ticketsPresenter.getTickets();
    }

    @Override
    public TicketViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        return new TicketViewHolder(
            DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.ticket_layout, viewGroup, false),
            ticketsPresenter::deleteTicket
        );
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
