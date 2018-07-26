package com.eventyay.organizer.data.sponsor;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SponsorApi {

    @GET("events/{id}/sponsors?include=event&fields[event]=id&page[size]=0")
    Observable<List<Sponsor>> getSponsors(@Path("id") long id);

    @POST("sponsors")
    Observable<Sponsor> postSponsor(@Body Sponsor sponsor);

    @GET("sponsors/{sponsor_id}")
    Observable<Sponsor> getSponsor(@Path("sponsor_id") long id);

    @PATCH("sponsors/{sponsor_id}")
    Observable<Sponsor> updateSponsor(@Path("sponsor_id") long id, @Body Sponsor sponsor);

    @DELETE("sponsors/{sponsor_id}")
    Completable deleteSponsor(@Path("sponsor_id") long id);
}
