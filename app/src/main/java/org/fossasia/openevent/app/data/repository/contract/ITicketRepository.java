package org.fossasia.openevent.app.data.repository.contract;

import android.support.annotation.NonNull;

import org.fossasia.openevent.app.data.models.Ticket;
import org.fossasia.openevent.app.data.models.query.TypeQuantity;

import io.reactivex.Observable;
import io.reactivex.Single;

public interface ITicketRepository {

    @NonNull
    Observable<Ticket> getTickets(long eventId, boolean reload);

    @NonNull
    Observable<TypeQuantity> getTicketsQuantity(long eventId);

    @NonNull
    Observable<TypeQuantity> getSoldTicketsQuantity(long eventId);

    @NonNull
    Single<Float> getTotalSale(long eventId);

}
