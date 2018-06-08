package org.fossasia.openevent.app.data.speaker;

import io.reactivex.Observable;

public interface SpeakerRepository {

    Observable<Speaker> getSpeakers(long id, boolean reload);

    Observable<Speaker> getSpeaker(long speakerId, boolean reload);
}
