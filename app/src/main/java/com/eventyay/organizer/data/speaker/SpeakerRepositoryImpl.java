package com.eventyay.organizer.data.speaker;

import com.eventyay.organizer.data.RateLimiter;
import com.eventyay.organizer.data.Repository;
import org.threeten.bp.Duration;

import javax.inject.Inject;

import io.reactivex.Observable;

public class SpeakerRepositoryImpl implements SpeakerRepository {

    private final SpeakerApi speakerApi;
    private final Repository repository;
    private final RateLimiter<String> rateLimiter = new RateLimiter<>(Duration.ofMinutes(10));

    @Inject
    public SpeakerRepositoryImpl(SpeakerApi speakerApi, Repository repository) {
        this.speakerApi = speakerApi;
        this.repository = repository;
    }

    @Override
    public Observable<Speaker> getSpeakers(long eventId, boolean reload) {
        Observable<Speaker> diskObservable = Observable.defer(() ->
            repository.getItems(Speaker.class, Speaker_Table.event_id.eq(eventId))
        );

        Observable<Speaker> networkObservable = Observable.defer(() ->
            speakerApi.getSpeakers(eventId)
                .doOnNext(speakers -> repository.syncSave(Speaker.class, speakers, Speaker::getId, Speaker_Table.id).subscribe())
                .flatMapIterable(speakers -> speakers));

        return repository.observableOf(Speaker.class)
            .reload(reload)
            .withRateLimiterConfig("Speakers", rateLimiter)
            .withDiskObservable(diskObservable)
            .withNetworkObservable(networkObservable)
            .build();
    }

    @Override
    public Observable<Speaker> getSpeaker(long speakerId, boolean reload) {
        Observable<Speaker> diskObservable = Observable.defer(() ->
            repository
                .getItems(Speaker.class, Speaker_Table.id.eq(speakerId)).take(1)
        );

        Observable<Speaker> networkObservable = Observable.defer(() ->
            speakerApi.getSpeaker(speakerId)
                .doOnNext(speaker -> repository
                    .save(Speaker.class, speaker)
                    .subscribe()));

        return repository
            .observableOf(Speaker.class)
            .reload(reload)
            .withDiskObservable(diskObservable)
            .withNetworkObservable(networkObservable)
            .build();
    }
}
