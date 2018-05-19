package org.fossasia.openevent.app.core.track.list.viewholder;

import androidx.recyclerview.widget.RecyclerView;

import org.fossasia.openevent.app.common.Pipe;
import org.fossasia.openevent.app.data.tracks.Track;
import org.fossasia.openevent.app.databinding.TrackItemBinding;

public class TracksViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
    private final TrackItemBinding binding;
    private Track track;
    private Pipe<Track> clickAction;
    private Pipe<Long> editAction;
    private Pipe<Long> deleteAction;

    public TracksViewHolder(TrackItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;

        binding.getRoot().setOnClickListener(view -> {
            if (clickAction != null)
                clickAction.push(track);
        });

        binding.actionChangeTrack.setOnClickListener(view -> {
            if (editAction != null) editAction.push(track.getId());
        });

        binding.actionDeleteTrack.setOnClickListener(view -> {
            if (deleteAction != null) deleteAction.push(track.getId());
        });
    }

    public void setClickAction(Pipe<Track> clickAction) {
        this.clickAction = clickAction;
    }

    public void setEditAction(Pipe<Long> editAction) {
        this.editAction = editAction;
    }

    public void setDeleteAction(Pipe<Long> deleteAction) {
        this.deleteAction = deleteAction;
    }

    public void bind(Track track) {
        this.track = track;
        binding.setTrack(track);
        binding.executePendingBindings();
    }
}
