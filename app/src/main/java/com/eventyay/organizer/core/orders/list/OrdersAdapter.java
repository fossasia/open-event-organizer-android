package com.eventyay.organizer.core.orders.list;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.eventyay.organizer.R;
import com.eventyay.organizer.core.orders.list.viewholder.OrderViewHolder;
import com.eventyay.organizer.data.order.Order;
import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrderViewHolder> {

    private List<Order> orders;
    private final OrdersViewModel ordersViewModel;

    public OrdersAdapter(OrdersViewModel ordersViewModel) {
        this.ordersViewModel = ordersViewModel;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        OrderViewHolder orderViewHolder =
                new OrderViewHolder(
                        DataBindingUtil.inflate(
                                LayoutInflater.from(viewGroup.getContext()),
                                R.layout.order_layout,
                                viewGroup,
                                false));

        orderViewHolder.setClickAction(ordersViewModel::click);

        return orderViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder orderViewHolder, int position) {
        orderViewHolder.bind(orders.get(position));
    }

    @Override
    public int getItemCount() {
        if (orders == null) {
            return 0;
        }

        return orders.size();
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis") // Inevitable DU Anomaly
    protected void setOrders(final List<Order> newOrders) {
        if (orders == null) {
            orders = newOrders;
            notifyItemRangeInserted(0, newOrders.size());
        } else {
            DiffUtil.DiffResult result =
                    DiffUtil.calculateDiff(
                            new DiffUtil.Callback() {
                                @Override
                                public int getOldListSize() {
                                    return orders.size();
                                }

                                @Override
                                public int getNewListSize() {
                                    return newOrders.size();
                                }

                                @Override
                                public boolean areItemsTheSame(
                                        int oldItemPosition, int newItemPosition) {
                                    return orders.get(oldItemPosition)
                                            .getId()
                                            .equals(newOrders.get(newItemPosition).getId());
                                }

                                @Override
                                public boolean areContentsTheSame(
                                        int oldItemPosition, int newItemPosition) {
                                    return orders.get(oldItemPosition)
                                            .equals(newOrders.get(newItemPosition));
                                }
                            });
            orders = newOrders;
            result.dispatchUpdatesTo(this);
        }
    }
}
