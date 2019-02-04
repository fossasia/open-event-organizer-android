package com.eventyay.organizer.core.session.create;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;

import com.eventyay.organizer.common.livedata.SingleEventLiveData;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.session.Session;
import com.eventyay.organizer.data.session.SessionRepository;
import com.eventyay.organizer.data.tracks.Track;
import com.eventyay.organizer.utils.DateUtils;
import com.eventyay.organizer.utils.ErrorUtils;
import com.eventyay.organizer.utils.StringUtils;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeParseException;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class CreateSessionViewModel extends ViewModel {

    private final SessionRepository sessionRepository;
    private Session session = new Session();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final SingleEventLiveData<Boolean> progress = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> error = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> success = new SingleEventLiveData<>();
    private final SingleEventLiveData<Void> dismiss = new SingleEventLiveData<>();
    private final SingleEventLiveData<Session> sessionLiveData = new SingleEventLiveData<>();

    @Inject
    public CreateSessionViewModel(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
        LocalDateTime current = LocalDateTime.now();

        String isoDate = DateUtils.formatDateToIso(current);
        session.setStartsAt(isoDate);
        session.setEndsAt(isoDate);
    }

    private boolean verify() {
        try {
            ZonedDateTime start = DateUtils.getDate(session.getStartsAt());
            ZonedDateTime end = DateUtils.getDate(session.getEndsAt());

            if (!end.isAfter(start)) {
                //getView().showError("End time should be after start time");
                return false;
            }
            return true;
        } catch (DateTimeParseException pe) {
            //getView().showError("Please enter date in correct format");
            return false;
        }
    }

    @VisibleForTesting
    protected void nullifyEmptyFields(Session session) {
        session.setSlidesUrl(StringUtils.emptyToNull(session.getSlidesUrl()));
        session.setAudioUrl(StringUtils.emptyToNull(session.getAudioUrl()));
        session.setVideoUrl(StringUtils.emptyToNull(session.getVideoUrl()));
        session.setSignupUrl(StringUtils.emptyToNull(session.getSignupUrl()));
    }

    public Session getSession() {
        return session;
    }

    public LiveData<Boolean> getProgress() {
        return progress;
    }

    public LiveData<String> getSuccess() {
        return success;
    }

    public LiveData<Void> getDismiss() {
        return dismiss;
    }

    public LiveData<String> getError() {
        return error;
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

        compositeDisposable.add(
            sessionRepository
                .createSession(session)
                .doOnSubscribe(disposable -> progress.setValue(true))
                .doFinally(() -> progress.setValue(false))
                .subscribe(createdSession -> {
                    success.setValue("Session Created");
                    dismiss.call();
                }, throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
    }

    public void loadSession(long sessionId) {
        compositeDisposable.add(
            sessionRepository
                .getSession(sessionId, false)
                .doOnSubscribe(disposable -> progress.setValue(true))
                .doFinally(() -> {
                    progress.setValue(false);
                    sessionLiveData.setValue(session);
                }).subscribe(loadedSession -> this.session = loadedSession, Logger::logError));
    }

    public LiveData<Session> getSessionLiveData() {
        return sessionLiveData;
    }

    public void updateSession(long trackId, long eventId) {
        nullifyEmptyFields(session);
        Track track = new Track();
        Event event = new Event();

        track.setId(trackId);
        event.setId(eventId);
        session.setTrack(track);
        session.setEvent(event);

        compositeDisposable.add(
            sessionRepository
                .updateSession(session)
                .doOnSubscribe(disposable -> progress.setValue(true))
                .doFinally(() -> progress.setValue(false))
                .subscribe(updatedSession -> {
                    success.setValue("Session Updated");
                    dismiss.call();
                }, throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
    }
}
