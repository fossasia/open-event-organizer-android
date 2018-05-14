package org.fossasia.openevent.app.core.session.list.viewholder;

import android.support.v7.widget.RecyclerView;

import org.fossasia.openevent.app.common.Pipe;
import org.fossasia.openevent.app.data.session.Session;
import org.fossasia.openevent.app.databinding.SessionLayoutBinding;

public class SessionViewHolder extends RecyclerView.ViewHolder {

    private final SessionLayoutBinding binding;
    private Session session;
    private Pipe<Session> longClickAction;
    private Runnable clickAction;

    public SessionViewHolder(SessionLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;

        binding.getRoot().setOnLongClickListener(view -> {
            if (longClickAction != null) {
                session.getSelected().set(true);
                longClickAction.push(session);
            }
            return true;
        });
        binding.getRoot().setOnClickListener(view -> {
            if (clickAction != null)
                clickAction.run();
        });
    }

    public void setLongClickAction(Pipe<Session> longClickAction) {
        this.longClickAction = longClickAction;
    }

    public void setClickAction(Runnable clickAction) {
        this.clickAction = clickAction;
    }

    public void bind(Session session) {
        this.session = session;
        binding.setSession(session);
        binding.executePendingBindings();
    }
}
