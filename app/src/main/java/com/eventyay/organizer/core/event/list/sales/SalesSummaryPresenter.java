package com.eventyay.organizer.core.event.list.sales;

import com.eventyay.organizer.common.mvp.presenter.AbstractDetailPresenter;
import com.eventyay.organizer.data.attendee.Attendee;

import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.core.event.dashboard.analyser.TicketAnalyser;
import com.eventyay.organizer.data.attendee.AttendeeRepository;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.event.EventRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

import static com.eventyay.organizer.common.rx.ViewTransformers.dispose;
import static com.eventyay.organizer.common.rx.ViewTransformers.progressiveErroneous;
import static com.eventyay.organizer.common.rx.ViewTransformers.result;


public class SalesSummaryPresenter extends AbstractDetailPresenter<Long, SalesSummaryView> {


    private Event event;
    private List<Attendee> attendees;
    private final EventRepository eventRepository;
    private final AttendeeRepository attendeeRepository;
    private final TicketAnalyser ticketAnalyser;

    @Inject
    public SalesSummaryPresenter(EventRepository eventRepository, AttendeeRepository attendeeRepository,
                                   TicketAnalyser ticketAnalyser) {
        this.eventRepository = eventRepository;
        this.ticketAnalyser = ticketAnalyser;
        this.attendeeRepository = attendeeRepository;
    }

    @Override
    public void start() {
        loadDetails(false);
    }

    public void loadDetails(boolean forceReload) {
        if (getView() == null)
            return;

        getEventSource(forceReload)
            .compose(dispose(getDisposable()))
            .compose(result(getView()))
            .flatMap(loadedEvent -> {
                this.event = loadedEvent;
                ticketAnalyser.analyseTotalTickets(event);
                return getAttendeeSource(forceReload);
            })
            .compose(progressiveErroneous(getView()))
            .toList()
            .subscribe(attendees -> {
                this.attendees = attendees;
                ticketAnalyser.analyseSoldTickets(event, attendees);
            }, Logger::logError);

    }

    private Observable<Event> getEventSource(boolean forceReload) {
        if (!forceReload && event != null && isRotated())
            return Observable.just(event);
        else
            return eventRepository.getEvent(getId(), forceReload);
    }

    private Observable<Attendee> getAttendeeSource(boolean forceReload) {
        if (!forceReload && attendees != null && isRotated())
            return Observable.fromIterable(attendees);
        else
            return attendeeRepository.getAttendees(getId(), forceReload);
    }

}
