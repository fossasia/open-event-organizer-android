package org.fossasia.openevent.app.core.speaker.details;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.core.speaker.details.viewholder.SessionViewHolder;
import org.fossasia.openevent.app.data.session.Session;

import java.util.List;

public class SpeakersSessionsAdapter extends RecyclerView.Adapter<SessionViewHolder> {
    private List<Session> sessions;

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        return new SessionViewHolder(
            DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                R.layout.speaker_session_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder sessionViewHolder, int position) {
        sessionViewHolder.bind(sessions.get(position));
    }

    @Override
    public int getItemCount() {
        return sessions == null ? 0 : sessions.size();
    }

    protected void setSessions(final List<Session> newSessions) {
        if (sessions == null) {
            sessions = newSessions;
            notifyItemRangeInserted(0, newSessions.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return sessions.size();
                }

                @Override
                public int getNewListSize() {
                    return newSessions.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return sessions.get(oldItemPosition).getId()
                        .equals(newSessions.get(newItemPosition).getId());
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return sessions.get(oldItemPosition).equals(newSessions.get(newItemPosition));
                }
            });
            sessions = newSessions;
            result.dispatchUpdatesTo(this);
        }
    }
}
