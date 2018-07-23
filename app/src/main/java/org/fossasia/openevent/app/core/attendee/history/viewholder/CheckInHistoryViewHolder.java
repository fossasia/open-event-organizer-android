package org.fossasia.openevent.app.core.attendee.history.viewholder;

import android.support.v7.widget.RecyclerView;

import org.fossasia.openevent.app.data.attendee.CheckInDetail;
import org.fossasia.openevent.app.databinding.CheckInHistoryLayoutBinding;

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
