package org.fossasia.openevent.app.data.network.api;


import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.models.Login;
import org.fossasia.openevent.app.data.models.LoginResponse;
import org.fossasia.openevent.app.data.models.User;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface EventService {

    @POST("login")
    Observable<LoginResponse> login(@Body Login login);

    @GET("users/me/events")
    Observable<List<Event>> getEvents(@Header("Authorization") String authToken);

    @GET("users/me")
    Observable<User> getUser(@Header("Authorization") String authToken);

    @GET("events/{id}?include=tickets")
    Observable<Event> getEvent(@Path("id") long id, @Header("Authorization") String authToken);

    @GET("events/{id}/attendees")
    Observable<List<Attendee>> getAttendees(@Path("id") long id, @Header("Authorization") String authToken);

}
