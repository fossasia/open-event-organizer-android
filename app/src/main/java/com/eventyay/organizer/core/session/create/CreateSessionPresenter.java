package com.eventyay.organizer.core.session.create;

import com.eventyay.organizer.common.mvp.presenter.AbstractBasePresenter;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.session.Session;
import com.eventyay.organizer.data.session.SessionRepository;
import com.eventyay.organizer.data.tracks.Track;
import com.eventyay.organizer.utils.DateUtils;
import com.eventyay.organizer.utils.StringUtils;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeParseException;

import javax.inject.Inject;

import static com.eventyay.organizer.common.rx.ViewTransformers.dispose;
import static com.eventyay.organizer.common.rx.ViewTransformers.progressiveErroneous;

public class CreateSessionPresenter extends AbstractBasePresenter<CreateSessionView> {

    private final SessionRepository sessionRepository;
    private Session session = new Session();

    @Inject
    public CreateSessionPresenter(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;

        LocalDateTime current = LocalDateTime.now();

        String isoDate = DateUtils.formatDateToIso(current);
        session.setStartsAt(isoDate);
        session.setEndsAt(isoDate);
    }

    @Override
    public void start() {
        // Nothing to do
    }

    public Session getSession() {
        return session;
    }

    private boolean verify() {
        try {
            ZonedDateTime start = DateUtils.getDate(session.getStartsAt());
            ZonedDateTime end = DateUtils.getDate(session.getEndsAt());

            if (!end.isAfter(start)) {
                getView().showError("End time should be after start time");
                return false;
            }
            return true;
        } catch (DateTimeParseException pe) {
            getView().showError("Please enter date in correct format");
            return false;
        }
    }

    protected void nullifyEmptyFields(Session session) {
        session.setSlidesUrl(StringUtils.emptyToNull(session.getSlidesUrl()));
        session.setAudioUrl(StringUtils.emptyToNull(session.getAudioUrl()));
        session.setVideoUrl(StringUtils.emptyToNull(session.getVideoUrl()));
        session.setSignupUrl(StringUtils.emptyToNull(session.getSignupUrl()));
    }

    //Used for loading the session information on start
    public void loadSession(long sessionId) {
        sessionRepository
            .getSession(sessionId, false)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneous(getView()))
            .doFinally(this::showSession)
            .subscribe(loadedSession -> this.session = (Session) loadedSession, Logger::logError);
    }

    private void showSession() {
        getView().setSession(session);
    }

    //method called for updating an session
    public void updateSession(long trackId, long eventId) {
        Track track = new Track();
        Event event = new Event();

        track.setId(trackId);
        event.setId(eventId);
        session.setTrack(track);
        session.setEvent(event);

        sessionRepository
            .updateSession(session)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneous(getView()))
            .subscribe(updatedSession -> {
                    getView().onSuccess("Session Updated Successfully");
                    getView().dismiss();
            }, Logger::logError);
    }

    public void createSession(long trackId, long eventId) {
        if (!verify())
            return;

        Track track = new Track();
        Event event = new Event();

        track.setId(trackId);
        event.setId(eventId);
        session.setTrack(track);
        session.setEvent(event);

        nullifyEmptyFields(session);

        sessionRepository
            .createSession(session)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneous(getView()))
            .subscribe(createdSession -> {
                getView().onSuccess("Session Created");
                getView().dismiss();
            }, Logger::logError);
    }
}
