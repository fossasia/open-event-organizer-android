package org.fossasia.openevent.app.data.feedback;

import io.reactivex.Observable;

public interface FeedbackRepository {
    Observable<Feedback> getFeedbacks(long id, boolean reload);
}
