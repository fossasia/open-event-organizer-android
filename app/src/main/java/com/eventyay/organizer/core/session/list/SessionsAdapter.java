package com.eventyay.organizer.core.session.list;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.eventyay.organizer.R;
import com.eventyay.organizer.core.session.list.viewholder.SessionViewHolder;
import com.eventyay.organizer.data.session.Session;

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

        sessionViewHolder.setLongClickAction(sessionsPresenter::longClick);
        sessionViewHolder.setClickAction(sessionsPresenter::click);

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
