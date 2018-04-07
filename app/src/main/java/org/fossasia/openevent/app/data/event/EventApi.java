package org.fossasia.openevent.app.data.event;

import org.fossasia.openevent.app.data.auth.model.User;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface EventApi {

    @GET("users/{id}")
    Observable<User> getUser(@Path("id") long id);

    @GET("users/{id}/events?page[size]=0")
    Observable<List<Event>> getEvents(@Path("id") long id);

    @GET("events/{id}?include=tickets")
    Observable<Event> getEvent(@Path("id") long id);

    @PATCH("events/{id}")
    Observable<Event> patchEvent(@Path("id") long id, @Body Event event);

    @POST("events")
    Observable<Event> postEvent(@Body Event event);

    @GET("events/{id}/general-statistics")
    Observable<EventStatistics> getEventStatistics(@Path("id") long id);
}
