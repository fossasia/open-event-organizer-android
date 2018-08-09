package com.eventyay.organizer.data.speaker;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface SpeakerApi {

    @GET("events/{id}/speakers?include=event&fields[event]=id&page[size]=0")
    Observable<List<Speaker>> getSpeakers(@Path("id") long id);

    @GET("speakers/{speaker_id}")
    Observable<Speaker> getSpeaker(@Path("speaker_id") long id);
}
