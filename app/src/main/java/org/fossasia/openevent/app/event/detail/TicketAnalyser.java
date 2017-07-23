package org.fossasia.openevent.app.event.detail;

import android.support.annotation.NonNull;

import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.models.query.TypeQuantity;
import org.fossasia.openevent.app.data.repository.EventRepository;

import java.util.List;

import javax.inject.Inject;

public class TicketAnalyser {
    public static final String TICKET_FREE = "free";
    public static final String TICKET_PAID = "paid";
    public static final String TICKET_DONATION = "donation";
    private EventRepository eventRepository;

    @Inject
    public TicketAnalyser(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void analyseTotalTickets(@NonNull Event event) {
        if (event.getTickets() == null)
            return;

        eventRepository.getTicketsQuantity(event.getId())
            .doOnNext(typeQuantity -> {
                switch (typeQuantity.getType()) {
                    case "free":
                        event.freeTickets.set(typeQuantity.getQuantity());
                        break;
                    case "paid":
                        event.paidTickets.set(typeQuantity.getQuantity());
                        break;
                    case "donation":
                        event.donationTickets.set(typeQuantity.getQuantity());
                        break;
                    default:
                        // Nothing
                }
            })
            .map(TypeQuantity::getQuantity)
            .reduce((one, two) -> one + two)
            .subscribe(event.totalTickets::set, Logger::logError);
    }

    public void analyseSoldTickets(@NonNull Event event, @NonNull List<Attendee> attendees) {
        event.totalAttendees.set(attendees.size());

        eventRepository.getSoldTicketsQuantity(event.getId())
            .doOnNext(typeQuantity -> {
                switch (typeQuantity.getType()) {
                    case "free":
                        event.soldFreeTickets.set(typeQuantity.getQuantity());
                        break;
                    case "paid":
                        event.soldPaidTickets.set(typeQuantity.getQuantity());
                        break;
                    case "donation":
                        event.soldDonationTickets.set(typeQuantity.getQuantity());
                        break;
                    default:
                        // Nothing
                }
            })
            .map(TypeQuantity::getQuantity)
            .reduce((one, two) -> one + two)
            .subscribe(Logger::logSuccess, Logger::logError);

        eventRepository.getTotalSale(event.getId())
            .subscribe(event.totalSale::set, Logger::logError);

        eventRepository.getCheckedInAttendees(event.getId())
            .subscribe(event.checkedIn::set, Logger::logError);
    }

}
