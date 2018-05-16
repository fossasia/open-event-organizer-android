package org.fossasia.openevent.app.core.session.list;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.core.session.list.viewholder.SessionViewHolder;
import org.fossasia.openevent.app.data.session.Session;

import java.util.List;

public class SessionsAdapter extends RecyclerView.Adapter<SessionViewHolder> {

    private final SessionsPresenter sessionsPresenter;
    private final List<Session> sessions;

    public SessionsAdapter(SessionsPresenter sessionsPresenter) {
        this.sessionsPresenter = sessionsPresenter;
        this.sessions = sessionsPresenter.getSessions();
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        SessionViewHolder sessionViewHolder = new SessionViewHolder(
            DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                R.layout.session_layout, viewGroup, false), sessionsPresenter);

        sessionViewHolder.setLongClickAction(sessionsPresenter::toolbarDeleteMode);
        sessionViewHolder.setClickAction(sessionsPresenter::resetToDefaultState);

        return sessionViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder sessionViewHolder, int position) {
        sessionViewHolder.bind(sessions.get(position));
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }
}
