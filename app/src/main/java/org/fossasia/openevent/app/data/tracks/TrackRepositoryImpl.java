package org.fossasia.openevent.app.data.tracks;


import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.data.Repository;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TrackRepositoryImpl implements TrackRepository {
    private final TrackApi trackApi;
    private final Repository repository;

    @Inject
    public TrackRepositoryImpl(TrackApi trackApi, Repository repository) {
        this.trackApi = trackApi;
        this.repository = repository;
    }

    @Override
    public Observable<Track> getTracks(long eventId, boolean reload) {
        Observable<Track> diskObservable = Observable.defer(() ->
            repository.getItems(Track.class, Track_Table.event_id.eq(eventId))
        );

        Observable<Track> networkObservable = Observable.defer(() ->
            trackApi.getTracks(eventId)
                .doOnNext(tracks -> repository.syncSave(Track.class, tracks, Track::getId, Track_Table.id).subscribe())
                .flatMapIterable(tracks -> tracks));

        return repository.observableOf(Track.class)
            .reload(reload)
            .withDiskObservable(diskObservable)
            .withNetworkObservable(networkObservable)
            .build();
    }

    @Override
    public Observable<Track> createTrack(Track track) {
        if (!repository.isConnected()) {
            return Observable.error(new Throwable(Constants.NO_NETWORK));
        }

        return trackApi
            .postTrack(track)
            .doOnNext(created -> {
                created.setEvent(track.getEvent());
                repository
                    .save(Track.class, created)
                    .subscribe();
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }
}
