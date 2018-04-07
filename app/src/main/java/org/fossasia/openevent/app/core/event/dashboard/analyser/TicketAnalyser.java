package org.fossasia.openevent.app.core.event.dashboard.analyser;

import android.support.annotation.NonNull;

import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.attendee.Attendee;
import org.fossasia.openevent.app.data.attendee.AttendeeRepository;
import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.data.ticket.TicketRepository;
import org.fossasia.openevent.app.data.ticket.TypeQuantity;

import java.util.List;

import javax.inject.Inject;

public class TicketAnalyser {
    public static final String TICKET_FREE = "free";
    public static final String TICKET_PAID = "paid";
    public static final String TICKET_DONATION = "donation";
    private final TicketRepository ticketRepository;
    private final AttendeeRepository attendeeRepository;

    @Inject
    public TicketAnalyser(TicketRepository ticketRepository, AttendeeRepository attendeeRepository) {
        this.ticketRepository = ticketRepository;
        this.attendeeRepository = attendeeRepository;
    }

    public void analyseTotalTickets(@NonNull Event event) {
        ticketRepository.getTicketsQuantity(event.getId())
            .doOnNext(typeQuantity -> {
                switch (typeQuantity.getType()) {
                    case "free":
                        event.analytics.freeTickets.set(typeQuantity.getQuantity());
                        break;
                    case "paid":
                        event.analytics.paidTickets.set(typeQuantity.getQuantity());
                        break;
                    case "donation":
                        event.analytics.donationTickets.set(typeQuantity.getQuantity());
                        break;
                    default:
                        // Nothing
                }
            })
            .map(TypeQuantity::getQuantity)
            .reduce((one, two) -> one + two)
            .subscribe(event.analytics.totalTickets::set, Logger::logError);
    }

    public void analyseSoldTickets(@NonNull Event event, @NonNull List<Attendee> attendees) {
        event.analytics.totalAttendees.set(attendees.size());

        ticketRepository.getSoldTicketsQuantity(event.getId())
            .doOnNext(typeQuantity -> {
                switch (typeQuantity.getType()) {
                    case "free":
                        event.analytics.soldFreeTickets.set(typeQuantity.getQuantity());
                        break;
                    case "paid":
                        event.analytics.soldPaidTickets.set(typeQuantity.getQuantity());
                        break;
                    case "donation":
                        event.analytics.soldDonationTickets.set(typeQuantity.getQuantity());
                        break;
                    default:
                        // Nothing
                }
            })
            .map(TypeQuantity::getQuantity)
            .reduce((one, two) -> one + two)
            .subscribe(Logger::logSuccess, Logger::logError);

        ticketRepository.getTotalSale(event.getId())
            .subscribe(event.analytics.totalSale::set, Logger::logError);

        attendeeRepository.getCheckedInAttendees(event.getId())
            .subscribe(event.analytics.checkedIn::set, Logger::logError);
    }

}
