package org.fossasia.openevent.app.event.detail;

import android.support.annotation.NonNull;

import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.models.Ticket;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;
import timber.log.Timber;

public class TicketAnalyser {
    public static final String TICKET_FREE = "free";
    public static final String TICKET_PAID = "paid";
    public static final String TICKET_DONATION = "donation";

    @Inject
    public TicketAnalyser() {}

    public void analyseTotalTickets(@NonNull Event event) {
        if (event.getTickets() == null)
            return;

        ReplaySubject<Ticket> free = ReplaySubject.create();
        ReplaySubject<Ticket> paid = ReplaySubject.create();
        ReplaySubject<Ticket> donation = ReplaySubject.create();

        Observable<Ticket> total = splitByType(
            Observable.fromIterable(event.getTickets()),
            free, paid, donation
        );

        getTicketQuantity(free).subscribe(event.freeTickets::set);
        getTicketQuantity(paid).subscribe(event.paidTickets::set);
        getTicketQuantity(donation).subscribe(event.donationTickets::set);
        getTicketQuantity(total).subscribe(event.totalTickets::set, Timber::e);
    }

    public void analyseSoldTickets(@NonNull Event event, @NonNull List<Attendee> attendees) {
        event.totalAttendees.set(attendees.size());

        Observable<Attendee> attendeeObservable = Observable.fromIterable(attendees);

        getCount(attendeeObservable
            .filter(Attendee::isCheckedIn)
        ).subscribe(event.checkedIn::set);

        ReplaySubject<Ticket> free = ReplaySubject.create();
        ReplaySubject<Ticket> paid = ReplaySubject.create();
        ReplaySubject<Ticket> donation = ReplaySubject.create();

        getTicketPrice(
            splitByType(
                attendeeObservable
                    .map(Attendee::getTicket),
                free, paid, donation
            )
        ).subscribe(event.totalSale::set, Timber::e);

        getCount(free).subscribe(event.soldFreeTickets::set);
        getCount(paid).subscribe(event.soldPaidTickets::set);
        getCount(donation).subscribe(event.soldDonationTickets::set);
    }

    private static <T> Single<T> collect(Observable<T> observable, BiFunction<T, T, T> biFunction) {
        return observable.reduce(biFunction)
            .toSingle()
            .subscribeOn(Schedulers.computation());
    }

    private static Single<Float> getTicketPrice(Observable<Ticket> observable) {
        return collect(
            observable.map(Ticket::getPrice),
            (collected, next) -> collected + next
        );
    }

    private static Single<Long> getTicketQuantity(Observable<Ticket> observable) {
        return collect(
            observable.map(Ticket::getQuantity),
            (collected, next) -> collected + next
        );
    }

    private static Single<Long> getCount(Observable<?> observable) {
        return observable.count()
            .subscribeOn(Schedulers.computation());
    }

    private static void completeIfNotEmpty(Subject<?> subject) {
        subject.isEmpty()
            .filter(empty -> !empty)
            .subscribe(empty -> subject.onComplete());
    }

    private static void completeIfNotEmpty(Subject<?>... subjects) {
        for (Subject<?> subject : subjects)
            completeIfNotEmpty(subject);
    }

    private static Observable<Ticket> splitByType(Observable<Ticket> ticketObservable, Subject<Ticket> free, Subject<Ticket> paid, Subject<Ticket> donation) {
        return ticketObservable
            .doOnNext(ticket -> {
                switch (ticket.getType()) {
                    case TICKET_FREE:
                        free.onNext(ticket);
                        break;
                    case TICKET_PAID:
                        paid.onNext(ticket);
                        break;
                    case TICKET_DONATION:
                        donation.onNext(ticket);
                        break;
                    default:
                        // Pass
                }
            }).doOnComplete(() -> completeIfNotEmpty(free, paid, donation));
    }

}
