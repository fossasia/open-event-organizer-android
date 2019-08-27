package com.eventyay.organizer.core.speaker.list.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import com.eventyay.organizer.common.Pipe;
import com.eventyay.organizer.data.speaker.Speaker;
import com.eventyay.organizer.databinding.SpeakerItemBinding;

public class SpeakerViewHolder extends RecyclerView.ViewHolder {
    private final SpeakerItemBinding binding;
    private Speaker speaker;
    private Pipe<Long> clickAction;

    public SpeakerViewHolder(SpeakerItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;

        binding.getRoot()
                .setOnClickListener(
                        view -> {
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
