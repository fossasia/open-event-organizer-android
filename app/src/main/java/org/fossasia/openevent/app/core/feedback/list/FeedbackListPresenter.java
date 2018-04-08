package org.fossasia.openevent.app.core.feedback.list;

import org.fossasia.openevent.app.common.mvp.presenter.AbstractDetailPresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.feedback.Feedback;
import org.fossasia.openevent.app.data.feedback.FeedbackRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.emptiable;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneousRefresh;

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
