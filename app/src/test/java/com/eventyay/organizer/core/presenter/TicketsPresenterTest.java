package com.eventyay.organizer.core.presenter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.core.ticket.list.TicketsPresenter;
import com.eventyay.organizer.core.ticket.list.TicketsView;
import com.eventyay.organizer.data.db.DatabaseChangeListener;
import com.eventyay.organizer.data.ticket.Ticket;
import com.eventyay.organizer.data.ticket.TicketRepository;
import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

@SuppressWarnings({"PMD.CommentSize", "PMD.LineTooLong"})
@RunWith(JUnit4.class)
public class TicketsPresenterTest {
    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock private TicketsView ticketsView;
    @Mock private TicketRepository ticketRepository;
    @Mock private DatabaseChangeListener<Ticket> databaseChangeListener;

    private TicketsPresenter ticketsPresenter;

    private static final long ID = 42;

    private static final List<Ticket> TICKETS =
            Arrays.asList(
                    Ticket.builder().id(2L).type("free").build(),
                    Ticket.builder().id(3L).type("free").build(),
                    Ticket.builder().id(4L).type("paid").build());

    @Before
    public void setUp() {
        ticketsPresenter = new TicketsPresenter(ticketRepository, databaseChangeListener);
        ticketsPresenter.attach(ID, ticketsView);

        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(
                schedulerCallable -> Schedulers.trampoline());
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldLoadTicketsAutomatically() {
        when(ticketRepository.getTickets(anyLong(), anyBoolean()))
                .thenReturn(Observable.fromIterable(TICKETS));
        when(databaseChangeListener.getNotifier()).thenReturn(PublishSubject.create());

        ticketsPresenter.start();

        verify(ticketRepository).getTickets(ID, false);
    }

    @Test
    public void shouldShowTicketsAutomatically() {
        when(ticketRepository.getTickets(ID, false)).thenReturn(Observable.fromIterable(TICKETS));
        when(databaseChangeListener.getNotifier()).thenReturn(PublishSubject.create());

        ticketsPresenter.start();

        verify(ticketsView).showResults(TICKETS);
    }

    @Test
    public void shouldActivateChangeListenerOnStart() {
        when(ticketRepository.getTickets(anyLong(), anyBoolean()))
                .thenReturn(Observable.fromIterable(TICKETS));
        when(databaseChangeListener.getNotifier()).thenReturn(PublishSubject.create());

        ticketsPresenter.start();

        verify(databaseChangeListener).startListening();
    }

    @Test
    public void shouldDisableChangeListenerOnDetach() {
        ticketsPresenter.detach();

        verify(databaseChangeListener).stopListening();
    }

    @Test
    public void shouldShowEmptyViewOnNoTickets() {
        when(ticketRepository.getTickets(anyLong(), anyBoolean()))
                .thenReturn(Observable.fromIterable(new ArrayList<>()));

        ticketsPresenter.loadTickets(true);

        verify(ticketsView).showEmptyView(true);
    }

    @Test
    public void shouldShowTicketsOnSwipeRefreshSuccess() {
        when(ticketRepository.getTickets(ID, true)).thenReturn(Observable.fromIterable(TICKETS));

        ticketsPresenter.loadTickets(true);

        verify(ticketsView).showResults(any());
    }

    @Test
    public void shouldShowErrorMessageOnSwipeRefreshError() {
        when(ticketRepository.getTickets(ID, true)).thenReturn(Observable.error(Logger.TEST_ERROR));

        ticketsPresenter.loadTickets(true);

        verify(ticketsView).showError(Logger.TEST_ERROR.getMessage());
    }

    @Test
    public void testProgressbarOnSwipeRefreshSuccess() {
        when(ticketRepository.getTickets(ID, true)).thenReturn(Observable.fromIterable(TICKETS));

        ticketsPresenter.loadTickets(true);

        InOrder inOrder = Mockito.inOrder(ticketsView);

        inOrder.verify(ticketsView).showProgress(true);
        inOrder.verify(ticketsView).onRefreshComplete(true);
        inOrder.verify(ticketsView).showProgress(false);
    }

    @Test
    public void testProgressbarOnSwipeRefreshError() {
        when(ticketRepository.getTickets(ID, true)).thenReturn(Observable.error(Logger.TEST_ERROR));

        ticketsPresenter.loadTickets(true);

        InOrder inOrder = Mockito.inOrder(ticketsView);

        inOrder.verify(ticketsView).showProgress(true);
        inOrder.verify(ticketsView).onRefreshComplete(false);
        inOrder.verify(ticketsView).showProgress(false);
    }

    @Test
    public void testProgressbarOnSwipeRefreshNoItem() {
        List<Ticket> emptyList = new ArrayList<>();
        when(ticketRepository.getTickets(ID, true)).thenReturn(Observable.fromIterable(emptyList));

        ticketsPresenter.loadTickets(true);

        InOrder inOrder = Mockito.inOrder(ticketsView);

        inOrder.verify(ticketsView).showProgress(true);
        inOrder.verify(ticketsView).onRefreshComplete(true);
        inOrder.verify(ticketsView).showProgress(false);
    }

    /*@Test
    public void shouldUpdateViewOnTicketDeleteSuccess() {
        when(ticketRepository.deleteTicket(TICKETS.get(0).id)).thenReturn(Completable.complete());

        ticketsPresenter.deleteTicket(TICKETS.get(0));

        verify(ticketsView).showTicketDeleted(anyString());
    }

    @Test
    public void shouldShowErrorMessageOnTicketDeleteError() {
        when(ticketRepository.deleteTicket(TICKETS.get(0).id)).thenReturn(Completable.error(Logger.TEST_ERROR));

        ticketsPresenter.deleteTicket(TICKETS.get(0));

        verify(ticketsView).showError(Logger.TEST_MESSAGE);
    }

    @Test
    public void testDeletingFlagOnTicketDelete() {
        when(ticketRepository.deleteTicket(TICKETS.get(0).id)).thenReturn(Completable.complete());

        ticketsPresenter.deleteTicket(TICKETS.get(0));

        assertFalse(TICKETS.get(0).deleting.get());
    }*/
}
