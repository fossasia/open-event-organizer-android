package org.fossasia.openevent.app.robo.db;

import org.fossasia.openevent.app.data.db.DatabaseRepository;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.models.Ticket;
import org.fossasia.openevent.app.data.models.query.TypeQuantity;
import org.fossasia.openevent.app.data.repository.TicketRepository;
import org.junit.After;
import org.junit.Test;

import java.util.Arrays;

import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TicketTest extends BaseTest {

    private static final String PAID = "paid";
    private static final String FREE = "free";
    private static final String DONATION = "donation";

    private TicketRepository ticketRepository;

    @Override
    public void setUp() {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());

        Event event = new Event();
        event.setId(Long.valueOf(12));

        Ticket ticket1 = Ticket.builder()
            .id(1L)
            .quantity(120L)
            .type(FREE)
            .event(event)
            .build();

        Ticket ticket2 = Ticket.builder()
            .id(2L)
            .quantity(20L)
            .type(PAID)
            .price(25.99f)
            .event(event)
            .build();

        Ticket ticket3 = Ticket.builder()
            .id(3L)
            .quantity(30L)
            .type(DONATION)
            .event(event)
            .build();

        Ticket ticket4 = Ticket.builder()
            .id(4L)
            .quantity(10L)
            .type(DONATION)
            .event(event)
            .build();

        Ticket ticket5 = Ticket.builder()
            .id(5L)
            .quantity(50L)
            .type(PAID)
            .price(99.99f)
            .event(event)
            .build();

        Attendee attendee = Attendee.builder()
            .id(1L)
            .ticket(ticket1)
            .event(event)
            .build();

        Attendee attendee2 = Attendee.builder()
            .id(2L)
            .ticket(ticket1)
            .event(event)
            .build();

        Attendee attendee3 = Attendee.builder()
            .id(3L)
            .ticket(ticket1)
            .event(event)
            .build();

        Attendee attendee4 = Attendee.builder()
            .id(4L)
            .ticket(ticket2)
            .event(event)
            .build();

        Attendee attendee5 = Attendee.builder()
            .id(5L)
            .ticket(ticket2)
            .event(event)
            .build();

        Attendee attendee6 = Attendee.builder()
            .id(6L)
            .ticket(ticket4)
            .event(event)
            .build();

        Attendee attendee7 = Attendee.builder()
            .id(7L)
            .ticket(ticket5)
            .event(event)
            .build();

        event.setTickets(Arrays.asList(ticket1, ticket2, ticket3, ticket4, ticket5));

        DatabaseRepository databaseRepository = new DatabaseRepository();

        databaseRepository.save(Event.class, event).subscribe();
        databaseRepository.saveList(Ticket.class, Arrays.asList(ticket1, ticket2, ticket3, ticket4, ticket5)).subscribe();
        databaseRepository.saveList(Attendee.class, Arrays.asList(attendee, attendee2, attendee3,
            attendee4, attendee5, attendee6, attendee7)).subscribe();

        ticketRepository = new TicketRepository(null, databaseRepository, null);
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void testTicketQuantity() {
        Iterable<TypeQuantity> typeQuantities = ticketRepository.getTicketsQuantity(12).blockingIterable();

        for (TypeQuantity typeQuantity : typeQuantities) {
            switch (typeQuantity.getType()) {
                case DONATION:
                    assertEquals(40, typeQuantity.getQuantity());
                    break;
                case FREE:
                    assertEquals(120, typeQuantity.getQuantity());
                    break;
                case PAID:
                    assertEquals(70, typeQuantity.getQuantity());
                    break;
                default:
                    fail(typeQuantity.getType() + " type should not exist");
                    break;
            }
        }
    }

    @Test
    public void testTotalSale() {
        float sale = ticketRepository.getTotalSale(12).blockingGet();

        assertEquals(151.97, sale, 0.01);
    }

    @Test
    public void testSoldTicketQuantity() {
        Iterable<TypeQuantity> typeQuantities = ticketRepository.getSoldTicketsQuantity(12).blockingIterable();

        for (TypeQuantity typeQuantity : typeQuantities) {
            switch (typeQuantity.getType()) {
                case DONATION:
                    assertEquals(1, typeQuantity.getQuantity());
                    break;
                case FREE:
                    assertEquals(3, typeQuantity.getQuantity());
                    break;
                case PAID:
                    assertEquals(3, typeQuantity.getQuantity());
                    break;
                default:
                    fail(typeQuantity.getType() + " type should not exist");
                    break;
            }
        }
    }

}
