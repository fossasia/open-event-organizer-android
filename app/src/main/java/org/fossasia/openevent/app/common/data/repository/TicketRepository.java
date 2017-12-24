package org.fossasia.openevent.app.common.data.repository;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.rx2.language.RXSQLite;
import com.raizlabs.android.dbflow.sql.language.Method;

import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.common.data.contract.IUtilModel;
import org.fossasia.openevent.app.common.data.db.QueryHelper;
import org.fossasia.openevent.app.common.data.db.contract.IDatabaseRepository;
import org.fossasia.openevent.app.common.data.models.Attendee;
import org.fossasia.openevent.app.common.data.models.Attendee_Table;
import org.fossasia.openevent.app.common.data.models.Event;
import org.fossasia.openevent.app.common.data.models.Event_Table;
import org.fossasia.openevent.app.common.data.models.Ticket;
import org.fossasia.openevent.app.common.data.models.Ticket_Table;
import org.fossasia.openevent.app.common.data.models.query.TypeQuantity;
import org.fossasia.openevent.app.common.data.network.EventService;
import org.fossasia.openevent.app.common.data.repository.contract.ITicketRepository;

import java.util.NoSuchElementException;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TicketRepository extends Repository implements ITicketRepository {

    @Inject
    public TicketRepository(IUtilModel utilModel, IDatabaseRepository databaseRepository, EventService eventService) {
        super(utilModel, databaseRepository, eventService);
    }

    @NonNull
    @Override
    public Observable<Ticket> createTicket(Ticket ticket) {
        if (!utilModel.isConnected()) {
            return Observable.error(new Throwable(Constants.NO_NETWORK));
        }

        return eventService
            .postTicket(ticket)
            .doOnNext(created -> {
                created.setEvent(ticket.getEvent());
                databaseRepository.save(Ticket.class, created)
                    .subscribe();
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    @Override
    public Observable<Ticket> getTicket(long ticketId, boolean reload) {
        Observable<Ticket> diskObservable = Observable.defer(() ->
            databaseRepository.getItems(Ticket.class, Ticket_Table.id.eq(ticketId)).take(1)
        );

        Observable<Ticket> networkObservable = Observable.defer(() ->
            eventService.getTicket(ticketId)
                .doOnNext(ticket -> databaseRepository
                    .save(Ticket.class, ticket)
                    .subscribe()));

        return new AbstractObservableBuilder<Ticket>(utilModel)
            .reload(reload)
            .withDiskObservable(diskObservable)
            .withNetworkObservable(networkObservable)
            .build();
    }

    @NonNull
    @Override
    public Observable<Ticket> getTickets(long eventId, boolean reload) {
        Observable<Ticket> diskObservable = Observable.defer(() ->
            databaseRepository.getItems(Ticket.class, Ticket_Table.event_id.eq(eventId))
        );

        Observable<Ticket> networkObservable = Observable.defer(() ->
            eventService.getTickets(eventId)
                .doOnNext(tickets -> databaseRepository
                    .deleteAll(Ticket.class)
                    .concatWith(databaseRepository.saveList(Ticket.class, tickets))
                    .subscribe())
                .flatMapIterable(tickets -> tickets));

        return new AbstractObservableBuilder<Ticket>(utilModel)
            .reload(reload)
            .withDiskObservable(diskObservable)
            .withNetworkObservable(networkObservable)
            .build();
    }

    @NonNull
    @Override
    public Completable deleteTicket(long id) {
        if (!utilModel.isConnected()) {
            return Completable.error(new Throwable(Constants.NO_NETWORK));
        }

        return eventService.deleteTicket(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    @Override
    public Observable<TypeQuantity> getTicketsQuantity(long eventId) {
        return new QueryHelper<Ticket>()
            .select(Ticket_Table.type)
            .sum(Ticket_Table.quantity, "quantity")
            .from(Ticket.class)
            .equiJoin(Event.class, Ticket_Table.event_id, Event_Table.id)
            .where(Ticket_Table.event_id.withTable().eq(eventId))
            .group(Ticket_Table.type)
            .toCustomObservable(TypeQuantity.class)
            .subscribeOn(Schedulers.io());
    }

    @NonNull
    @Override
    public Observable<TypeQuantity> getSoldTicketsQuantity(long eventId) {
        return new QueryHelper<Ticket>()
            .select(Ticket_Table.type)
            .method(Method.count(), "quantity")
            .from(Ticket.class)
            .equiJoin(Attendee.class, Attendee_Table.ticket_id, Ticket_Table.id)
            .equiJoin(Event.class, Attendee_Table.event_id, Event_Table.id)
            .where(Ticket_Table.event_id.withTable().eq(eventId))
            .group(Ticket_Table.type)
            .toCustomObservable(TypeQuantity.class)
            .subscribeOn(Schedulers.io());
    }

    @NonNull
    @Override
    public Maybe<Float> getTotalSale(long eventId) {
        return RXSQLite.rx(
            new QueryHelper<Ticket>()
                .sum(Ticket_Table.price, "price")
                .from(Ticket.class)
                .equiJoin(Attendee.class, Attendee_Table.ticket_id, Ticket_Table.id)
                .equiJoin(Event.class, Ticket_Table.event_id, Event_Table.id)
                .where(Ticket_Table.event_id.withTable().eq(eventId))
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
