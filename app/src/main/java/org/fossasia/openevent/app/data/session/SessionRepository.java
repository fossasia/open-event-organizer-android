package org.fossasia.openevent.app.data.session;

import android.support.annotation.NonNull;

import io.reactivex.Completable;
import io.reactivex.Observable;

public interface SessionRepository {

    Observable<Session> getSessions(long id, boolean reload);

    @NonNull
    Observable<Session> getSession(long sessionId, boolean reload);

    Observable<Session> createSession(Session session);

    @NonNull
    Observable<Session> updateSession(Session session);

    Completable deleteSession(long id);
}
