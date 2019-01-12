package com.eventyay.organizer.core.track.create;

import android.graphics.Color;
import com.eventyay.organizer.common.ContextManager;
import com.eventyay.organizer.common.mvp.presenter.AbstractBasePresenter;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.tracks.Track;
import com.eventyay.organizer.data.tracks.TrackRepository;
import com.eventyay.organizer.utils.StringUtils;

import java.util.Random;
import javax.inject.Inject;

import static com.eventyay.organizer.common.rx.ViewTransformers.dispose;
import static com.eventyay.organizer.common.rx.ViewTransformers.progressiveErroneous;

public class CreateTrackPresenter extends AbstractBasePresenter<CreateTrackView> {

    private final TrackRepository trackRepository;
    private final Track track = new Track();
    private int colorRed, colorGreen, colorBlue, colorRGB;

    @Inject
    public CreateTrackPresenter(TrackRepository trackRepository) {
        this.trackRepository = trackRepository;
    }

    @Override
    public void start() {
        track.setColor(getRandomColor());
    }

    public Track getTrack() {
        return track;
    }

    private void nullifyEmptyFields(Track track) {
        track.setDescription(StringUtils.emptyToNull(track.getDescription()));
    }

    public void createTrack() {
        nullifyEmptyFields(track);

        long eventId = ContextManager.getSelectedEvent().getId();
        Event event = new Event();
        event.setId(eventId);
        track.setEvent(event);

        trackRepository
            .createTrack(track)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneous(getView()))
            .subscribe(createdTrack -> {
                getView().onSuccess("Track Created");
                getView().dismiss();
            }, Logger::logError);
    }

    public String getRandomColor() {
        Random random = new Random();
        colorRed = random.nextInt(255);
        colorGreen = random.nextInt(255);
        colorBlue = random.nextInt(255);
        colorRGB = Color.rgb(colorRed, colorGreen, colorBlue);
        return String.format("#%06X", 0xFFFFFF & colorRGB);
    }

    public void trimFields() {
        track.setName(track.name.trim());
        track.setColor(track.color.trim());
    }

    public int getRed() {
        return colorRed;
    }

    public int getGreen() {
        return colorGreen;
    }

    public int getBlue() {
        return colorBlue;
    }

    public int getColorRGB() {
        return colorRGB;
    }
}
