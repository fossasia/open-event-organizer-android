package org.fossasia.openevent.app.core.track.list.viewholder;

import android.support.v7.widget.RecyclerView;

import org.fossasia.openevent.app.common.Pipe;
import org.fossasia.openevent.app.data.tracks.Track;
import org.fossasia.openevent.app.databinding.TrackItemBinding;

public class TracksViewHolder extends RecyclerView.ViewHolder {
    private final TrackItemBinding binding;
    private long trackId;
    private Pipe<Long> clickAction;
    private Pipe<Long> editAction;
    private Pipe<Long> deleteAction;

    public TracksViewHolder(TrackItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;

        binding.getRoot().setOnClickListener(view -> {
            if (clickAction != null)
                clickAction.push(trackId);
        });

        binding.actionChangeTrack.setOnClickListener(view -> {
            if (editAction != null) editAction.push(trackId);
        });

        binding.actionDeleteTrack.setOnClickListener(view -> {
            if (deleteAction != null) deleteAction.push(trackId);
        });
    }

    public void setClickAction(Pipe<Long> clickAction) {
        this.clickAction = clickAction;
    }

    public void setEditAction(Pipe<Long> editAction) {
        this.editAction = editAction;
    }

    public void setDeleteAction(Pipe<Long> deleteAction) {
        this.deleteAction = deleteAction;
    }

    public void bind(Track track) {
        trackId = track.getId();
        binding.setTrack(track);
        binding.executePendingBindings();
    }
}
