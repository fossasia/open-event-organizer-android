package org.fossasia.openevent.app.data.tracks;

import androidx.annotation.NonNull;

import io.reactivex.Completable;
import io.reactivex.Observable;

public interface TrackRepository {
    Observable<Track> getTracks(long id, boolean reload);

    Observable<Track> createTrack(Track track);

    @NonNull
    Observable<Track> getTrack(long trackId, boolean reload);

    @NonNull
    Observable<Track> updateTrack(Track track);

    @NonNull
    Completable deleteTrack(long id);
}
