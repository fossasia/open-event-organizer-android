package org.fossasia.openevent.app.module.event.dashboard.analyser;

import android.support.annotation.NonNull;

import org.fossasia.openevent.app.common.app.rx.Logger;
import org.fossasia.openevent.app.common.data.models.Attendee;
import org.fossasia.openevent.app.common.data.models.Event;
import org.fossasia.openevent.app.common.data.models.query.TypeQuantity;
import org.fossasia.openevent.app.common.data.repository.contract.IAttendeeRepository;
import org.fossasia.openevent.app.common.data.repository.contract.ITicketRepository;

import java.util.List;

import javax.inject.Inject;

public class TicketAnalyser {
    public static final String TICKET_FREE = "free";
    public static final String TICKET_PAID = "paid";
    public static final String TICKET_DONATION = "donation";
    private final ITicketRepository ticketRepository;
    private final IAttendeeRepository attendeeRepository;

    @Inject
    public TicketAnalyser(ITicketRepository ticketRepository, IAttendeeRepository attendeeRepository) {
        this.ticketRepository = ticketRepository;
        this.attendeeRepository = attendeeRepository;
    }

    public void analyseTotalTickets(@NonNull Event event) {
        if (event.getTickets() == null)
            return;

        ticketRepository.getTicketsQuantity(event.getId())
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

        ticketRepository.getSoldTicketsQuantity(event.getId())
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

        ticketRepository.getTotalSale(event.getId())
            .subscribe(event.totalSale::set, Logger::logError);

        attendeeRepository.getCheckedInAttendees(event.getId())
            .subscribe(event.checkedIn::set, Logger::logError);
    }

}
