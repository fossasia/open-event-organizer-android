package org.fossasia.openevent.app.presenter.helper;

import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.models.Ticket;
import org.fossasia.openevent.app.event.detail.TicketAnalyser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.List;

import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class TicketAnalyserTest {

    private Event event;
    private List<Attendee> attendees;

    private TicketAnalyser ticketAnalyser;

    @Before
    public void setUp() {
        ticketAnalyser = new TicketAnalyser();

        event = new Event();
        List<Ticket> tickets = Arrays.asList(
            new Ticket(190, TicketAnalyser.TICKET_PAID).price(3.45f),
            new Ticket(50, TicketAnalyser.TICKET_PAID).price(4.56f),
            new Ticket(15, TicketAnalyser.TICKET_PAID).price(1.34f),
            new Ticket(60, TicketAnalyser.TICKET_FREE),
            new Ticket(25, TicketAnalyser.TICKET_FREE),
            new Ticket(55, TicketAnalyser.TICKET_DONATION),
            new Ticket(90, TicketAnalyser.TICKET_DONATION)
        );
        event.setTickets(tickets);
        attendees = Arrays.asList(
            Attendee.withTicket(tickets.get(0)).withCheckedIn(), // PAID
            Attendee.withTicket(tickets.get(1)),                 // PAID
            Attendee.withTicket(tickets.get(3)).withCheckedIn(), // FREE
            Attendee.withTicket(tickets.get(0)).withCheckedIn(), // PAID
            Attendee.withTicket(tickets.get(4)),                 // FREE
            Attendee.withTicket(tickets.get(2)).withCheckedIn(), // PAID
            Attendee.withTicket(tickets.get(5)).withCheckedIn(), // DONATION
            Attendee.withTicket(tickets.get(0)),                 // PAID
            Attendee.withTicket(tickets.get(5)).withCheckedIn(), // DONATION
            Attendee.withTicket(tickets.get(1)),                 // PAID
            Attendee.withTicket(tickets.get(2)).withCheckedIn(), // PAID
            Attendee.withTicket(tickets.get(2)),                 // PAID
            Attendee.withTicket(tickets.get(6)).withCheckedIn(), // DONATION
            Attendee.withTicket(tickets.get(3)).withCheckedIn(), // FREE
            Attendee.withTicket(tickets.get(4)),                 // FREE
            Attendee.withTicket(tickets.get(6)),                 // DONATION
            Attendee.withTicket(tickets.get(3)).withCheckedIn(), // FREE
            Attendee.withTicket(tickets.get(3)).withCheckedIn(), // FREE
            Attendee.withTicket(tickets.get(4)),                 // FREE
            Attendee.withTicket(tickets.get(6)),                 // DONATION
            Attendee.withTicket(tickets.get(1)).withCheckedIn()  // PAID
        );

        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
    }

    @Test
    public void shouldSetTotalPaidTickets() {
        ticketAnalyser.analyseTotalTickets(event);
        assertEquals(255, event.paidTickets.get());
    }

    @Test
    public void shouldSetTotalFreeTickets() {
        ticketAnalyser.analyseTotalTickets(event);
        assertEquals(85, event.freeTickets.get());
    }

    @Test
    public void shouldSetTotalDonationTickets() {
        ticketAnalyser.analyseTotalTickets(event);
        assertEquals(145, event.donationTickets.get());
    }

    @Test
    public void shouldSetTotalTickets() {
        ticketAnalyser.analyseTotalTickets(event);
        assertEquals(485, event.totalTickets.get());
    }

    @Test
    public void shouldSetTotalAttendees() {
        ticketAnalyser.analyseSoldTickets(event, attendees);
        assertEquals(attendees.size(), event.totalAttendees.get());
    }

    @Test
    public void shouldSetCheckedInAttendees() {
        ticketAnalyser.analyseSoldTickets(event, attendees);
        assertEquals(12, event.checkedIn.get());
    }

    @Test
    public void shouldSetTotalSales() {
        ticketAnalyser.analyseSoldTickets(event, attendees);
        assertEquals(28.05, event.totalSale.get(), 0.001);
    }

    @Test
    public void shouldSetSoldPaidTickets() {
        ticketAnalyser.analyseSoldTickets(event, attendees);
        assertEquals(9, event.soldPaidTickets.get());
    }

    @Test
    public void shouldSetSoldFreeTickets() {
        ticketAnalyser.analyseSoldTickets(event, attendees);
        assertEquals(7, event.soldFreeTickets.get());
    }

    @Test
    public void shouldSetSoldDonationTickets() {
        ticketAnalyser.analyseSoldTickets(event, attendees);
        assertEquals(5, event.soldDonationTickets.get());
    }

}
