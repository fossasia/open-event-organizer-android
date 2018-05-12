package org.fossasia.openevent.app.core.speaker.list.viewholder;

import android.support.v7.widget.RecyclerView;

import org.fossasia.openevent.app.data.speaker.Speaker;
import org.fossasia.openevent.app.databinding.SpeakerItemBinding;

public class SpeakerViewHolder extends RecyclerView.ViewHolder {
    private final SpeakerItemBinding binding;

    public SpeakerViewHolder(SpeakerItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Speaker speaker) {
        binding.setSpeaker(speaker);
        binding.executePendingBindings();
    }
}
