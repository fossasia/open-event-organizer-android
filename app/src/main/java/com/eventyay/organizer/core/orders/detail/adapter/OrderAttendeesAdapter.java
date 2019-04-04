package com.eventyay.organizer.core.orders.detail.adapter;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.eventyay.organizer.R;
import com.eventyay.organizer.core.orders.detail.viewholder.OrderAttendeesViewHolder;
import com.eventyay.organizer.data.attendee.Attendee;

import java.util.List;

public class OrderAttendeesAdapter extends RecyclerView.Adapter<OrderAttendeesViewHolder> {

    private List<Attendee> attendees;

    @NonNull
    @Override
    public OrderAttendeesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        return new OrderAttendeesViewHolder(DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                R.layout.order_attendee_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAttendeesViewHolder orderAttendeesViewHolder, int position) {
        orderAttendeesViewHolder.bind(attendees.get(position));
    }

    @Override
    public int getItemCount() {
        return attendees == null ? 0 : attendees.size();
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis") // Inevitable DU Anomaly
    public void setAttendees(final List<Attendee> newAttendees) {
        if (attendees == null) {
            attendees = newAttendees;
            notifyItemRangeInserted(0, newAttendees.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return attendees.size();
                }

                @Override
                public int getNewListSize() {
                    return newAttendees.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return attendees.get(oldItemPosition).getId() == newAttendees.get(newItemPosition).getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return attendees.get(oldItemPosition).equals(newAttendees.get(newItemPosition));
                }
            });
            attendees = newAttendees;
            result.dispatchUpdatesTo(this);
        }
    }
}
