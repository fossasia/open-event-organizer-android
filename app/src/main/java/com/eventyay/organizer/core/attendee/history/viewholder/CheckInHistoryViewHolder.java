package com.eventyay.organizer.core.attendee.history.viewholder;

import android.support.v7.widget.RecyclerView;

import com.eventyay.organizer.data.attendee.CheckInDetail;
import com.eventyay.organizer.databinding.CheckInHistoryLayoutBinding;

public class CheckInHistoryViewHolder extends RecyclerView.ViewHolder {

    private final CheckInHistoryLayoutBinding binding;

    public CheckInHistoryViewHolder(CheckInHistoryLayoutBinding binding, int viewType) {
        super(binding.getRoot());
        this.binding = binding;
        binding.timeMarker.initLine(viewType);
    }

    public void bind(CheckInDetail checkInDetail) {
        binding.setCheckTime(checkInDetail);
        binding.executePendingBindings();
    }
}
