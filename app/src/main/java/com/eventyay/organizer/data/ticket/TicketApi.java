package com.eventyay.organizer.data.ticket;

import io.reactivex.Completable;
import io.reactivex.Observable;
import java.util.List;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TicketApi {

    @POST("tickets")
    Observable<Ticket> postTicket(@Body Ticket ticket);

    @GET("events/{id}/tickets?include=event&fields[event]=id&page[size]=0")
    Observable<List<Ticket>> getTickets(@Path("id") long id);

    @GET("orders/{id}/tickets?include=event&fields[event]=id&page[size]=0")
    Observable<List<Ticket>> getTicketsUnderOrder(@Path("id") String id);

    @GET("tickets/{id}")
    Observable<Ticket> getTicket(@Path("id") long id);

    @DELETE("tickets/{id}")
    Completable deleteTicket(@Path("id") long id);

    @PATCH("tickets/{ticket_id}")
    Observable<Ticket> updateTicket(@Path("ticket_id") long id, @Body Ticket ticket);
}
