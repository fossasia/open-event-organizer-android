package org.fossasia.openevent.app.event.detail;

import android.databinding.ObservableLong;
import android.support.annotation.NonNull;

import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.models.Ticket;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class TicketAnalyser {
    public static final String TICKET_FREE = "free";
    public static final String TICKET_PAID = "paid";
    public static final String TICKET_DONATION = "donation";

    public static void analyseTotalTickets(@NonNull Event event) {
        subscribeSingle(analyseTotalTicketsByType(event, TICKET_FREE), event.freeTickets);
        subscribeSingle(analyseTotalTicketsByType(event, TICKET_PAID), event.paidTickets);
        subscribeSingle(analyseTotalTicketsByType(event, TICKET_DONATION), event.donationTickets);
    }

    public static void analyseSoldTickets(@NonNull Event event, @NonNull List<Attendee> attendees) {
        subscribeSingle(analyseSoldTicketsByType(attendees, TICKET_FREE), event.soldFreeTickets);
        subscribeSingle(analyseSoldTicketsByType(attendees, TICKET_PAID), event.soldPaidTickets);
        subscribeSingle(analyseSoldTicketsByType(attendees, TICKET_DONATION), event.soldDonationTickets);
    }

    private static void subscribeSingle(Single<Long> single, ObservableLong observableLong) {
        single.subscribe(
            observableLong::set,
            Timber::e
        );
    }

    private static Single<Long> analyseTotalTicketsByType(@NonNull Event event, @NonNull String type) {
        return Observable.fromIterable(event.getTickets())
                .filter(ticket -> ticket.getType().equals(type))
                .map(Ticket::getQuantity)
                .reduce((collected, next) -> collected + next)
                .toSingle()
                .subscribeOn(Schedulers.computation());
    }

    private static Single<Long> analyseSoldTicketsByType(@NonNull List<Attendee> attendees, @NonNull String type) {
        return Observable.fromIterable(attendees)
                    .map(Attendee::getTicket)
                    .filter(ticket -> ticket.getType().equals(type))
                    .count()
                    .subscribeOn(Schedulers.computation());
    }

}
