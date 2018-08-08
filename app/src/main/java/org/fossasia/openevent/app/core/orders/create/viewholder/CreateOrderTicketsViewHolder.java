package org.fossasia.openevent.app.core.orders.create.viewholder;

import android.support.v7.widget.RecyclerView;

import org.fossasia.openevent.app.common.Pipe;
import org.fossasia.openevent.app.core.orders.create.CreateOrderViewModel;
import org.fossasia.openevent.app.data.ticket.Ticket;
import org.fossasia.openevent.app.databinding.OrderCreateTicketLayoutBinding;

public class CreateOrderTicketsViewHolder extends RecyclerView.ViewHolder {

    private final OrderCreateTicketLayoutBinding binding;
    private Ticket ticket;
    private Pipe<Ticket> clickAction;
    private CreateOrderViewModel createOrderViewModel;

    public CreateOrderTicketsViewHolder(OrderCreateTicketLayoutBinding binding, CreateOrderViewModel createOrderViewModel) {
        super(binding.getRoot());
        this.binding = binding;
        this.createOrderViewModel = createOrderViewModel;

        binding.getRoot().setOnClickListener(view -> {
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
