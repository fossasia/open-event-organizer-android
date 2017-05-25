package org.fossasia.openevent.app.event.detail;

import org.fossasia.openevent.app.data.contract.IEventDataRepository;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.models.Ticket;
import org.fossasia.openevent.app.event.detail.contract.IEventDetailPresenter;
import org.fossasia.openevent.app.event.detail.contract.IEventDetailView;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class EventDetailActivityPresenter implements IEventDetailPresenter {

    private Event initialEvent;
    private IEventDetailView eventDetailView;
    private IEventDataRepository eventRepository;

    private long totalTickets, totalAttendees;

    public EventDetailActivityPresenter(Event initialEvent, IEventDetailView eventDetailView, IEventDataRepository eventRepository) {
        this.initialEvent = initialEvent;
        this.eventDetailView = eventDetailView;
        this.eventRepository = eventRepository;
    }

    @Override
    public void attach() {
        showEventInfo(initialEvent);
        loadAttendees(initialEvent.getId(), false);
        loadTickets(initialEvent.getId(), false);
    }

    @Override
    public void detach() {
        eventDetailView = null;
    }

    @Override
    public void loadTickets(long eventId, boolean forceReload) {
        if(eventDetailView == null)
            return;

        eventDetailView.showProgressBar(true);

        eventRepository
            .getEvent(eventId, forceReload)
            .subscribe(this::processEventAndDisplay,
                throwable -> {
                    if(eventDetailView == null)
                        return;
                    eventDetailView.showEventLoadError(throwable.getMessage());
                    eventDetailView.showProgressBar(false);
                });
    }

    private void showEventInfo(Event event) {
        if(eventDetailView == null)
            return;

        eventDetailView.showEventName(event.getName());

        String[] startDate = event.getStartTime().split("T");
        String[] endDate = event.getEndTime().split("T");

        eventDetailView.showDates(startDate[0], endDate[0]);
        eventDetailView.showTime(endDate[1]);
    }

    private void processEventAndDisplay(Event event) {
        if(eventDetailView == null)
            return;

        showEventInfo(event);

        List<Ticket> tickets = event.getTickets();

        totalTickets = 0;
        if(tickets != null) {
            for (Ticket thisTicket : tickets)
                totalTickets += thisTicket.getQuantity();
        }

        eventDetailView.showTicketStats(totalTickets > 0 ? totalAttendees : 0, totalTickets);

        eventDetailView.showProgressBar(false);
    }

    @Override
    public void loadAttendees(long eventId, boolean forceReload) {
        if(eventDetailView == null)
            return;

        eventRepository
            .getAttendees(eventId, forceReload)
            .subscribe(this::processAttendeesAndDisplay,
                throwable -> {
                    if(eventDetailView == null)
                        return;
                    eventDetailView.showEventLoadError(throwable.getMessage());
                });
    }

    private void processAttendeesAndDisplay(List<Attendee> attendees) {
        totalAttendees = attendees.size();

        Observable.fromIterable(attendees)
            .filter(Attendee::isCheckedIn)
            .toList()
            .map(List::size)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(checkedIn -> {
                if (eventDetailView == null)
                    return;

                eventDetailView.showAttendeeStats(checkedIn, totalAttendees);
                eventDetailView.showTicketStats(totalTickets > 0 ? totalAttendees : 0, totalTickets);
            });
    }

    public IEventDetailView getView() {
        return eventDetailView;
    }
}
