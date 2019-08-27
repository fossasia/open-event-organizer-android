package com.eventyay.organizer.data.session;

import androidx.annotation.NonNull;
import com.eventyay.organizer.common.Constants;
import com.eventyay.organizer.data.RateLimiter;
import com.eventyay.organizer.data.Repository;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;
import org.threeten.bp.Duration;

public class SessionRepositoryImpl implements SessionRepository {

    private final SessionApi sessionApi;
    private final Repository repository;
    private final RateLimiter<String> rateLimiter = new RateLimiter<>(Duration.ofMinutes(10));

    @Inject
    public SessionRepositoryImpl(SessionApi sessionApi, Repository repository) {
        this.sessionApi = sessionApi;
        this.repository = repository;
    }

    @Override
    public Observable<Session> getSessions(long trackId, boolean reload) {
        Observable<Session> diskObservable =
                Observable.defer(
                        () ->
                                repository.getItems(
                                        Session.class, Session_Table.track_id.eq(trackId)));

        Observable<Session> networkObservable =
                Observable.defer(
                        () ->
                                sessionApi
                                        .getSessions(trackId)
                                        .doOnNext(
                                                sessions ->
                                                        repository
                                                                .syncSave(
                                                                        Session.class,
                                                                        sessions,
                                                                        Session::getId,
                                                                        Session_Table.id)
                                                                .subscribe())
                                        .flatMapIterable(sessions -> sessions));

        return repository
                .observableOf(Session.class)
                .reload(reload)
                .withRateLimiterConfig("Sessions", rateLimiter)
                .withDiskObservable(diskObservable)
                .withNetworkObservable(networkObservable)
                .build();
    }

    @NonNull
    @Override
    public Observable<Session> getSession(long trackId, boolean reload) {
        Observable<Session> diskObservable =
                Observable.defer(
                        () ->
                                repository
                                        .getItems(Session.class, Session_Table.id.eq(trackId))
                                        .take(1));

        Observable<Session> networkObservable =
                Observable.defer(
                        () ->
                                sessionApi
                                        .getSession(trackId)
                                        .doOnNext(
                                                session ->
                                                        repository
                                                                .save(Session.class, session)
                                                                .subscribe()));

        return repository
                .observableOf(Session.class)
                .reload(reload)
                .withDiskObservable(diskObservable)
                .withNetworkObservable(networkObservable)
                .build();
    }

    @NonNull
    @Override
    public Observable<Session> updateSession(Session session) {
        if (!repository.isConnected()) {
            return Observable.error(new Throwable(Constants.NO_NETWORK));
        }

        return sessionApi
                .updateSession(session.getId(), session)
                .doOnNext(
                        updatedSession ->
                                repository.update(Session.class, updatedSession).subscribe())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Session> getSessionsUnderSpeaker(long speakerId, boolean reload) {
        Observable<Session> diskObservable =
                Observable.defer(
                        () ->
                                repository.getItems(
                                        Session.class, Session_Table.speaker_id.eq(speakerId)));

        Observable<Session> networkObservable =
                Observable.defer(
                        () ->
                                sessionApi
                                        .getSessionsUnderSpeaker(speakerId)
                                        .doOnNext(
                                                sessions ->
                                                        repository
                                                                .syncSave(
                                                                        Session.class,
                                                                        sessions,
                                                                        Session::getId,
                                                                        Session_Table.id)
                                                                .subscribe())
                                        .flatMapIterable(sessions -> sessions));

        return repository
                .observableOf(Session.class)
                .reload(reload)
                .withDiskObservable(diskObservable)
                .withNetworkObservable(networkObservable)
                .build();
    }

    @Override
    public Observable<Session> createSession(Session session) {
        if (!repository.isConnected()) {
            return Observable.error(new Throwable(Constants.NO_NETWORK));
        }

        return sessionApi
                .postSession(session)
                .doOnNext(
                        created -> {
                            created.setTrack(session.getTrack());
                            created.setEvent(session.getEvent());
                            repository.save(Session.class, created).subscribe();
                        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    @Override
    public Completable deleteSession(long id) {
        if (!repository.isConnected()) {
            return Completable.error(new Throwable(Constants.NO_NETWORK));
        }

        return sessionApi
                .deleteSession(id)
                .doOnComplete(
                        () -> repository.delete(Session.class, Session_Table.id.eq(id)).subscribe())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
