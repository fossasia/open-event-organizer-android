package org.fossasia.openevent.app.core.faq.create;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.app.common.ContextManager;
import org.fossasia.openevent.app.common.livedata.SingleEventLiveData;
import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.data.faq.Faq;
import org.fossasia.openevent.app.data.faq.FaqRepository;
import org.fossasia.openevent.app.utils.ErrorUtils;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class CreateFaqViewModel extends ViewModel {

    private final FaqRepository faqRepository;
    private final Faq faq = new Faq();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final SingleEventLiveData<Boolean> progress = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> error = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> success = new SingleEventLiveData<>();
    private final SingleEventLiveData<Void> dismiss = new SingleEventLiveData<>();

    @Inject
    public CreateFaqViewModel(FaqRepository faqRepository) {
        this.faqRepository = faqRepository;
    }

    public Faq getFaq() {
        return faq;
    }

    public LiveData<Boolean> getProgress() {
        return progress;
    }

    public LiveData<String> getSuccess() {
        return success;
    }

    public LiveData<Void> getDismiss() {
        return dismiss;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void createFaq() {
        long eventId = ContextManager.getSelectedEvent().getId();
        Event event = new Event();
        event.setId(eventId);
        faq.setEvent(event);

        compositeDisposable.add(
            faqRepository
                .createFaq(faq)
                .doOnSubscribe(disposable -> progress.setValue(true))
                .doFinally(() -> progress.setValue(false))
                .subscribe(createdFaq -> {
                    success.setValue("Faq Created");
                    dismiss.call();
                }, throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
    }
}
