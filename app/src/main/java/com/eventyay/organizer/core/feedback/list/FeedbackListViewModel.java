package com.eventyay.organizer.core.feedback.list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.eventyay.organizer.common.ContextManager;
import androidx.lifecycle.MutableLiveData;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.feedback.Feedback;
import com.eventyay.organizer.data.feedback.FeedbackRepository;
import com.eventyay.organizer.utils.ErrorUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

import static com.eventyay.organizer.common.rx.ViewTransformers.dispose;
import static com.eventyay.organizer.common.rx.ViewTransformers.emptiable;
import static com.eventyay.organizer.common.rx.ViewTransformers.progressiveErroneousRefresh;

public class FeedbackListViewModel extends ViewModel {

    private final List<Feedback> feedbacks = new ArrayList<>();
    private final FeedbackRepository feedbackRepository;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final MutableLiveData<Boolean> progress = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<String> success = new MutableLiveData<>();
    private final MutableLiveData<List<Feedback>> feedbacksLiveData = new MutableLiveData<>();

    private long eventId;

    @Inject
    public FeedbackListViewModel(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    public LiveData<Boolean> getProgress() {
        return progress;
    }

    public LiveData<String> getSuccess() {
        return success;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<List<Feedback>> getFeedbacksLiveData() {
        return feedbacksLiveData;
    }

    public void loadFeedbacks(boolean forceReload) {

        eventId = ContextManager.getSelectedEvent().getId();

        compositeDisposable.add(
            getFeedbackSource(forceReload)
                .doOnSubscribe(disposable -> progress.setValue(true))
                .doFinally(() -> progress.setValue(false))
                .toList()
                .subscribe(feedbacks -> {
                    feedbacksLiveData.setValue(feedbacks);
                }, throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
    }

    private Observable<Feedback> getFeedbackSource(boolean forceReload) {
        if (!forceReload && !feedbacks.isEmpty()) {
            return Observable.fromIterable(feedbacks);
        } else {
            return feedbackRepository.getFeedbacks(eventId, forceReload);
        }
    }

    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }
}
