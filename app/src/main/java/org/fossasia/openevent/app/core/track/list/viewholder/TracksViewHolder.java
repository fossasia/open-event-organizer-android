package org.fossasia.openevent.app.core.track.list.viewholder;

import android.support.v7.widget.RecyclerView;

import org.fossasia.openevent.app.data.tracks.Track;
import org.fossasia.openevent.app.databinding.TrackItemBinding;

public class TracksViewHolder extends RecyclerView.ViewHolder {
    private final TrackItemBinding binding;

    public TracksViewHolder(TrackItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Track track) {
        binding.setTrack(track);
        binding.executePendingBindings();
    }
}
