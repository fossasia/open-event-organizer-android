package com.eventyay.organizer.core.orders.create.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import com.eventyay.organizer.common.Pipe;
import com.eventyay.organizer.core.orders.create.CreateOrderViewModel;
import com.eventyay.organizer.data.ticket.Ticket;
import com.eventyay.organizer.databinding.OrderCreateTicketLayoutBinding;

public class CreateOrderTicketsViewHolder extends RecyclerView.ViewHolder {

    private final OrderCreateTicketLayoutBinding binding;
    private Ticket ticket;
    private Pipe<Ticket> clickAction;
    private CreateOrderViewModel createOrderViewModel;

    public CreateOrderTicketsViewHolder(
            OrderCreateTicketLayoutBinding binding, CreateOrderViewModel createOrderViewModel) {
        super(binding.getRoot());
        this.binding = binding;
        this.createOrderViewModel = createOrderViewModel;

        binding.getRoot()
                .setOnClickListener(
                        view -> {
                            if (clickAction != null) {
                                clickAction.push(ticket);
                            }
                        });
    }

    public void setClickAction(Pipe<Ticket> clickAction) {
        this.clickAction = clickAction;
    }

    public void bind(Ticket ticket) {
        this.ticket = ticket;
        binding.setTicket(ticket);
        binding.setCreateOrderViewModel(createOrderViewModel);
        binding.executePendingBindings();
    }
}
