package org.fossasia.openevent.app.data.speaker;

import org.fossasia.openevent.app.data.Repository;

import javax.inject.Inject;

import io.reactivex.Observable;

public class SpeakerRepositoryImpl implements SpeakerRepository {

    private final SpeakerApi speakerApi;
    private final Repository repository;

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
            .withDiskObservable(diskObservable)
            .withNetworkObservable(networkObservable)
            .build();
    }
}
