package org.fossasia.openevent.app.common.data.network;

import org.fossasia.openevent.app.common.data.models.Attendee;
import org.fossasia.openevent.app.common.data.models.Event;
import org.fossasia.openevent.app.common.data.models.Ticket;
import org.fossasia.openevent.app.common.data.models.User;
import org.fossasia.openevent.app.common.data.models.dto.Login;
import org.fossasia.openevent.app.common.data.models.dto.LoginResponse;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface EventService {

    @POST("../auth/session")
    Observable<LoginResponse> login(@Body Login login);

    @GET("users/{id}/events")
    Observable<List<Event>> getEvents(@Path("id") long id);

    @GET("users/{id}")
    Observable<User> getUser(@Path("id") long id);

    @GET("events/{id}?include=tickets")
    Observable<Event> getEvent(@Path("id") long id);

    @POST("events/{id}/tickets")
    Observable<Ticket> postTicket(@Path("id") long id, @Body Ticket ticket);

    @GET("events/{id}/tickets?include=event&fields[event]=id")
    Observable<List<Ticket>> getTickets(@Path("id") long id);

    @GET("events/{id}/attendees?include=ticket,event&fields[event]=id")
    Observable<List<Attendee>> getAttendees(@Path("id") long id);

    @PATCH("attendees/{attendee_id}?include=ticket,event&fields[event]=id")
    Observable<Attendee> patchAttendee(@Path("attendee_id") long attendeeId, @Body Attendee attendee);

}
