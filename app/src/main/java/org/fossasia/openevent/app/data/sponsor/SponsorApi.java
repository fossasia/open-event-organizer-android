package org.fossasia.openevent.app.data.sponsor;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface SponsorApi {

    @GET("events/{id}/sponsors?include=event&fields[event]=id&page[size]=0")
    Observable<List<Sponsor>> getSponsors(@Path("id") long id);

}
