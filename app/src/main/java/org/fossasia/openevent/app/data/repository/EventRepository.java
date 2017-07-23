package org.fossasia.openevent.app.data.repository;

import com.raizlabs.android.dbflow.rx2.language.RXSQLite;
import com.raizlabs.android.dbflow.sql.language.Method;

import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.data.db.QueryHelper;
import org.fossasia.openevent.app.data.db.contract.IDatabaseRepository;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.data.models.Attendee_Table;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.models.Event_Table;
import org.fossasia.openevent.app.data.models.Ticket;
import org.fossasia.openevent.app.data.models.Ticket_Table;
import org.fossasia.openevent.app.data.models.User;
import org.fossasia.openevent.app.data.models.query.TypeQuantity;
import org.fossasia.openevent.app.data.network.EventService;
import org.fossasia.openevent.app.data.repository.contract.IEventRepository;

import java.util.NoSuchElementException;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

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
                .getUser(getAuthorization())
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
                })
        );

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
            eventService.getEvents(getAuthorization())
                .doOnNext(events -> databaseRepository.deleteAll(Event.class)
                    .concatWith(databaseRepository.saveList(Event.class, events))
                    .subscribe())
                .flatMapIterable(events -> events))
                .doOnEach(eventNotification -> {
                    // Download all complete events in one go
                    if (!eventNotification.isOnNext())
                        return;
                    Event event = eventNotification.getValue();
                    getEvent(event.getId(), false)
                        .subscribe(
                            eventDownloaded ->
                                Timber.d("Downloaded complete event %s", eventDownloaded.getName()),
                            Timber::e);
                });

        return new AbstractObservableBuilder<Event>(utilModel)
            .reload(reload)
            .withDiskObservable(diskObservable)
            .withNetworkObservable(networkObservable)
            .build();
    }

    public Observable<TypeQuantity> getTicketsQuantity(long eventId) {
        return new QueryHelper<Ticket>()
            .select(Ticket_Table.type)
            .sum(Ticket_Table.quantity, "quantity")
            .from(Ticket.class)
            .equiJoin(Event.class, Ticket_Table.event_id, Event_Table.id)
            .where(Ticket_Table.event_id.eq(eventId))
            .group(Ticket_Table.type)
            .toCustomObservable(TypeQuantity.class)
            .subscribeOn(Schedulers.io());
    }

    public Observable<TypeQuantity> getSoldTicketsQuantity(long eventId) {
        return new QueryHelper<Ticket>()
            .select(Ticket_Table.type)
            .method(Method.count(), "quantity")
            .from(Ticket.class)
            .equiJoin(Attendee.class, Attendee_Table.ticket_id, Ticket_Table.id)
            .equiJoin(Event.class, Attendee_Table.eventId, Event_Table.id)
            .where(Ticket_Table.event_id.eq(eventId))
            .group(Ticket_Table.type)
            .toCustomObservable(TypeQuantity.class)
            .subscribeOn(Schedulers.io());
    }

    public Observable<Long> getCheckedInAttendees(long eventId) {
        return new QueryHelper<Attendee>()
            .method(Method.count(), "sum")
            .from(Attendee.class)
            .equiJoin(Event.class, Event_Table.id, Attendee_Table.eventId)
            .where(Attendee_Table.checkedIn.eq(true))
            .and(Attendee_Table.eventId.eq(eventId))
            .count()
            .subscribeOn(Schedulers.io());
    }

    public Single<Float> getTotalSale(long eventId) {
        return RXSQLite.rx(
            new QueryHelper<Ticket>()
                .sum(Ticket_Table.price, "price")
                .from(Ticket.class)
                .equiJoin(Attendee.class, Attendee_Table.ticket_id, Ticket_Table.id)
                .equiJoin(Event.class, Ticket_Table.event_id, Event_Table.id)
                .where(Ticket_Table.event_id.eq(eventId))
                .build())
            .query()
            .map(cursor -> {
                if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                    float result = cursor.getFloat(0);
                    cursor.close();
                    return result;
                }
                cursor.close();
                throw new NoSuchElementException();
            }).subscribeOn(Schedulers.io());
    }

}
