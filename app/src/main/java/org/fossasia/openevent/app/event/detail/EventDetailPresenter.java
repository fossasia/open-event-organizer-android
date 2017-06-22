package org.fossasia.openevent.app.event.detail;

import org.fossasia.openevent.app.data.contract.IEventRepository;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.models.Ticket;
import org.fossasia.openevent.app.event.detail.contract.IEventDetailPresenter;
import org.fossasia.openevent.app.event.detail.contract.IEventDetailView;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class EventDetailPresenter implements IEventDetailPresenter {

    private long initialEventId;
    private Event event;
    private IEventDetailView eventDetailView;
    private IEventRepository eventRepository;

    private long totalAttendees;
    private long checkedInAttendees;
    private float totalSales;

    /**
     * progress is parameter to check if complete data is loaded.
     * we have two async processes loadAttendees and loadTickets.
     * on completion of each one progress will be incremented
     * hence progressbar will be hidden when progress is greater than equal to 2
     */
    private int progress = 0;

    @Inject
    public EventDetailPresenter(IEventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public void attach(IEventDetailView eventDetailView, long initialEventId) {
        this.eventDetailView = eventDetailView;
        this.initialEventId = initialEventId;
    }

    @Override
    public void start(boolean forceStart) {
        if (forceStart)
            progress = 0;
        if (progress == 0) {
            loadAttendees(initialEventId, false);
            loadTickets(initialEventId, false);
        } else {
            eventDetailView.showEvent(event);
        }
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
                    hideProgressbar();
                });
    }

    private void processEventAndDisplay(Event event) {
        if(eventDetailView == null)
            return;

        this.event = event;

        showEventInfo(event);

        List<Ticket> tickets = event.getTickets();

        long totalTickets = 0;
        if(tickets != null) {
            for (Ticket thisTicket : tickets)
                totalTickets += thisTicket.getQuantity();
        }

        event.totalTickets.set(totalTickets);
        event.totalAttendees.set(totalAttendees);
        event.checkedIn.set(checkedInAttendees);
        event.totalSale.set(totalSales);

        hideProgressbar();
    }

    private void showEventInfo(Event event) {
        if(eventDetailView == null)
            return;

        eventDetailView.showEvent(event);

        String[] startDate = event.getStartTime().split("T");
        String[] endDate = event.getEndTime().split("T");

        event.startDate.set(startDate[0]);
        event.endDate.set(endDate[0]);
        event.eventStartTime.set(endDate[1]);
    }

    @Override
    public void loadAttendees(long eventId, boolean forceReload) {
        if(eventDetailView == null)
            return;

        eventDetailView.showProgressBar(true);

        eventRepository
            .getAttendees(eventId, forceReload)
            .toList()
            .subscribe(this::processAttendeesAndDisplay,
                throwable -> {
                    if(eventDetailView == null)
                        return;
                    eventDetailView.showEventLoadError(throwable.getMessage());

                    hideProgressbar();
                });
    }

    private void processAttendeesAndDisplay(List<Attendee> attendees) {
        if(eventDetailView == null)
            return;

        totalAttendees = attendees.size();

        if(event != null)
            event.totalAttendees.set(totalAttendees);

        Observable.fromIterable(attendees)
            .filter(attendee -> attendee.getTicket() != null && attendee.getTicket().getPrice() != 0)
            .map(attendee -> attendee.getTicket().getPrice())
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                price -> totalSales += price,
                Timber::e,
                () -> {
                    if(event != null)
                        event.totalSale.set(totalSales);
                });

        Observable.fromIterable(attendees)
            .filter(Attendee::isCheckedIn)
            .toList()
            .map(List::size)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(checkedIn -> {
                if (eventDetailView == null)
                    return;

                checkedInAttendees = checkedIn;

                if(event != null)
                    event.checkedIn.set(checkedInAttendees);

                hideProgressbar();
            });
    }

    /**
     * checks if complete data is loaded
     * and hides progressbar accordingly
     */
    private boolean hideProgressbar() {
        progress ++;
        if (progress >= 2) {
            eventDetailView.showProgressBar(false);
            return true;
        }
        return false;
    }

    public int getProgress() {
        return progress;
    }

    public IEventDetailView getView() {
        return eventDetailView;
    }

    public Event getEvent() {
        return event;
    }
}
