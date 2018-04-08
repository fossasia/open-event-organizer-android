package org.fossasia.openevent.app.data.tracks;

import io.reactivex.Observable;

public interface TrackRepository {

    Observable<Track> getTracks(long id, boolean reload);
}
