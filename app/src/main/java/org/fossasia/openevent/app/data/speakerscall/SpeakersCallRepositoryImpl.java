package org.fossasia.openevent.app.data.speakerscall;

import android.support.annotation.NonNull;

import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.data.Repository;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

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

    @Override
    public Observable<SpeakersCall> createSpeakersCall(SpeakersCall speakersCall) {
        if (!repository.isConnected()) {
            return Observable.error(new Throwable(Constants.NO_NETWORK));
        }

        return speakersCallApi
            .postSpeakersCall(speakersCall)
            .doOnNext(created -> {
                created.setEvent(speakersCall.getEvent());
                repository
                    .save(SpeakersCall.class, created)
                    .subscribe();
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    @Override
    public Observable<SpeakersCall> updateSpeakersCall(SpeakersCall speakersCall) {
        if (!repository.isConnected()) {
            return Observable.error(new Throwable(Constants.NO_NETWORK));
        }

        return speakersCallApi
            .updateSpeakersCall(speakersCall.getId(), speakersCall)
            .doOnNext(updatedSpeakersCall -> repository
                .update(SpeakersCall.class, updatedSpeakersCall)
                .subscribe())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }
}
