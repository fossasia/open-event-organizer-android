package com.eventyay.organizer.core.share;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.event.EventRepository;
import com.eventyay.organizer.utils.ErrorUtils;
import com.eventyay.organizer.utils.Utils;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class ShareEventViewModel extends ViewModel {

    private final EventRepository eventRepository;
    private Event event = new Event();

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final MutableLiveData<Event> eventLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> progress = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    @Inject
    public ShareEventViewModel(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    protected LiveData<Event> getEvent(long eventId, boolean reload) {
        if (eventLiveData.getValue() != null && !reload)
            return eventLiveData;

        compositeDisposable.add(eventRepository.getEvent(eventId, reload)
            .doOnSubscribe(disposable -> progress.setValue(true))
            .doFinally(() -> progress.setValue(false))
            .subscribe(event -> {
                  this.event = event;
                  eventLiveData.setValue(event);
                },
                throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));

        return eventLiveData;
    }

    public String getShareableInformation() {
        return Utils.getShareableInformation(event);
    }

    public String getEmailSubject() {
        return "Check Out " + event.getName();
    }

    public String getShareableUrl() {
        if (event.getExternalEventUrl() == null) {
            return null;
        } else {
            return event.getExternalEventUrl();
        }
    }

    protected LiveData<Boolean> getProgress() {
        return progress;
    }

    protected LiveData<String> getError() {
        return error;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
