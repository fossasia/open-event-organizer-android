package org.fossasia.openevent.app.unit.presenter;

import org.fossasia.openevent.app.common.ContextManager;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.models.Ticket;
import org.fossasia.openevent.app.data.models.dto.ObservableString;
import org.fossasia.openevent.app.data.repository.ITicketRepository;
import org.fossasia.openevent.app.utils.DateUtils;
import org.fossasia.openevent.app.core.ticket.create.CreateTicketPresenter;
import org.fossasia.openevent.app.core.ticket.create.ICreateTicketView;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.threeten.bp.LocalDateTime;

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TicketCreatePresenterTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock private ICreateTicketView ticketsView;
    @Mock private ITicketRepository ticketRepository;
    @Mock private Event event;

    private CreateTicketPresenter createTicketPresenter;

    @Before
    public void setUp() {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());

        setupMockEvent();
        ContextManager.setSelectedEvent(event);
        createTicketPresenter = new CreateTicketPresenter(ticketRepository);
        createTicketPresenter.attach(ticketsView);
        ContextManager.setSelectedEvent(null);
    }

    private void setupMockEvent() {
        when(event.getTimezone()).thenReturn("UTC");
        when(event.getEndsAt()).thenReturn(new ObservableString("2018-12-14T23:59:59.123456+00:00"));
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldInitializeTicket() {
        Ticket ticket = createTicketPresenter.getTicket();

        assertEquals("free", ticket.getType());
        assertNotNull(ticket.getSalesStartsAt());
        assertNotNull(ticket.getSalesEndsAt());
    }

    @Test
    public void shouldRejectWrongSaleDates() {
        Ticket ticket = createTicketPresenter.getTicket();

        String isoDate = DateUtils.formatDateToIso(LocalDateTime.now());
        ticket.getSalesStartsAt().set(isoDate);
        ticket.getSalesEndsAt().set(isoDate);

        createTicketPresenter.createTicket();

        verify(ticketsView).showError(anyString());
        verify(ticketRepository, never()).createTicket(any());
    }

    @Test
    public void shouldAcceptCorrectSaleDates() {
        Ticket ticket = createTicketPresenter.getTicket();

        when(ticketRepository.createTicket(ticket)).thenReturn(Observable.empty());

        String isoDateNow = DateUtils.formatDateToIso(LocalDateTime.now());
        String isoDateThen = DateUtils.formatDateToIso(LocalDateTime.MAX);
        ticket.getSalesStartsAt().set(isoDateNow);
        ticket.getSalesEndsAt().set(isoDateThen);

        createTicketPresenter.createTicket();

        verify(ticketsView, never()).showError(anyString());
        verify(ticketRepository).createTicket(ticket);
    }

    @Test
    public void shouldShowErrorOnFailure() {
        Ticket ticket = createTicketPresenter.getTicket();

        when(ticketRepository.createTicket(ticket)).thenReturn(Observable.error(new Throwable("Error")));

        String isoDateNow = DateUtils.formatDateToIso(LocalDateTime.now());
        String isoDateThen = DateUtils.formatDateToIso(LocalDateTime.MAX);
        ticket.getSalesStartsAt().set(isoDateNow);
        ticket.getSalesEndsAt().set(isoDateThen);

        createTicketPresenter.createTicket();

        InOrder inOrder = Mockito.inOrder(ticketsView);

        inOrder.verify(ticketsView).showProgress(true);
        inOrder.verify(ticketsView).showError("Error");
        inOrder.verify(ticketsView).showProgress(false);
    }

    @Test
    public void shouldShowSuccessOnCreated() {
        Ticket ticket = createTicketPresenter.getTicket();

        when(ticketRepository.createTicket(ticket)).thenReturn(Observable.just(ticket));

        String isoDateNow = DateUtils.formatDateToIso(LocalDateTime.now());
        String isoDateThen = DateUtils.formatDateToIso(LocalDateTime.MAX);
        ticket.getSalesStartsAt().set(isoDateNow);
        ticket.getSalesEndsAt().set(isoDateThen);

        createTicketPresenter.createTicket();

        InOrder inOrder = Mockito.inOrder(ticketsView);

        inOrder.verify(ticketsView).showProgress(true);
        inOrder.verify(ticketsView).onSuccess(anyString());
        inOrder.verify(ticketsView).dismiss();
        inOrder.verify(ticketsView).showProgress(false);
    }

}
