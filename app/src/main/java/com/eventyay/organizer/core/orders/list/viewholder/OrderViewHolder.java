package com.eventyay.organizer.core.orders.list.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import com.eventyay.organizer.common.Pipe;
import com.eventyay.organizer.data.order.Order;
import com.eventyay.organizer.databinding.OrderLayoutBinding;

public class OrderViewHolder extends RecyclerView.ViewHolder {

    private final OrderLayoutBinding binding;
    private Pipe<Order> clickAction;
    private Order order;

    public OrderViewHolder(OrderLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;

        binding.getRoot()
                .setOnClickListener(
                        view -> {
                            if (clickAction != null) {
                                clickAction.push(order);
                            }
                        });
    }

    public void setClickAction(Pipe<Order> clickAction) {
        this.clickAction = clickAction;
    }

    public void bind(Order order) {
        this.order = order;
        binding.setOrder(order);
        binding.executePendingBindings();
    }
}
