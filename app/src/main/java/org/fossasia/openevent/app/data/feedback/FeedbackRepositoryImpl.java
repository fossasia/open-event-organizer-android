package org.fossasia.openevent.app.data.feedback;

import org.fossasia.openevent.app.data.RateLimiter;
import org.fossasia.openevent.app.data.Repository;
import org.threeten.bp.Duration;

import javax.inject.Inject;

import io.reactivex.Observable;

public class FeedbackRepositoryImpl implements FeedbackRepository {

    private final FeedbackApi feedbackApi;
    private final Repository repository;
    private final RateLimiter<String> rateLimiter = new RateLimiter<>(Duration.ofMinutes(10));

    @Inject
    public FeedbackRepositoryImpl(FeedbackApi feedbackApi, Repository repository) {
        this.feedbackApi = feedbackApi;
        this.repository = repository;
    }

    @Override
    public Observable<Feedback> getFeedbacks(long eventId, boolean reload) {
        Observable<Feedback> diskObservable = Observable.defer(() ->
            repository.getItems(Feedback.class, Feedback_Table.event_id.eq(eventId))
        );

        Observable<Feedback> networkObservable = Observable.defer(() ->
            feedbackApi.getFeedbacks(eventId)
                .doOnNext(feedbacks -> repository.syncSave(Feedback.class, feedbacks, Feedback::getId, Feedback_Table.id).subscribe())
                .flatMapIterable(feedbacks -> feedbacks));

        return repository.observableOf(Feedback.class)
            .reload(reload)
            .withRateLimiterConfig("Feedbacks", rateLimiter)
            .withDiskObservable(diskObservable)
            .withNetworkObservable(networkObservable)
            .build();
    }
}
