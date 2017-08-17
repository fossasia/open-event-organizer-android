package org.fossasia.openevent.app.common.data.network;

import org.fossasia.openevent.app.common.data.models.Attendee;
import org.fossasia.openevent.app.common.data.models.Event;
import org.fossasia.openevent.app.common.data.models.Ticket;
import org.fossasia.openevent.app.common.data.models.User;
import org.fossasia.openevent.app.common.data.models.dto.Login;
import org.fossasia.openevent.app.common.data.models.dto.LoginResponse;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface EventService {

    @POST("users")
    Observable<User> signUp(@Body User user);

    @POST("../auth/session")
    Observable<LoginResponse> login(@Body Login login);

    @GET("users/{id}/events?page[size]=0")
    Observable<List<Event>> getEvents(@Path("id") long id);

    @GET("users/{id}")
    Observable<User> getUser(@Path("id") long id);

    @GET("events/{id}?include=tickets")
    Observable<Event> getEvent(@Path("id") long id);

    @POST("tickets")
    Observable<Ticket> postTicket(@Body Ticket ticket);

    @GET("events/{id}/tickets?include=event&fields[event]=id&page[size]=0")
    Observable<List<Ticket>> getTickets(@Path("id") long id);

    @GET("tickets/{id}")
    Observable<Ticket> getTicket(@Path("id") long id);

    @DELETE("tickets/{id}")
    Completable deleteTicket(@Path("id") long id);

    @GET("events/{id}/attendees?include=order,ticket,event&fields[event]=id&fields[ticket]=id&page[size]=0")
    Observable<List<Attendee>> getAttendees(@Path("id") long id);

    @PATCH("attendees/{attendee_id}?include=ticket,event,order&fields[event]=id&fields[ticket]=id")
    Observable<Attendee> patchAttendee(@Path("attendee_id") long attendeeId, @Body Attendee attendee);

}
