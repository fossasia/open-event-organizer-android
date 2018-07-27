package com.eventyay.organizer.core.orders.detail;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;

import com.eventyay.organizer.core.presenter.TestUtil;
import com.eventyay.organizer.data.attendee.Attendee;
import com.eventyay.organizer.data.attendee.AttendeeRepository;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.event.EventRepository;
import com.eventyay.organizer.data.order.Order;
import com.eventyay.organizer.data.order.OrderRepository;
import com.eventyay.organizer.data.ticket.Ticket;
import com.eventyay.organizer.data.ticket.TicketRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OrderDetailViewModelTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    private OrderDetailViewModel orderDetailsViewModel;

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private AttendeeRepository attendeeRepository;

    private static final Order ORDER = new Order();
    private static final Event EVENT = new Event();

    private static final long ORDER_ID = 5L;
    private static final long EVENT_ID = 5L;
    private static final String ORDER_IDENTIFIER = "617ed24c-9a07-4084-b076-ed73552db27e";
    private static final String ERROR_MESSAGE = "Test Error";
    private static final String ERROR = "Error";

    private static final List<Attendee> ATTENDEES_LIST = Arrays.asList(
        Attendee.builder().id(12L).checking(false).isCheckedIn(false).build(),
        Attendee.builder().id(13L).checking(false).isCheckedIn(false).build(),
        Attendee.builder().id(14L).checking(false).isCheckedIn(false).build()
    );

    private static final List<Ticket> TICKETS_LIST = Arrays.asList(
        Ticket.builder().id(12L).build(),
        Ticket.builder().id(13L).build(),
        Ticket.builder().id(14L).build()
    );

    @Mock
    Observer<List<Attendee>> attendees;
    @Mock
    Observer<List<Ticket>> tickets;
    @Mock
    Observer<String> error;
    @Mock
    Observer<Boolean> progress;
    @Mock
    Observer<Order> order;

    @Before
    public void setUp() {
        orderDetailsViewModel = new OrderDetailViewModel(orderRepository, eventRepository, attendeeRepository, ticketRepository);
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldLoadOrderSuccessfully() {
        when(orderRepository.getOrder(ORDER_IDENTIFIER, false)).thenReturn(Observable.just(ORDER));
        when(eventRepository.getEvent(EVENT_ID, false)).thenReturn(Observable.just(EVENT));

        InOrder inOrder = Mockito.inOrder(orderRepository, eventRepository, order);

        orderDetailsViewModel.getOrder(ORDER_IDENTIFIER, EVENT_ID, false).observeForever(order);

        inOrder.verify(orderRepository).getOrder(ORDER_IDENTIFIER, false);
        inOrder.verify(eventRepository).getEvent(EVENT_ID, false);
        inOrder.verify(order).onChanged(ORDER);
    }

    @Test
    public void shouldShowErrorOnLoadingOrderUnsuccessfully() {
        when(orderRepository.getOrder(ORDER_IDENTIFIER, false)).thenReturn(Observable.error(new Throwable(ERROR)));
        when(eventRepository.getEvent(EVENT_ID, false)).thenReturn(Observable.just(EVENT));

        InOrder inOrder = Mockito.inOrder(orderRepository, eventRepository, order, error);

        orderDetailsViewModel.getError().observeForever(error);
        orderDetailsViewModel.getOrder(ORDER_IDENTIFIER, EVENT_ID, false).observeForever(order);

        inOrder.verify(orderRepository).getOrder(ORDER_IDENTIFIER, false);
        inOrder.verify(error).onChanged(ERROR);
        inOrder.verify(eventRepository).getEvent(EVENT_ID, false);
    }

    @Test
    public void shouldShowErrorOnLoadingEventUnsuccessfully() {
        when(orderRepository.getOrder(ORDER_IDENTIFIER, false)).thenReturn(Observable.just(ORDER));
        when(eventRepository.getEvent(EVENT_ID, false)).thenReturn(Observable.error(new Throwable(ERROR)));

        InOrder inOrder = Mockito.inOrder(orderRepository, eventRepository, order, error);

        orderDetailsViewModel.getError().observeForever(error);
        orderDetailsViewModel.getOrder(ORDER_IDENTIFIER, EVENT_ID, false).observeForever(order);

        inOrder.verify(orderRepository).getOrder(ORDER_IDENTIFIER, false);
        inOrder.verify(eventRepository).getEvent(EVENT_ID, false);
        inOrder.verify(error).onChanged(ERROR);
    }

    @Test
    public void shouldShowDataOnSwipeRefresh() {
        when(orderRepository.getOrder(ORDER_IDENTIFIER, true)).thenReturn(Observable.just(ORDER));
        when(attendeeRepository.getAttendeesUnderOrder(ORDER_IDENTIFIER, ORDER_ID, true))
            .thenReturn(Observable.fromIterable(ATTENDEES_LIST));
        when(ticketRepository.getTicketsUnderOrder(ORDER_IDENTIFIER, ORDER_ID, true))
            .thenReturn(Observable.fromIterable(TICKETS_LIST));

        attendees.onChanged(new ArrayList<>());
        tickets.onChanged(new ArrayList<>());

        orderDetailsViewModel.getOrder(ORDER_IDENTIFIER, EVENT_ID, true).observeForever(order);
        orderDetailsViewModel.getAttendeesUnderOrder(ORDER_IDENTIFIER, ORDER_ID, true);
        orderDetailsViewModel.getTicketsUnderOrder(ORDER_IDENTIFIER, ORDER_ID, true);

        verify(attendees).onChanged(new ArrayList<>());
        verify(tickets).onChanged(new ArrayList<>());
        verify(attendeeRepository).getAttendeesUnderOrder(ORDER_IDENTIFIER, ORDER_ID, true);
        verify(ticketRepository).getTicketsUnderOrder(ORDER_IDENTIFIER, ORDER_ID, true);
        verify(orderRepository).getOrder(ORDER_IDENTIFIER, true);
        verify(order).onChanged(ORDER);
    }

    @Test
    public void shouldLoadAttendeesSuccessfully() {
        when(attendeeRepository.getAttendeesUnderOrder(ORDER_IDENTIFIER, ORDER_ID, false))
            .thenReturn(Observable.fromIterable(ATTENDEES_LIST));

        InOrder inOrder = Mockito.inOrder(attendees, attendeeRepository, progress);

        orderDetailsViewModel.getProgress().observeForever(progress);

        attendees.onChanged(new ArrayList<>());

        orderDetailsViewModel.getAttendeesUnderOrder(ORDER_IDENTIFIER, ORDER_ID, false);

        inOrder.verify(attendees).onChanged(new ArrayList<>());
        inOrder.verify(attendeeRepository).getAttendeesUnderOrder(ORDER_IDENTIFIER, ORDER_ID, false);
        inOrder.verify(progress).onChanged(true);
        inOrder.verify(progress).onChanged(false);
    }

    @Test
    public void shouldLoadTicketsSuccessfully() {
        when(ticketRepository.getTicketsUnderOrder(ORDER_IDENTIFIER, ORDER_ID, false))
            .thenReturn(Observable.fromIterable(TICKETS_LIST));

        InOrder inOrder = Mockito.inOrder(tickets, ticketRepository, progress);

        orderDetailsViewModel.getProgress().observeForever(progress);

        tickets.onChanged(new ArrayList<>());

        orderDetailsViewModel.getTicketsUnderOrder(ORDER_IDENTIFIER, ORDER_ID, false);

        inOrder.verify(tickets).onChanged(new ArrayList<>());
        inOrder.verify(ticketRepository).getTicketsUnderOrder(ORDER_IDENTIFIER, ORDER_ID, false);
        inOrder.verify(progress).onChanged(true);
        inOrder.verify(progress).onChanged(false);
    }

    @Test
    public void shouldShowErrorInLoadingAttendees() {
        when(attendeeRepository.getAttendeesUnderOrder(ORDER_IDENTIFIER, ORDER_ID, false))
            .thenReturn(TestUtil.ERROR_OBSERVABLE);

        InOrder inOrder = Mockito.inOrder(attendees, attendeeRepository, progress, error);

        orderDetailsViewModel.getProgress().observeForever(progress);
        orderDetailsViewModel.getError().observeForever(error);

        attendees.onChanged(new ArrayList<>());

        orderDetailsViewModel.getAttendeesUnderOrder(ORDER_IDENTIFIER, ORDER_ID, false);

        inOrder.verify(attendeeRepository).getAttendeesUnderOrder(ORDER_IDENTIFIER, ORDER_ID, false);
        inOrder.verify(progress).onChanged(true);
        inOrder.verify(error).onChanged(ERROR_MESSAGE);
        inOrder.verify(progress).onChanged(false);
    }

    @Test
    public void shouldShowErrorInLoadingTickets() {
        when(ticketRepository.getTicketsUnderOrder(ORDER_IDENTIFIER, ORDER_ID, false))
            .thenReturn(TestUtil.ERROR_OBSERVABLE);

        InOrder inOrder = Mockito.inOrder(tickets, ticketRepository, progress, error);

        orderDetailsViewModel.getProgress().observeForever(progress);
        orderDetailsViewModel.getError().observeForever(error);

        tickets.onChanged(new ArrayList<>());

        orderDetailsViewModel.getTicketsUnderOrder(ORDER_IDENTIFIER, ORDER_ID, false);

        inOrder.verify(ticketRepository).getTicketsUnderOrder(ORDER_IDENTIFIER, ORDER_ID, false);
        inOrder.verify(progress).onChanged(true);
        inOrder.verify(error).onChanged(ERROR_MESSAGE);
        inOrder.verify(progress).onChanged(false);
    }

    @Test
    public void shouldHandleTogglingError() {
        when(attendeeRepository.scheduleToggle(ATTENDEES_LIST.get(1))).thenReturn(Completable.error(new Throwable()));
        orderDetailsViewModel.setAttendees(ATTENDEES_LIST);

        InOrder inOrder = Mockito.inOrder(attendeeRepository, error);

        orderDetailsViewModel.getError().observeForever(error);

        orderDetailsViewModel.toggleCheckIn(1);

        inOrder.verify(attendeeRepository).scheduleToggle(ATTENDEES_LIST.get(1));
        inOrder.verify(error).onChanged(any());
    }
}
