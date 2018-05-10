package org.fossasia.openevent.app.data.speakerscall;

import org.fossasia.openevent.app.data.Repository;

import javax.inject.Inject;

import io.reactivex.Observable;

public class SpeakersCallRepositoryImpl implements SpeakersCallRepository {

    private final SpeakersCallApi speakersCallApi;
    private final Repository repository;

    @Inject
    public SpeakersCallRepositoryImpl(SpeakersCallApi speakersCallApi, Repository repository) {
        this.speakersCallApi = speakersCallApi;
        this.repository = repository;
    }

    @Override
    public Observable<SpeakersCall> getSpeakersCall(long eventId, boolean reload) {
        Observable<SpeakersCall> diskObservable = Observable.defer(() ->
            repository.getItems(SpeakersCall.class, SpeakersCall_Table.event_id.eq(eventId))
        );

        Observable<SpeakersCall> networkObservable = Observable.defer(() ->
            speakersCallApi.getSpeakersCall(eventId)
                .doOnNext(speakersCall -> repository
                        .save(SpeakersCall.class, speakersCall)
                        .subscribe()));

        return repository.observableOf(SpeakersCall.class)
            .reload(reload)
            .withDiskObservable(diskObservable)
            .withNetworkObservable(networkObservable)
            .build();
    }
}
