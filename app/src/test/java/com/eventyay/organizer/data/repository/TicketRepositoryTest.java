package com.eventyay.organizer.data.repository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.eventyay.organizer.common.Constants;
import com.eventyay.organizer.data.AbstractObservable;
import com.eventyay.organizer.data.Repository;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.ticket.Ticket;
import com.eventyay.organizer.data.ticket.TicketApi;
import com.eventyay.organizer.data.ticket.TicketRepositoryImpl;
import com.raizlabs.android.dbflow.sql.language.SQLOperator;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

@SuppressWarnings("PMD.TooManyMethods")
public class TicketRepositoryTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    private TicketRepositoryImpl ticketRepository;
    private static final Ticket TICKET = new Ticket();
    private static final Event EVENT = new Event();
    private static final long ID = 12;

    @Mock private TicketApi ticketApi;
    @Mock private Repository repository;

    static {
        TICKET.setEvent(EVENT);
    }

    @Before
    public void setUp() {
        when(repository.observableOf(Ticket.class))
                .thenReturn(new AbstractObservable.AbstractObservableBuilder<>(repository));
        ticketRepository = new TicketRepositoryImpl(ticketApi, repository);
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(
                schedulerCallable -> Schedulers.trampoline());
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    // Network down tests

    @Test
    public void shouldReturnConnectionErrorOnCreateTicket() {
        when(repository.isConnected()).thenReturn(false);

        Observable<Ticket> ticketObservable = ticketRepository.createTicket(TICKET);

        ticketObservable
                .test()
                .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    @Test
    public void shouldReturnConnectionErrorDeleteTicket() {
        when(repository.isConnected()).thenReturn(false);

        Completable ticketObservable = ticketRepository.deleteTicket(ID);

        ticketObservable
                .test()
                .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    @Test
    public void shouldReturnConnectionErrorOnGetTicketWithReload() {
        when(repository.isConnected()).thenReturn(false);

        Observable<Ticket> ticketObservable = ticketRepository.getTicket(ID, true);

        ticketObservable
                .test()
                .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    @Test
    public void shouldReturnConnectionErrorOnGetTicketWithNoneSaved() {
        when(repository.isConnected()).thenReturn(false);
        when(repository.getItems(eq(Ticket.class), any(SQLOperator.class)))
                .thenReturn(Observable.empty());

        Observable<Ticket> ticketObservable = ticketRepository.getTicket(ID, false);

        ticketObservable
                .test()
                .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    @Test
    public void shouldReturnConnectionErrorOnGetTicketsWithReload() {
        when(repository.isConnected()).thenReturn(false);

        Observable<Ticket> ticketObservable = ticketRepository.getTickets(ID, true);

        ticketObservable
                .test()
                .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    @Test
    public void shouldReturnConnectionErrorOnGetTicketsWithNoneSaved() {
        when(repository.isConnected()).thenReturn(false);
        when(repository.getItems(eq(Ticket.class), any(SQLOperator.class)))
                .thenReturn(Observable.empty());

        Observable<Ticket> ticketObservable = ticketRepository.getTickets(ID, false);

        ticketObservable
                .test()
                .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    // Network up tests

    // Ticket Create Tests

    @Test
    public void shouldCallCreateTicketService() {
        when(repository.isConnected()).thenReturn(true);
        when(ticketApi.postTicket(TICKET)).thenReturn(Observable.empty());

        ticketRepository.createTicket(TICKET).subscribe();

        verify(ticketApi).postTicket(TICKET);
    }

    @Test
    public void shouldSetEventOnCreatedTicket() {
        Ticket created = mock(Ticket.class);

        when(repository.isConnected()).thenReturn(true);
        when(ticketApi.postTicket(TICKET)).thenReturn(Observable.just(created));
        when(repository.save(eq(Ticket.class), eq(created))).thenReturn(Completable.complete());

        ticketRepository.createTicket(TICKET).subscribe();

        verify(created).setEvent(EVENT);
    }

    @Test
    public void shouldSaveCreatedTicket() {
        Ticket created = mock(Ticket.class);

        when(repository.isConnected()).thenReturn(true);
        when(ticketApi.postTicket(TICKET)).thenReturn(Observable.just(created));
        when(repository.save(eq(Ticket.class), eq(created))).thenReturn(Completable.complete());

        ticketRepository.createTicket(TICKET).subscribe();

        verify(repository).save(Ticket.class, created);
    }

    // Ticket Get Tests

    @Test
    public void shouldCallGetTicketServiceOnReload() {
        when(repository.isConnected()).thenReturn(true);
        when(ticketApi.getTicket(ID)).thenReturn(Observable.empty());
        when(repository.getItems(eq(Ticket.class), any(SQLOperator.class)))
                .thenReturn(Observable.empty());

        ticketRepository.getTicket(ID, true).subscribe();

        verify(ticketApi).getTicket(ID);
    }

    @Test
    public void shouldCallGetTicketServiceWithNoneSaved() {
        when(repository.isConnected()).thenReturn(true);
        when(ticketApi.getTicket(ID)).thenReturn(Observable.empty());
        when(repository.getItems(eq(Ticket.class), any(SQLOperator.class)))
                .thenReturn(Observable.empty());

        ticketRepository.getTicket(ID, false).subscribe();

        verify(ticketApi).getTicket(ID);
    }

    @Test
    public void shouldSaveTicketOnGet() {
        when(repository.isConnected()).thenReturn(true);
        when(ticketApi.getTicket(ID)).thenReturn(Observable.just(TICKET));
        when(repository.save(eq(Ticket.class), eq(TICKET))).thenReturn(Completable.complete());
        when(repository.getItems(eq(Ticket.class), any(SQLOperator.class)))
                .thenReturn(Observable.empty());

        ticketRepository.getTicket(ID, true).subscribe();

        verify(repository).save(Ticket.class, TICKET);
    }

    // Tickets Get Tests

    @Test
    public void shouldCallGetTicketsServiceOnReload() {
        when(repository.isConnected()).thenReturn(true);
        when(ticketApi.getTickets(ID)).thenReturn(Observable.empty());
        when(repository.getItems(eq(Ticket.class), any(SQLOperator.class)))
                .thenReturn(Observable.empty());

        ticketRepository.getTickets(ID, true).subscribe();

        verify(ticketApi).getTickets(ID);
    }

    @Test
    public void shouldCallGetTicketsServiceWithNoneSaved() {
        when(repository.isConnected()).thenReturn(true);
        when(ticketApi.getTickets(ID)).thenReturn(Observable.empty());
        when(repository.getItems(eq(Ticket.class), any(SQLOperator.class)))
                .thenReturn(Observable.empty());

        ticketRepository.getTickets(ID, false).subscribe();

        verify(ticketApi).getTickets(ID);
    }

    @Test
    public void shouldSaveTicketsOnGet() {
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(TICKET);

        when(repository.isConnected()).thenReturn(true);
        when(ticketApi.getTickets(ID)).thenReturn(Observable.just(tickets));
        when(repository.syncSave(eq(Ticket.class), eq(tickets), any(), any()))
                .thenReturn(Completable.complete());
        when(repository.getItems(eq(Ticket.class), any(SQLOperator.class)))
                .thenReturn(Observable.empty());

        ticketRepository.getTickets(ID, true).subscribe();

        verify(repository).syncSave(eq(Ticket.class), eq(tickets), any(), any());
    }
}
