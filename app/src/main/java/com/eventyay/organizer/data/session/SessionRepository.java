package com.eventyay.organizer.data.session;

import androidx.annotation.NonNull;

import io.reactivex.Completable;
import io.reactivex.Observable;

public interface SessionRepository {

    Observable<Session> getSessions(long id, boolean reload);

    @NonNull
    Observable<Session> getSession(long sessionId, boolean reload);

    Observable<Session> getSessionsUnderSpeaker(long speakerId, boolean reload);

    Observable<Session> createSession(Session session);

    @NonNull
    Observable<Session> updateSession(Session session);

    Completable deleteSession(long id);
}
