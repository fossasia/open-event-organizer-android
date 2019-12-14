package com.eventyay.organizer.data.tracks;


import androidx.annotation.NonNull;

import com.eventyay.organizer.common.Constants;
import com.eventyay.organizer.data.RateLimiter;
import com.eventyay.organizer.data.Repository;

import org.threeten.bp.Duration;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TrackRepositoryImpl implements TrackRepository {
    private final TrackApi trackApi;
    private final Repository repository;
    private final RateLimiter<String> rateLimiter = new RateLimiter<>(Duration.ofMinutes(10));

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
            .withRateLimiterConfig("Tracks", rateLimiter)
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

    @NonNull
    @Override
    public Observable<Track> getTrack(long trackId, boolean reload) {
        Observable<Track> diskObservable = Observable.defer(() ->
            repository
                .getItems(Track.class, Track_Table.id.eq(trackId)).take(1)
        );

        Observable<Track> networkObservable = Observable.defer(() ->
            trackApi.getTrack(trackId)
                .doOnNext(track -> repository
                    .save(Track.class, track)
                    .subscribe()));

        return repository
            .observableOf(Track.class)
            .reload(reload)
            .withDiskObservable(diskObservable)
            .withNetworkObservable(networkObservable)
            .build();
    }

    @NonNull
    @Override
    public Observable<Track> updateTrack(Track track) {
        if (!repository.isConnected()) {
            return Observable.error(new Throwable(Constants.NO_NETWORK));
        }

        return trackApi
            .updateTrack(track.getId(), track)
            .doOnNext(updatedTrack -> repository
                .update(Track.class, updatedTrack)
                .subscribe())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    @Override
    public Completable deleteTrack(long id) {
        if (!repository.isConnected()) {
            return Completable.error(new Throwable(Constants.NO_NETWORK));
        }

        return trackApi.deleteTrack(id)
            .doOnComplete(() -> repository
                .delete(Track.class, Track_Table.id.eq(id))
                .subscribe())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }
}
