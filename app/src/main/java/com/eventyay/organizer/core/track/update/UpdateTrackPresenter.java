package com.eventyay.organizer.core.track.update;

import android.graphics.Color;

import com.eventyay.organizer.common.ContextManager;
import com.eventyay.organizer.common.mvp.presenter.AbstractBasePresenter;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.tracks.Track;
import com.eventyay.organizer.data.tracks.TrackRepository;
import com.eventyay.organizer.utils.StringUtils;

import javax.inject.Inject;

import static com.eventyay.organizer.common.rx.ViewTransformers.dispose;
import static com.eventyay.organizer.common.rx.ViewTransformers.progressiveErroneous;

public class UpdateTrackPresenter extends AbstractBasePresenter<UpdateTrackView> {
    private final TrackRepository trackRepository;
    private Track track;

    @Inject
    public UpdateTrackPresenter(TrackRepository trackRepository) {
        this.trackRepository = trackRepository;
    }

    @Override
    public void start() {
        // Nothing to do
    }

    public void loadTrack(long trackId) {
        trackRepository
            .getTrack(trackId, false)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneous(getView()))
            .doFinally(this::showTrack)
            .subscribe(loadedTrack -> this.track = loadedTrack, Logger::logError);
    }

    private void showTrack() {
        getView().setTrack(track);
    }

    private void nullifyEmptyFields(Track track) {
        track.setDescription(StringUtils.emptyToNull(track.getDescription()));
    }

    public void updateTrack() {
        nullifyEmptyFields(track);

        long eventId = ContextManager.getSelectedEvent().getId();
        Event event = new Event();
        event.setId(eventId);
        track.setEvent(event);

        trackRepository
            .updateTrack(track)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneous(getView()))
            .subscribe(updatedTrack -> {
                getView().onSuccess("Track Updated");
                getView().dismiss();
            }, Logger::logError);
    }

    public int getRed() {
        String colorRed = track.getColor();
        return Integer.valueOf(colorRed.substring(1, 3), 16);
    }

    public int getGreen() {
        String colorGreen = track.getColor();
        return Integer.valueOf(colorGreen.substring(3, 5), 16);
    }

    public int getBlue() {
        String colorBlue = track.getColor();
        return Integer.valueOf(colorBlue.substring(5, 7), 16);
    }

    public int getColorRGB() {
        return Color.rgb(getRed(), getGreen(), getBlue());
    }
}
