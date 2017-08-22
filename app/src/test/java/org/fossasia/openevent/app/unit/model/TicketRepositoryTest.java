package org.fossasia.openevent.app.unit.model;

import com.raizlabs.android.dbflow.sql.language.SQLOperator;

import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.common.data.contract.IUtilModel;
import org.fossasia.openevent.app.common.data.db.contract.IDatabaseRepository;
import org.fossasia.openevent.app.common.data.models.Event;
import org.fossasia.openevent.app.common.data.models.Ticket;
import org.fossasia.openevent.app.common.data.network.EventService;
import org.fossasia.openevent.app.common.data.repository.TicketRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("PMD.TooManyMethods")
public class TicketRepositoryTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private TicketRepository ticketRepository;
    private static final Ticket TICKET = new Ticket();
    private static final Event EVENT = new Event();
    private static final long ID = 12;

    @Mock private EventService eventService;
    @Mock private IUtilModel utilModel;
    @Mock private IDatabaseRepository databaseRepository;

    static {
        TICKET.setEvent(EVENT);
    }

    @Before
    public void setUp() {
        ticketRepository = new TicketRepository(utilModel, databaseRepository, eventService);
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    // Network down tests

    @Test
    public void shouldReturnConnectionErrorOnCreateTicket() {
        when(utilModel.isConnected()).thenReturn(false);

        Observable<Ticket> ticketObservable = ticketRepository.createTicket(TICKET);

        ticketObservable
            .test()
            .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    @Test
    public void shouldReturnConnectionErrorDeleteTicket() {
        when(utilModel.isConnected()).thenReturn(false);

        Completable ticketObservable = ticketRepository.deleteTicket(ID);

        ticketObservable
            .test()
            .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    @Test
    public void shouldReturnConnectionErrorOnGetTicketWithReload() {
        when(utilModel.isConnected()).thenReturn(false);

        Observable<Ticket> ticketObservable = ticketRepository.getTicket(ID, true);

        ticketObservable
            .test()
            .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    @Test
    public void shouldReturnConnectionErrorOnGetTicketWithNoneSaved() {
        when(utilModel.isConnected()).thenReturn(false);
        when(databaseRepository.getItems(eq(Ticket.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        Observable<Ticket> ticketObservable = ticketRepository.getTicket(ID, false);

        ticketObservable
            .test()
            .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    @Test
    public void shouldReturnConnectionErrorOnGetTicketsWithReload() {
        when(utilModel.isConnected()).thenReturn(false);

        Observable<Ticket> ticketObservable = ticketRepository.getTickets(ID, true);

        ticketObservable
            .test()
            .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    @Test
    public void shouldReturnConnectionErrorOnGetTicketsWithNoneSaved() {
        when(utilModel.isConnected()).thenReturn(false);
        when(databaseRepository.getItems(eq(Ticket.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        Observable<Ticket> ticketObservable = ticketRepository.getTickets(ID, false);

        ticketObservable
            .test()
            .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    // Network up tests

    // Ticket Create Tests

    @Test
    public void shouldCallCreateTicketService() {
        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.postTicket(TICKET)).thenReturn(Observable.empty());

        ticketRepository.createTicket(TICKET).subscribe();

        verify(eventService).postTicket(TICKET);
    }

    @Test
    public void shouldSetEventOnCreatedTicket() {
        Ticket created = mock(Ticket.class);

        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.postTicket(TICKET)).thenReturn(Observable.just(created));
        when(databaseRepository.save(eq(Ticket.class), eq(created))).thenReturn(Completable.complete());

        ticketRepository.createTicket(TICKET).subscribe();

        verify(created).setEvent(EVENT);
    }

    @Test
    public void shouldSaveCreatedTicket() {
        Ticket created = mock(Ticket.class);

        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.postTicket(TICKET)).thenReturn(Observable.just(created));
        when(databaseRepository.save(eq(Ticket.class), eq(created))).thenReturn(Completable.complete());

        ticketRepository.createTicket(TICKET).subscribe();

        verify(databaseRepository).save(Ticket.class, created);
    }

    // Ticket Get Tests

    @Test
    public void shouldCallGetTicketServiceOnReload() {
        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.getTicket(ID)).thenReturn(Observable.empty());
        when(databaseRepository.getItems(eq(Ticket.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        ticketRepository.getTicket(ID, true).subscribe();

        verify(eventService).getTicket(ID);
    }

    @Test
    public void shouldCallGetTicketServiceWithNoneSaved() {
        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.getTicket(ID)).thenReturn(Observable.empty());
        when(databaseRepository.getItems(eq(Ticket.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        ticketRepository.getTicket(ID, false).subscribe();

        verify(eventService).getTicket(ID);
    }

    @Test
    public void shouldSaveTicketOnGet() {
        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.getTicket(ID)).thenReturn(Observable.just(TICKET));
        when(databaseRepository.save(eq(Ticket.class), eq(TICKET))).thenReturn(Completable.complete());
        when(databaseRepository.getItems(eq(Ticket.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        ticketRepository.getTicket(ID, true).subscribe();

        verify(databaseRepository).save(Ticket.class, TICKET);
    }

    // Tickets Get Tests

    @Test
    public void shouldCallGetTicketsServiceOnReload() {
        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.getTickets(ID)).thenReturn(Observable.empty());
        when(databaseRepository.getItems(eq(Ticket.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        ticketRepository.getTickets(ID, true).subscribe();

        verify(eventService).getTickets(ID);
    }

    @Test
    public void shouldCallGetTicketsServiceWithNoneSaved() {
        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.getTickets(ID)).thenReturn(Observable.empty());
        when(databaseRepository.getItems(eq(Ticket.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        ticketRepository.getTickets(ID, false).subscribe();

        verify(eventService).getTickets(ID);
    }

    @Test
    public void shouldDeleteTicketsOnGet() {
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(TICKET);

        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.getTickets(ID)).thenReturn(Observable.just(tickets));
        when(databaseRepository.deleteAll(Ticket.class)).thenReturn(Completable.complete());
        when(databaseRepository.saveList(Ticket.class, tickets)).thenReturn(Completable.complete());
        when(databaseRepository.getItems(eq(Ticket.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        ticketRepository.getTickets(ID, true).subscribe();

        verify(databaseRepository).deleteAll(Ticket.class);
    }

    @Test
    public void shouldSaveTicketsOnGet() {
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(TICKET);

        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.getTickets(ID)).thenReturn(Observable.just(tickets));
        when(databaseRepository.deleteAll(Ticket.class)).thenReturn(Completable.complete());
        when(databaseRepository.saveList(Ticket.class, tickets)).thenReturn(Completable.complete());
        when(databaseRepository.getItems(eq(Ticket.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        ticketRepository.getTickets(ID, true).subscribe();

        verify(databaseRepository).saveList(Ticket.class, tickets);
    }

}
