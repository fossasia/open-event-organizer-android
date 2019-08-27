package com.eventyay.organizer.core.session.list.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import com.eventyay.organizer.common.Pipe;
import com.eventyay.organizer.core.session.list.SessionsPresenter;
import com.eventyay.organizer.data.session.Session;
import com.eventyay.organizer.databinding.SessionLayoutBinding;

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

        binding.getRoot()
                .setOnLongClickListener(
                        view -> {
                            if (longClickAction != null) {
                                longClickAction.push(session);
                            }
                            return true;
                        });
        binding.getRoot()
                .setOnClickListener(
                        view -> {
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
