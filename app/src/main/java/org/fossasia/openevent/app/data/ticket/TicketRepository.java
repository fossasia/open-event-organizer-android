package org.fossasia.openevent.app.data.ticket;

import androidx.annotation.NonNull;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;

public interface TicketRepository {

    @NonNull
    Observable<Ticket> createTicket(Ticket ticket);

    @NonNull
    Observable<Ticket> getTicket(long ticketId, boolean reload);

    @NonNull
    Observable<Ticket> getTickets(long eventId, boolean reload);

    @NonNull
    Completable deleteTicket(long id);

    @NonNull
    Observable<TypeQuantity> getTicketsQuantity(long eventId);

    @NonNull
    Observable<TypeQuantity> getSoldTicketsQuantity(long eventId);

    @NonNull
    Maybe<Float> getTotalSale(long eventId);

}
