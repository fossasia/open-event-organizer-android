package org.fossasia.openevent.app.core.track.list.viewholder;

import android.support.v7.widget.RecyclerView;

import org.fossasia.openevent.app.common.Pipe;
import org.fossasia.openevent.app.data.tracks.Track;
import org.fossasia.openevent.app.databinding.TrackItemBinding;

public class TracksViewHolder extends RecyclerView.ViewHolder {
    private final TrackItemBinding binding;
    private long trackId;
    private Pipe<Long> clickAction;
    private Pipe<Track> editAction;

    public TracksViewHolder(TrackItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;

        binding.getRoot().setOnClickListener(view -> {
            if (clickAction != null)
                clickAction.push(trackId);
        });
    }

    public void setClickAction(Pipe<Long> clickAction) {
        this.clickAction = clickAction;
    }

    public void setEditAction(Pipe<Track> editAction) {
        this.editAction = editAction;
    }

    public void bind(Track track) {
        trackId = track.getId();
        binding.setTrack(track);
        binding.actionChangeTrack.setOnClickListener(view -> {
            if (editAction != null) editAction.push(track);
        });
        binding.executePendingBindings();
    }
}
