package com.eventyay.organizer.core.event.list.sales;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.eventyay.organizer.common.livedata.SingleEventLiveData;
import com.eventyay.organizer.core.event.dashboard.analyser.TicketAnalyser;
import com.eventyay.organizer.data.attendee.Attendee;
import com.eventyay.organizer.data.attendee.AttendeeRepository;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.event.EventRepository;
import com.eventyay.organizer.utils.ErrorUtils;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

public class SalesSummaryViewModel extends ViewModel {

    private Event event;
    private List<Attendee> attendees;
    private final EventRepository eventRepository;
    private final AttendeeRepository attendeeRepository;
    private final TicketAnalyser ticketAnalyser;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final SingleEventLiveData<Boolean> progress = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> error = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> success = new SingleEventLiveData<>();
    private final SingleEventLiveData<Event> eventLiveData = new SingleEventLiveData<>();

    @Inject
    public SalesSummaryViewModel(EventRepository eventRepository, AttendeeRepository attendeeRepository,
                                 TicketAnalyser ticketAnalyser) {
        this.eventRepository = eventRepository;
        this.ticketAnalyser = ticketAnalyser;
        this.attendeeRepository = attendeeRepository;

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

    public LiveData<Event> getEventLiveData() {
        return eventLiveData;
    }

    public void loadDetails(long eventId, boolean forceReload) {

        compositeDisposable.add(
            getEventSource(eventId, forceReload)
                .flatMap(loadedEvent -> {
                    this.event = loadedEvent;
                    ticketAnalyser.analyseTotalTickets(event);
                    return getAttendeeSource(eventId, forceReload);
                })
                .doOnSubscribe(disposable -> progress.setValue(true))
                .doFinally(() -> {
                    success.setValue("Loaded Successfully");
                    progress.setValue(false);
                    eventLiveData.setValue(event);
                })
                .toList()
                .subscribe(attendees -> {
                    success.setValue("Loaded Successfully");
                    this.attendees = attendees;
                    ticketAnalyser.analyseSoldTickets(event, attendees);
                }, throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));

    }

    private Observable<Event> getEventSource(long eventId, boolean forceReload) {
        if (!forceReload && event != null)
            return Observable.just(event);
        else
            return eventRepository.getEvent(eventId, forceReload);
    }

    private Observable<Attendee> getAttendeeSource(long eventId, boolean forceReload) {
        if (!forceReload && attendees != null)
            return Observable.fromIterable(attendees);
        else
            return attendeeRepository.getAttendees(eventId, forceReload);
    }
}
