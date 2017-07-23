package org.fossasia.openevent.app.common;

import android.support.v7.widget.RecyclerView;

import org.fossasia.openevent.app.databinding.HeaderLayoutBinding;

public class HeaderViewHolder extends RecyclerView.ViewHolder {
    private HeaderLayoutBinding binding;

    public HeaderViewHolder(HeaderLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bindHeader(String header) {
        binding.setSubheading(header);
        binding.executePendingBindings();
    }
}
