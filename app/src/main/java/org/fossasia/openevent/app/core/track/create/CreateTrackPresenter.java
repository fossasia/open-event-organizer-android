package org.fossasia.openevent.app.core.track.create;

import android.graphics.Color;
import org.fossasia.openevent.app.common.ContextManager;
import org.fossasia.openevent.app.common.mvp.presenter.AbstractBasePresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.data.tracks.Track;
import org.fossasia.openevent.app.data.tracks.TrackRepository;
import org.fossasia.openevent.app.utils.StringUtils;

import java.util.Random;
import javax.inject.Inject;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneous;

public class CreateTrackPresenter extends AbstractBasePresenter<CreateTrackView> {

    private final TrackRepository trackRepository;
    private final Track track = new Track();
    private int colorRed, colorGreen, colorBlue;

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
        int colorRGB = Color.rgb(colorRed, colorGreen, colorBlue);
        return String.format("#%06X",(0xFFFFFF & colorRGB));
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

}
