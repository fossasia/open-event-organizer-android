package com.eventyay.organizer.data.copyright;

import io.reactivex.Completable;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CopyrightApi {

    @POST("event-copyrights")
    Observable<Copyright> postCopyright(@Body Copyright copyright);

    @GET("events/{eventId}/event-copyright?include=event&fields[event]=id&page[size]=0")
    Observable<Copyright> getCopyright(@Path("eventId") long eventId);

    @PATCH("event-copyrights/{id}")
    Observable<Copyright> patchCopyright(@Path("id") long id, @Body Copyright copyright);

    @DELETE("event-copyrights/{id}")
    Completable deleteCopyright(@Path("id") long id);
}
