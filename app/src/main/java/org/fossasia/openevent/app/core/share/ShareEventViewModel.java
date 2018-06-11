package org.fossasia.openevent.app.core.share;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.data.event.EventRepository;
import org.fossasia.openevent.app.utils.DateUtils;
import org.fossasia.openevent.app.utils.ErrorUtils;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class ShareEventViewModel extends ViewModel {

    private final EventRepository eventRepository;
    private Event event = new Event();

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private MutableLiveData<Event> eventLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> progress = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();

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
                throwable -> error.setValue(ErrorUtils.getMessage(throwable))));

        return eventLiveData;
    }

    public String getShareableInformation() {
        String doubleLineBreak = "\n\n";
        StringBuilder data = new StringBuilder(20);
        data.append(event.getName())
            .append(doubleLineBreak)
            .append("Starts: ").append(DateUtils.formatDateWithDefault(DateUtils.FORMAT_DAY_COMPLETE, event.getStartsAt()))
            .append(doubleLineBreak)
            .append("Ends at: ").append(DateUtils.formatDateWithDefault(DateUtils.FORMAT_DAY_COMPLETE, event.getEndsAt()));

        if (event.getExternalEventUrl() != null) {
            data.append(doubleLineBreak).append("Url: ").append(event.getExternalEventUrl());
        }

        return data.toString();
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
