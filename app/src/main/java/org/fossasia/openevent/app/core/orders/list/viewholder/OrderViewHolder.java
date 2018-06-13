package org.fossasia.openevent.app.core.orders.list.viewholder;

import android.support.v7.widget.RecyclerView;

import org.fossasia.openevent.app.data.order.Order;
import org.fossasia.openevent.app.databinding.OrderLayoutBinding;

public class OrderViewHolder extends RecyclerView.ViewHolder {

    private final OrderLayoutBinding binding;

    public OrderViewHolder(OrderLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Order order) {
        binding.setOrder(order);
        binding.executePendingBindings();
    }
}
