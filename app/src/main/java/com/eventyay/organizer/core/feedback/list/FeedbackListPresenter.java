package com.eventyay.organizer.core.feedback.list;

import com.eventyay.organizer.common.mvp.presenter.AbstractDetailPresenter;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.feedback.Feedback;
import com.eventyay.organizer.data.feedback.FeedbackRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

import static com.eventyay.organizer.common.rx.ViewTransformers.dispose;
import static com.eventyay.organizer.common.rx.ViewTransformers.emptiable;
import static com.eventyay.organizer.common.rx.ViewTransformers.progressiveErroneousRefresh;

public class FeedbackListPresenter extends AbstractDetailPresenter<Long, FeedbackListView> {

    private final List<Feedback> feedbacks = new ArrayList<>();
    private final FeedbackRepository feedbackRepository;

    @Inject
    public FeedbackListPresenter(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @Override
    public void start() {
        loadFeedbacks(false);
    }

    public void loadFeedbacks(boolean forceReload) {
        if (getView() == null)
            return;

        getFeedbackSource(forceReload)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneousRefresh(getView(), forceReload))
            .toList()
            .compose(emptiable(getView(), feedbacks))
            .subscribe(Logger::logSuccess, Logger::logError);
    }

    private Observable<Feedback> getFeedbackSource(boolean forceReload) {
        if (!forceReload && !feedbacks.isEmpty() && isRotated()) {
            return Observable.fromIterable(feedbacks);
        } else {
            return feedbackRepository.getFeedbacks(getId(), forceReload);
        }
    }

    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }
}
