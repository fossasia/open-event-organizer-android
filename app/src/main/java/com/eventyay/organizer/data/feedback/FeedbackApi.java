package com.eventyay.organizer.data.feedback;

import io.reactivex.Observable;
import java.util.List;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface FeedbackApi {

    @GET("events/{id}/feedbacks?include=event,user&fields[event]=id&page[size]=0")
    Observable<List<Feedback>> getFeedbacks(@Path("id") long id);
}
