package org.fossasia.openevent.app.data.feedback;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface FeedbackApi {

    @GET("events/{id}/feedbacks?include=event,user&fields[event]=id&page[size]=0")
    Observable<List<Feedback>> getFeedbacks(@Path("id") long id);
}
