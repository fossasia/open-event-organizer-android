package com.eventyay.organizer.core.attendee.history;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.eventyay.organizer.R;
import com.eventyay.organizer.core.attendee.history.viewholder.CheckInHistoryViewHolder;
import com.eventyay.organizer.data.attendee.CheckInDetail;
import com.github.vipulasri.timelineview.TimelineView;

import java.util.List;

public class CheckInHistoryAdapter extends RecyclerView.Adapter<CheckInHistoryViewHolder> {

    private List<CheckInDetail> checkInHistory;

    @NonNull
    @Override
    public CheckInHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new CheckInHistoryViewHolder(
            DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                R.layout.check_in_history_layout, viewGroup, false), viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckInHistoryViewHolder checkInHistoryViewHolder, int position) {
        checkInHistoryViewHolder.bind(checkInHistory.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position, getItemCount());
    }

    @Override
    public int getItemCount() {
        if (checkInHistory == null) {
            return 0;
        }

        return checkInHistory.size();
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis") // Inevitable DU Anomaly
    protected void setCheckInHistory(final List<CheckInDetail> newCheckInHistory) {
        if (checkInHistory == null) {
            checkInHistory = newCheckInHistory;
            notifyItemRangeInserted(0, newCheckInHistory.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return checkInHistory.size();
                }

                @Override
                public int getNewListSize() {
                    return newCheckInHistory.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return checkInHistory.get(oldItemPosition).getId()
                    == newCheckInHistory.get(newItemPosition).getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return checkInHistory.get(oldItemPosition).equals(newCheckInHistory.get(newItemPosition));
                }
            });
            checkInHistory = newCheckInHistory;
            result.dispatchUpdatesTo(this);
        }
    }
}
