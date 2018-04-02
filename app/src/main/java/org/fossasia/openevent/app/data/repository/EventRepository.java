package org.fossasia.openevent.app.data.repository;

import android.support.annotation.NonNull;

import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.data.IUtilModel;
import org.fossasia.openevent.app.data.db.IDatabaseRepository;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.models.EventStatistics;
import org.fossasia.openevent.app.data.models.Event_Table;
import org.fossasia.openevent.app.data.models.Ticket;
import org.fossasia.openevent.app.data.models.User;
import org.fossasia.openevent.app.data.network.EventService;
import org.fossasia.openevent.app.utils.JWTUtils;

import java.util.List;

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

    private void saveEvent(Event event) {
        event.setComplete(true);

        databaseRepository
            .save(Event.class, event)
            .subscribe();

        List<Ticket> tickets = event.getTickets();
        if (tickets != null) {
            for (Ticket ticket : tickets)
                ticket.setEvent(event);

            databaseRepository
                .saveList(Ticket.class, tickets)
                .subscribe();
        }
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
                .doOnNext(this::saveEvent));

        return new AbstractObservableBuilder<Event>(utilModel)
            .reload(reload)
            .withDiskObservable(diskObservable)
            .withNetworkObservable(networkObservable)
            .build();
    }

    @NonNull
    @Override
    public Observable<Event> getEvents(boolean reload) {
        Observable<Event> diskObservable = Observable.defer(() ->
            databaseRepository.getAllItems(Event.class)
        );

        Observable<Event> networkObservable = Observable.defer(() ->
            eventService.getEvents(JWTUtils.getIdentity(getAuthorization()))
                .doOnNext(events -> syncSave(Event.class, events, Event::getId, Event_Table.id).subscribe())
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
