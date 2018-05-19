package org.fossasia.openevent.app.core.session.list.viewholder;

import androidx.recyclerview.widget.RecyclerView;

import org.fossasia.openevent.app.data.session.Session;
import org.fossasia.openevent.app.databinding.SessionLayoutBinding;

public class SessionViewHolder extends RecyclerView.ViewHolder {

    private final SessionLayoutBinding binding;

    public SessionViewHolder(SessionLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Session session) {
        binding.setSession(session);
        binding.executePendingBindings();
    }
}
