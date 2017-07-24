package org.fossasia.openevent.app.common.data.repository;

import org.fossasia.openevent.app.common.data.contract.IUtilModel;
import org.fossasia.openevent.app.common.data.db.contract.IDatabaseRepository;
import org.fossasia.openevent.app.common.data.models.Event;
import org.fossasia.openevent.app.common.data.models.Event_Table;
import org.fossasia.openevent.app.common.data.models.User;
import org.fossasia.openevent.app.common.data.network.EventService;
import org.fossasia.openevent.app.common.data.repository.contract.IEventRepository;
import org.fossasia.openevent.app.common.utils.core.JWTUtils;

import javax.inject.Inject;

import io.reactivex.Observable;

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

}
