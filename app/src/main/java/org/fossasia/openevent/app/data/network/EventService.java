package org.fossasia.openevent.app.data.network;


import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.models.Login;
import org.fossasia.openevent.app.data.models.LoginResponse;
import org.fossasia.openevent.app.data.models.User;

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

    @GET("events/{id}/attendees?include=ticket,event&fields[event]=id")
    Observable<List<Attendee>> getAttendees(@Path("id") long id);

    @PATCH("attendees/{attendee_id}?include=ticket,event&fields[event]=id")
    Observable<Attendee> patchAttendee(@Path("attendee_id") long attendeeId, @Body Attendee attendee);

}
