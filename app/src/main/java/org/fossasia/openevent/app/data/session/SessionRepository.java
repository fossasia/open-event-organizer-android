package org.fossasia.openevent.app.data.session;

import io.reactivex.Observable;

public interface SessionRepository {

    Observable<Session> getSessions(long id, boolean reload);
}
