package org.fossasia.openevent.app.data.speaker;

import org.fossasia.openevent.app.data.Repository;
import org.fossasia.openevent.app.data.session.Session;
import org.fossasia.openevent.app.data.session.Session_Table;

import java.util.List;

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

    private void saveSpeaker(Speaker speaker) {
        repository
            .update(Speaker.class, speaker)
            .subscribe();

        List<Session> sessions = speaker.getSessions();
        if (sessions != null) {
            for (Session session : sessions)
                session.setSpeaker(speaker);

            repository
                .saveList(Session.class, sessions)
                .subscribe();
        }
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

    @Override
    public Observable<Speaker> getSpeaker(long speakerId, boolean reload) {
        Observable<Speaker> diskObservable = Observable.defer(() ->
            repository.getJoinedItems(Speaker.class, Session.class, Speaker_Table.id.eq(speakerId),
                Session_Table.speaker_id.eq(speakerId)).take(1)
        );

        Observable<Speaker> networkObservable = Observable.defer(() ->
            speakerApi.getSpeaker(speakerId)
                .doOnNext(this::saveSpeaker));

        return repository
            .observableOf(Speaker.class)
            .reload(reload)
            .withDiskObservable(diskObservable)
            .withNetworkObservable(networkObservable)
            .build();
    }
}
