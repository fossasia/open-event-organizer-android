package org.fossasia.openevent.app.core.track.list.viewholder;

import android.support.v7.widget.RecyclerView;

import org.fossasia.openevent.app.common.Pipe;
import org.fossasia.openevent.app.data.tracks.Track;
import org.fossasia.openevent.app.databinding.TrackItemBinding;

public class TracksViewHolder extends RecyclerView.ViewHolder {
    private final TrackItemBinding binding;
    private Pipe<Track> clickAction;

    public TracksViewHolder(TrackItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Track track) {
        binding.setTrack(track);
        binding.actionChangeTrack.setOnClickListener(view -> {
            if (clickAction != null) clickAction.push(track);
        });
        binding.executePendingBindings();
    }

    public void setClickAction(Pipe<Track> clickAction) {
        this.clickAction = clickAction;
    }
}
