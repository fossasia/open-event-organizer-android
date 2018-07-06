package org.fossasia.openevent.app.data.ticket;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.rx2.language.RXSQLite;
import com.raizlabs.android.dbflow.sql.language.Method;

import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.data.RateLimiter;
import org.fossasia.openevent.app.data.Repository;
import org.fossasia.openevent.app.data.attendee.Attendee;
import org.fossasia.openevent.app.data.attendee.Attendee_Table;
import org.fossasia.openevent.app.data.db.QueryHelper;
import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.data.event.Event_Table;
import org.threeten.bp.Duration;

import java.util.NoSuchElementException;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TicketRepositoryImpl implements TicketRepository {

    private final TicketApi ticketApi;
    private final Repository repository;
    private final RateLimiter<String> rateLimiter = new RateLimiter<>(Duration.ofMinutes(10));

    @Inject
    public TicketRepositoryImpl(TicketApi ticketApi, Repository repository) {
        this.ticketApi = ticketApi;
        this.repository = repository;
    }

    @NonNull
    @Override
    public Observable<Ticket> createTicket(Ticket ticket) {
        if (!repository.isConnected()) {
            return Observable.error(new Throwable(Constants.NO_NETWORK));
        }

        return ticketApi
            .postTicket(ticket)
            .doOnNext(created -> {
                created.setEvent(ticket.getEvent());
                repository
                    .save(Ticket.class, created)
                    .subscribe();
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    @Override
    public Observable<Ticket> getTicket(long ticketId, boolean reload) {
        Observable<Ticket> diskObservable = Observable.defer(() ->
            repository
                .getItems(Ticket.class, Ticket_Table.id.eq(ticketId)).take(1)
        );

        Observable<Ticket> networkObservable = Observable.defer(() ->
            ticketApi.getTicket(ticketId)
                .doOnNext(ticket -> repository
                    .save(Ticket.class, ticket)
                    .subscribe()));

        return repository
            .observableOf(Ticket.class)
            .reload(reload)
            .withRateLimiterConfig("Ticket", rateLimiter)
            .withDiskObservable(diskObservable)
            .withNetworkObservable(networkObservable)
            .build();
    }

    @NonNull
    @Override
    public Observable<Ticket> getTickets(long eventId, boolean reload) {
        Observable<Ticket> diskObservable = Observable.defer(() ->
            repository.getItems(Ticket.class, Ticket_Table.event_id.eq(eventId))
        );

        Observable<Ticket> networkObservable = Observable.defer(() ->
            ticketApi
                .getTickets(eventId)
                .doOnNext(tickets -> repository.syncSave(Ticket.class, tickets, Ticket::getId, Ticket_Table.id).subscribe()))
                .flatMapIterable(tickets -> tickets);

        return repository.observableOf(Ticket.class)
            .reload(reload)
            .withRateLimiterConfig("Tickets", rateLimiter)
            .withDiskObservable(diskObservable)
            .withNetworkObservable(networkObservable)
            .build();
    }

    @Override
    public Observable<Ticket> getTicketsUnderOrder(String orderIdentifier, long orderId, boolean reload) {
        Observable<Ticket> diskObservable = Observable.defer(() ->
            repository.getItems(Ticket.class, Ticket_Table.order_id.eq(orderId))
        );

        Observable<Ticket> networkObservable = Observable.defer(() ->
            ticketApi.getTicketsUnderOrder(orderIdentifier)
                .doOnNext(tickets -> repository.syncSave(Ticket.class, tickets, Ticket::getId, Ticket_Table.id).subscribe())
                .flatMapIterable(tickets -> tickets));

        return repository.observableOf(Ticket.class)
            .reload(reload)
            .withRateLimiterConfig("TicketsUnderOrder", rateLimiter)
            .withDiskObservable(diskObservable)
            .withNetworkObservable(networkObservable)
            .build();
    }

    @NonNull
    @Override
    public Completable deleteTicket(long id) {
        if (!repository.isConnected()) {
            return Completable.error(new Throwable(Constants.NO_NETWORK));
        }

        return ticketApi.deleteTicket(id)
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
