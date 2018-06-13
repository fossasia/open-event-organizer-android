package org.fossasia.openevent.app.core.speaker.list;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.core.speaker.list.viewholder.SpeakerViewHolder;
import org.fossasia.openevent.app.data.speaker.Speaker;

import java.util.List;

public class SpeakersAdapter extends RecyclerView.Adapter<SpeakerViewHolder> {
    private final SpeakersPresenter speakersPresenter;
    private final List<Speaker> speakers;

    public SpeakersAdapter(SpeakersPresenter speakersPresenter) {
        this.speakersPresenter = speakersPresenter;
        this.speakers = speakersPresenter.getSpeakers();
    }

    @NonNull
    @Override
    public SpeakerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        SpeakerViewHolder speakerViewHolder = new SpeakerViewHolder(
            DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                R.layout.speaker_item, viewGroup, false));

        speakerViewHolder.setClickAction(speakersPresenter::click);

        return speakerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SpeakerViewHolder speakerViewHolder, int position) {
        speakerViewHolder.bind(speakers.get(position));
    }

    @Override
    public int getItemCount() {
        return speakers.size();
    }
}
