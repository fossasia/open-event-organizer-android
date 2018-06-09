package org.fossasia.openevent.app.data.speakerscall;

import io.reactivex.Observable;

public interface SpeakersCallRepository {

    Observable<SpeakersCall> getSpeakersCall(long id, boolean reload);
}
