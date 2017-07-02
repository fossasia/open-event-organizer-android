package org.fossasia.openevent.app.event.detail;

import android.support.annotation.VisibleForTesting;

import org.fossasia.openevent.app.data.contract.IEventRepository;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.event.detail.contract.IEventDetailPresenter;
import org.fossasia.openevent.app.event.detail.contract.IEventDetailView;

import javax.inject.Inject;

public class EventDetailPresenter implements IEventDetailPresenter {

    private long eventId;
    private Event event;
    private IEventDetailView eventDetailView;
    private IEventRepository eventRepository;
    private TicketAnalyser ticketAnalyser;

    @Inject
    public EventDetailPresenter(IEventRepository eventRepository, TicketAnalyser ticketAnalyser) {
        this.eventRepository = eventRepository;
        this.ticketAnalyser = ticketAnalyser;
    }

    @Override
    public void attach(IEventDetailView eventDetailView, long initialEventId) {
        this.eventDetailView = eventDetailView;
        this.eventId = initialEventId;
    }

    @Override
    public void start() {
        loadDetails(false);
    }

    @Override
    public void detach() {
        eventDetailView = null;
    }

    @Override
    public void loadDetails(boolean forceReload) {
        if (eventDetailView == null)
            return;

        eventDetailView.showProgressBar(true);

        eventRepository
            .getEvent(eventId, forceReload)
            .doOnComplete(() ->
                loadAttendees(eventId, forceReload))
            .subscribe(this::processEventAndDisplay,
                throwable -> {
                    if(eventDetailView == null)
                        return;
                    eventDetailView.showError(throwable.getMessage());
                    hideProgress(forceReload);
            });
    }

    private void processEventAndDisplay(Event event) {
        if(eventDetailView == null)
            return;

        this.event = event;
        eventDetailView.showEvent(event);
        // TODO: Add views for date of event and format it here

        ticketAnalyser.analyseTotalTickets(event);
    }

    private void loadAttendees(long eventId, boolean forceReload) {
        if(eventDetailView == null)
            return;

        eventRepository
            .getAttendees(eventId, forceReload)
            .toList()
            .subscribe(
                attendees -> {
                    if(eventDetailView == null)
                        return;
                    ticketAnalyser.analyseSoldTickets(event, attendees);
                    hideProgress(forceReload);
                },
                throwable -> {
                    if(eventDetailView == null)
                        return;
                    eventDetailView.showError(throwable.getMessage());
                    hideProgress(forceReload);
                });
    }

    private void hideProgress(boolean forceReload) {
        eventDetailView.showProgressBar(false);

        if (forceReload)
            eventDetailView.onRefreshComplete();
    }

    @VisibleForTesting
    public IEventDetailView getView() {
        return eventDetailView;
    }

    @VisibleForTesting
    public Event getEvent() {
        return event;
    }
}
