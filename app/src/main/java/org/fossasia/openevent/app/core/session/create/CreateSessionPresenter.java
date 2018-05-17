package org.fossasia.openevent.app.core.session.create;

import org.fossasia.openevent.app.common.mvp.presenter.AbstractBasePresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.data.session.Session;
import org.fossasia.openevent.app.data.session.SessionRepository;
import org.fossasia.openevent.app.data.tracks.Track;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneous;

public class CreateSessionPresenter extends AbstractBasePresenter<CreateSessionView> {

    private final SessionRepository sessionRepository;
    private final Session session = new Session();

    @Inject
    public CreateSessionPresenter(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public void start() {
        // Nothing to do
    }

    public Session getSession() {
        return session;
    }

    public void createSession(long trackId, long eventId) {
        Track track = new Track();
        Event event = new Event();
        track.setId(trackId);
        event.setId(eventId);
        session.setTrack(track);
        session.setEvent(event);

        sessionRepository
            .createSession(session)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneous(getView()))
            .subscribe(createdSession -> getView().onSuccess("Session Created"), Logger::logError);
    }
}
