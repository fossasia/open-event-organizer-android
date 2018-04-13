package org.fossasia.openevent.app.core.track.create;

import org.fossasia.openevent.app.common.ContextManager;
import org.fossasia.openevent.app.common.mvp.presenter.AbstractBasePresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.tracks.Track;
import org.fossasia.openevent.app.data.tracks.TrackRepository;
import org.fossasia.openevent.app.utils.StringUtils;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneous;

public class CreateTrackPresenter extends AbstractBasePresenter<CreateTrackView> {

    private final TrackRepository trackRepository;
    private final Track track = new Track();

    @Inject
    public CreateTrackPresenter(TrackRepository trackRepository) {
        this.trackRepository = trackRepository;
    }

    @Override
    public void start() {
        // nothing to do
    }

    public Track getTrack() {
        return track;
    }

    private void nullifyEmptyFields(Track track) {
        track.setDescription(StringUtils.emptyToNull(track.getDescription()));
    }

    public void createTrack() {
        nullifyEmptyFields(track);

        track.setEvent(ContextManager.getSelectedEvent());

        trackRepository
            .createTrack(track)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneous(getView()))
            .subscribe(createdTrack -> {
                getView().onSuccess("Track Created");
                getView().dismiss();
            }, Logger::logError);
    }
}
