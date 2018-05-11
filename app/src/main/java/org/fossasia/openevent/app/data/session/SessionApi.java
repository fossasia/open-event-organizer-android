package org.fossasia.openevent.app.data.session;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SessionApi {

    @GET("tracks/{id}/sessions?include=track&fields[track]=id&page[size]=0")
    Observable<List<Session>> getSessions(@Path("id") long id);

    @POST("sessions")
    Observable<Session> postSession(@Body Session session);
}
