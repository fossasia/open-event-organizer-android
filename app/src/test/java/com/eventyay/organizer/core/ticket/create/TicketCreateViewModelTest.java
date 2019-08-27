package com.eventyay.organizer.core.ticket.create;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import com.eventyay.organizer.common.ContextManager;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.ticket.Ticket;
import com.eventyay.organizer.data.ticket.TicketRepository;
import com.eventyay.organizer.utils.DateUtils;
import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.threeten.bp.LocalDateTime;

@RunWith(JUnit4.class)
public class TicketCreateViewModelTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule public TestRule rule = new InstantTaskExecutorRule();

    @Mock private TicketRepository ticketRepository;
    @Mock private Event event;

    private CreateTicketViewModel createTicketViewModel;

    @Mock Observer<String> error;
    @Mock Observer<Boolean> progress;
    @Mock Observer<String> success;
    @Mock Observer<Void> dismiss;

    @Before
    public void setUp() {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(
                schedulerCallable -> Schedulers.trampoline());

        setupMockEvent();
        ContextManager.setSelectedEvent(event);
        createTicketViewModel = new CreateTicketViewModel(ticketRepository);
        ContextManager.setSelectedEvent(null);
    }

    private void setupMockEvent() {
        when(event.getTimezone()).thenReturn("UTC");
        when(event.getStartsAt()).thenReturn("2019-06-18T23:59:59.123456+00:00");
        when(event.getEndsAt()).thenReturn("2019-06-20T23:59:59.123456+00:00");
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldInitializeTicket() {
        Ticket ticket = createTicketViewModel.getTicket();

        Assert.assertEquals("free", ticket.getType());
        Assert.assertNotNull(ticket.getSalesStartsAt());
        Assert.assertNotNull(ticket.getSalesEndsAt());
    }

    @Test
    public void shouldRejectWrongSaleDates() {
        Ticket ticket = createTicketViewModel.getTicket();

        String isoDate = DateUtils.formatDateToIso(LocalDateTime.now());
        ticket.setSalesStartsAt(isoDate);
        ticket.setSalesEndsAt(isoDate);

        InOrder inOrder = inOrder(error);
        createTicketViewModel.getError().observeForever(error);

        createTicketViewModel.createTicket();

        inOrder.verify(error).onChanged(anyString());
        verify(ticketRepository, never()).createTicket(any());
    }

    @Test
    public void shouldAcceptCorrectSaleDates() {
        Ticket ticket = createTicketViewModel.getTicket();
        ContextManager.setSelectedEvent(event);

        when(ticketRepository.createTicket(ticket)).thenReturn(Observable.empty());

        String isoDateNow = DateUtils.formatDateToIso(LocalDateTime.now());
        String isoDateThen = DateUtils.formatDateToIso(LocalDateTime.MAX);
        ticket.setSalesStartsAt(isoDateNow);
        ticket.setSalesEndsAt(isoDateThen);

        InOrder inOrder = inOrder(error);
        createTicketViewModel.getError().observeForever(error);

        createTicketViewModel.createTicket();

        inOrder.verify(error, never()).onChanged(anyString());
        verify(ticketRepository).createTicket(ticket);

        ContextManager.setSelectedEvent(null);
    }

    @Test
    public void shouldShowErrorOnFailure() {
        String errorString = "Error";
        Ticket ticket = createTicketViewModel.getTicket();
        ContextManager.setSelectedEvent(event);

        when(ticketRepository.createTicket(ticket))
                .thenReturn(Observable.error(new Throwable("Error")));

        String isoDateNow = DateUtils.formatDateToIso(LocalDateTime.now());
        String isoDateThen = DateUtils.formatDateToIso(LocalDateTime.MAX);
        ticket.setSalesStartsAt(isoDateNow);
        ticket.setSalesEndsAt(isoDateThen);

        InOrder inOrder = inOrder(progress, error);
        createTicketViewModel.getProgress().observeForever(progress);
        createTicketViewModel.getError().observeForever(error);

        createTicketViewModel.createTicket();

        inOrder.verify(progress).onChanged(true);
        inOrder.verify(error).onChanged(errorString);
        inOrder.verify(progress).onChanged(false);

        ContextManager.setSelectedEvent(null);
    }

    @Test
    public void shouldShowSuccessOnCreated() {
        String successString = "Ticket Created";
        Ticket ticket = createTicketViewModel.getTicket();
        when(ticketRepository.createTicket(ticket)).thenReturn(Observable.just(ticket));
        ContextManager.setSelectedEvent(event);

        String isoDateNow = DateUtils.formatDateToIso(LocalDateTime.now());
        String isoDateThen = DateUtils.formatDateToIso(LocalDateTime.MAX);
        ticket.setSalesStartsAt(isoDateNow);
        ticket.setSalesEndsAt(isoDateThen);

        InOrder inOrder = inOrder(progress, dismiss, success);
        createTicketViewModel.getProgress().observeForever(progress);
        createTicketViewModel.getDismiss().observeForever(dismiss);
        createTicketViewModel.getSuccess().observeForever(success);

        createTicketViewModel.createTicket();

        inOrder.verify(progress).onChanged(true);
        inOrder.verify(success).onChanged(successString);
        inOrder.verify(dismiss).onChanged(null);
        inOrder.verify(progress).onChanged(false);

        ContextManager.setSelectedEvent(null);
    }
}
