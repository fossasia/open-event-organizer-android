package org.fossasia.openevent.app.ui;

import androidx.recyclerview.widget.RecyclerView;

import org.fossasia.openevent.app.databinding.HeaderLayoutBinding;

public class HeaderViewHolder extends RecyclerView.ViewHolder {
    private final HeaderLayoutBinding binding;

    public HeaderViewHolder(HeaderLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bindHeader(String header) {
        binding.setSubheading(header);
        binding.executePendingBindings();
    }
}
