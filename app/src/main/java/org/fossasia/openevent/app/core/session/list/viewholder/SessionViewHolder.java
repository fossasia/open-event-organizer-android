package org.fossasia.openevent.app.core.session.list.viewholder;

import android.support.v7.widget.RecyclerView;

import org.fossasia.openevent.app.common.Pipe;
import org.fossasia.openevent.app.core.session.list.SessionsPresenter;
import org.fossasia.openevent.app.data.session.Session;
import org.fossasia.openevent.app.databinding.SessionLayoutBinding;

public class SessionViewHolder extends RecyclerView.ViewHolder {

    private final SessionLayoutBinding binding;
    private final SessionsPresenter sessionsPresenter;
    private Session session;
    private Pipe<Session> longClickAction;
    private Pipe<Long> clickAction;

    public SessionViewHolder(SessionLayoutBinding binding, SessionsPresenter sessionsPresenter) {
        super(binding.getRoot());
        this.binding = binding;
        this.sessionsPresenter = sessionsPresenter;

        binding.getRoot().setOnLongClickListener(view -> {
            if (longClickAction != null) {
                longClickAction.push(session);
            }
            return true;
        });
        binding.getRoot().setOnClickListener(view -> {
            if (clickAction != null) {
                clickAction.push(session.getId());
           }
        });
    }

    public void setLongClickAction(Pipe<Session> longClickAction) {
        this.longClickAction = longClickAction;
    }

    public void setClickAction(Pipe<Long> clickAction) {
        this.clickAction = clickAction;
    }

    public void bind(Session session) {
        this.session = session;
        binding.setSession(session);
        binding.setSessionsPresenter(sessionsPresenter);
        binding.executePendingBindings();
    }
}
