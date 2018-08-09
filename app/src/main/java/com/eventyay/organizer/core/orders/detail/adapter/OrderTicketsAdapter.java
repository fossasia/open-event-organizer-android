package com.eventyay.organizer.core.orders.detail.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.eventyay.organizer.R;
import com.eventyay.organizer.core.orders.detail.viewholder.OrderTicketsViewHolder;
import com.eventyay.organizer.data.ticket.Ticket;

import java.util.List;

public class OrderTicketsAdapter extends RecyclerView.Adapter<OrderTicketsViewHolder> {

    private List<Ticket> tickets;

    @NonNull
    @Override
    public OrderTicketsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        return new OrderTicketsViewHolder(DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
            R.layout.order_ticket_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderTicketsViewHolder orderTicketsViewHolder, int position) {
        orderTicketsViewHolder.bind(tickets.get(position));
    }

    @Override
    public int getItemCount() {
        return tickets == null ? 0 : tickets.size();
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis") // Inevitable DU Anomaly
    public void setTickets(final List<Ticket> newTickets) {
        if (tickets == null) {
            tickets = newTickets;
            notifyItemRangeInserted(0, newTickets.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return tickets.size();
                }

                @Override
                public int getNewListSize() {
                    return newTickets.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return tickets.get(oldItemPosition).getId() == newTickets.get(newItemPosition).getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return tickets.get(oldItemPosition).equals(newTickets.get(newItemPosition));
                }
            });
            tickets = newTickets;
            result.dispatchUpdatesTo(this);
        }
    }
}
