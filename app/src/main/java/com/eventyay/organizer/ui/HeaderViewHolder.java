package com.eventyay.organizer.ui;

import androidx.recyclerview.widget.RecyclerView;

import com.eventyay.organizer.databinding.HeaderLayoutBinding;

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
