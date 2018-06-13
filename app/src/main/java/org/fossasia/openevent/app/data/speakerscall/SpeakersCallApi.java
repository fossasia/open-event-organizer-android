package org.fossasia.openevent.app.data.speakerscall;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SpeakersCallApi {

    @GET("events/{id}/speakers-call?include=event&fields[event]=id&page[size]=0")
    Observable<SpeakersCall> getSpeakersCall(@Path("id") long id);

    @POST("speakers-calls")
    Observable<SpeakersCall> postSpeakersCall(@Body SpeakersCall speakersCall);
}
