package com.eventyay.organizer.core.orders.create.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.eventyay.organizer.R;
import com.eventyay.organizer.core.orders.create.CreateOrderViewModel;
import com.eventyay.organizer.core.orders.create.viewholder.CreateOrderTicketsViewHolder;
import com.eventyay.organizer.data.ticket.Ticket;
import java.util.List;

public class CreateOrderTicketsAdapter extends RecyclerView.Adapter<CreateOrderTicketsViewHolder> {

    private List<Ticket> tickets;
    private CreateOrderViewModel createOrderViewModel;

    public CreateOrderTicketsAdapter(CreateOrderViewModel createOrderViewModel) {
        this.createOrderViewModel = createOrderViewModel;
    }

    @NonNull
    @Override
    public CreateOrderTicketsViewHolder onCreateViewHolder(
            @NonNull ViewGroup viewGroup, int position) {
        CreateOrderTicketsViewHolder createOrderTicketsViewHolder =
                new CreateOrderTicketsViewHolder(
                        DataBindingUtil.inflate(
                                LayoutInflater.from(viewGroup.getContext()),
                                R.layout.order_create_ticket_layout,
                                viewGroup,
                                false),
                        createOrderViewModel);

        createOrderTicketsViewHolder.setClickAction(createOrderViewModel::ticketClick);

        return createOrderTicketsViewHolder;
    }

    @Override
    public void onBindViewHolder(
            @NonNull CreateOrderTicketsViewHolder createOrderTicketsViewHolder, int position) {
        createOrderTicketsViewHolder.bind(tickets.get(position));
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
            DiffUtil.DiffResult result =
                    DiffUtil.calculateDiff(
                            new DiffUtil.Callback() {
                                @Override
                                public int getOldListSize() {
                                    return tickets.size();
                                }

                                @Override
                                public int getNewListSize() {
                                    return newTickets.size();
                                }

                                @Override
                                public boolean areItemsTheSame(
                                        int oldItemPosition, int newItemPosition) {
                                    return tickets.get(oldItemPosition).getId()
                                            == newTickets.get(newItemPosition).getId();
                                }

                                @Override
                                public boolean areContentsTheSame(
                                        int oldItemPosition, int newItemPosition) {
                                    return tickets.get(oldItemPosition)
                                            .equals(newTickets.get(newItemPosition));
                                }
                            });
            tickets = newTickets;
            result.dispatchUpdatesTo(this);
        }
    }
}
