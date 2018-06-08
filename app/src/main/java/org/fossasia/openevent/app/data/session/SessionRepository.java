package org.fossasia.openevent.app.data.session;

import io.reactivex.Completable;
import io.reactivex.Observable;

public interface SessionRepository {

    Observable<Session> getSessions(long id, boolean reload);

    Observable<Session> getSessionsUnderSpeaker(long speakerId, boolean reload);

    Observable<Session> createSession(Session session);

    Completable deleteSession(long id);
}
