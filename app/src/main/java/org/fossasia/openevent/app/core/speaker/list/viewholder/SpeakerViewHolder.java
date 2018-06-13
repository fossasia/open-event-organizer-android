package org.fossasia.openevent.app.core.speaker.list.viewholder;

import android.support.v7.widget.RecyclerView;

import org.fossasia.openevent.app.common.Pipe;
import org.fossasia.openevent.app.data.speaker.Speaker;
import org.fossasia.openevent.app.databinding.SpeakerItemBinding;

public class SpeakerViewHolder extends RecyclerView.ViewHolder {
    private final SpeakerItemBinding binding;
    private Speaker speaker;
    private Pipe<Long> clickAction;

    public SpeakerViewHolder(SpeakerItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;

        binding.getRoot().setOnClickListener(view -> {
            if (clickAction != null) {
                clickAction.push(speaker.getId());
            }
        });
    }

    public void bind(Speaker speaker) {
        this.speaker = speaker;
        binding.setSpeaker(speaker);
        binding.executePendingBindings();
    }

    public void setClickAction(Pipe<Long> clickAction) {
        this.clickAction = clickAction;
    }
}
