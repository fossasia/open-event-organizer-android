package org.fossasia.openevent.app.core.orders.list.viewholder;

import android.support.v7.widget.RecyclerView;

import org.fossasia.openevent.app.common.Pipe;
import org.fossasia.openevent.app.data.order.Order;
import org.fossasia.openevent.app.databinding.OrderLayoutBinding;

public class OrderViewHolder extends RecyclerView.ViewHolder {

    private final OrderLayoutBinding binding;
    private Pipe<Order> clickAction;
    private Order order;

    public OrderViewHolder(OrderLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;

        binding.getRoot().setOnClickListener(view -> {
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
