package org.fossasia.openevent.app.core.speaker.details.viewholder;

import android.support.v7.widget.RecyclerView;

import org.fossasia.openevent.app.data.session.Session;
import org.fossasia.openevent.app.databinding.SpeakerSessionLayoutBinding;

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
