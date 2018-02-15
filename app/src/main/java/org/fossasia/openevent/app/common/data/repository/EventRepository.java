package org.fossasia.openevent.app.common.data.repository;

import android.support.annotation.NonNull;

import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.common.data.contract.IUtilModel;
import org.fossasia.openevent.app.common.data.db.contract.IDatabaseRepository;
import org.fossasia.openevent.app.common.data.models.Event;
import org.fossasia.openevent.app.common.data.models.EventStatistics;
import org.fossasia.openevent.app.common.data.models.Event_Table;
import org.fossasia.openevent.app.common.data.models.User;
import org.fossasia.openevent.app.common.data.network.EventService;
import org.fossasia.openevent.app.common.data.repository.contract.IEventRepository;
import org.fossasia.openevent.app.common.utils.core.JWTUtils;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class EventRepository extends Repository implements IEventRepository {

    @Inject
    public EventRepository(IUtilModel utilModel, IDatabaseRepository databaseRepository, EventService eventService) {
        super(utilModel, databaseRepository, eventService);
    }

    @Override
    public Observable<User> getOrganiser(boolean reload) {
        Observable<User> diskObservable = Observable.defer(() ->
            databaseRepository
                .getAllItems(User.class)
                .firstElement()
                .toObservable()
        );

        Observable<User> networkObservable = Observable.defer(() ->
            eventService
                .getUser(JWTUtils.getIdentity(getAuthorization()))
                .doOnNext(user -> databaseRepository
                    .save(User.class, user)
                    .subscribe()
                )
        );

        return new AbstractObservableBuilder<User>(utilModel)
            .reload(reload)
            .withDiskObservable(diskObservable)
            .withNetworkObservable(networkObservable)
            .build();
    }

    @Override
    public Observable<Event> getEvent(long eventId, boolean reload) {
        Observable<Event> diskObservable = Observable.defer(() ->
            databaseRepository
                .getItems(Event.class, Event_Table.id.eq(eventId))
                .filter(Event::isComplete)
                .take(1)
        );

        Observable<Event> networkObservable = Observable.defer(() ->
            eventService
                .getEvent(eventId)
                .doOnNext(event -> {
                    event.setComplete(true);

                    databaseRepository
                        .save(Event.class, event)
                        .subscribe();
                }
        ));

        return new AbstractObservableBuilder<Event>(utilModel)
            .reload(reload)
            .withDiskObservable(diskObservable)
            .withNetworkObservable(networkObservable)
            .build();
    }

    @Override
    public Observable<Event> getEvents(boolean reload) {
        Observable<Event> diskObservable = Observable.defer(() ->
            databaseRepository.getAllItems(Event.class)
        );

        Observable<Event> networkObservable = Observable.defer(() ->
            eventService.getEvents(JWTUtils.getIdentity(getAuthorization()))
                .doOnNext(events -> databaseRepository
                    .deleteAll(Event.class)
                    .concatWith(databaseRepository.saveList(Event.class, events))
                    .subscribe())
                .flatMapIterable(events -> events));

        return new AbstractObservableBuilder<Event>(utilModel)
            .reload(reload)
            .withDiskObservable(diskObservable)
            .withNetworkObservable(networkObservable)
            .build();
    }

    @NonNull
    @Override
    public Observable<Event> updateEvent(Event event) {
        if (!utilModel.isConnected()) {
            return Observable.error(new Throwable(Constants.NO_NETWORK));
        }

        event.setTickets(null);
        return eventService.patchEvent(event.getId(), event)
            .doOnNext(updatedEvent -> databaseRepository
                .update(Event.class, updatedEvent)
                .subscribe())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Event> createEvent(Event event) {
        if (!utilModel.isConnected()) {
            return Observable.error(new Throwable(Constants.NO_NETWORK));
        }

        return eventService.postEvent(event)
            .doOnNext(created -> databaseRepository
                .save(Event.class, created)
                .subscribe())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<EventStatistics> getEventStatistics(long id) {
        if (!utilModel.isConnected()) {
            return Observable.error(new Throwable(Constants.NO_NETWORK));
        }

        return eventService.getEventStatistics(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }
}
