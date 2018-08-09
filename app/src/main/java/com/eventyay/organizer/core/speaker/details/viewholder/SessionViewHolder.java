package com.eventyay.organizer.core.speaker.details.viewholder;

import android.support.v7.widget.RecyclerView;

import com.eventyay.organizer.data.session.Session;
import com.eventyay.organizer.databinding.SpeakerSessionLayoutBinding;

public class SessionViewHolder extends RecyclerView.ViewHolder {
    private final SpeakerSessionLayoutBinding binding;

    public SessionViewHolder(SpeakerSessionLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Session session) {
        binding.setSession(session);
        binding.executePendingBindings();
    }
}
