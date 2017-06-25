package org.fossasia.openevent.app.events.viewholders;

import android.support.v7.widget.RecyclerView;

import org.fossasia.openevent.app.databinding.EventSubheaderLayoutBinding;

public class EventsHeaderViewHolder extends RecyclerView.ViewHolder {

    private EventSubheaderLayoutBinding binding;

    public EventsHeaderViewHolder(EventSubheaderLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bindHeader(String header) {
        binding.setSubheading(header);
        binding.executePendingBindings();
    }
}
