package org.fossasia.openevent.app.data.tracks;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TrackApi {

    @GET("events/{id}/tracks?include=event&fields[event]=id&page[size]=0")
    Observable<List<Track>> getTracks(@Path("id") long id);
}
