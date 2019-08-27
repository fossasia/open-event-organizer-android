package com.eventyay.organizer.core.presenter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.core.event.dashboard.EventDashboardPresenter;
import com.eventyay.organizer.core.event.dashboard.EventDashboardView;
import com.eventyay.organizer.core.event.dashboard.analyser.ChartAnalyser;
import com.eventyay.organizer.core.event.dashboard.analyser.TicketAnalyser;
import com.eventyay.organizer.data.ContextUtils;
import com.eventyay.organizer.data.Preferences;
import com.eventyay.organizer.data.attendee.Attendee;
import com.eventyay.organizer.data.attendee.AttendeeRepository;
import com.eventyay.organizer.data.db.DatabaseChangeListener;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.event.EventRepository;
import com.eventyay.organizer.data.event.EventStatistics;
import com.eventyay.organizer.data.order.OrderRepository;
import com.eventyay.organizer.data.order.OrderStatistics;
import com.eventyay.organizer.data.ticket.Ticket;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
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

@RunWith(JUnit4.class)
@SuppressWarnings("PMD.TooManyMethods")
public class EventDashboardPresenterTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock private EventDashboardView eventDetailView;
    @Mock private EventRepository eventRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private TicketAnalyser ticketAnalyser;
    @Mock private AttendeeRepository attendeeRepository;
    @Mock private ChartAnalyser chartAnalyser;
    @Mock private ContextUtils utilModel;
    @Mock private DatabaseChangeListener<Event> databaseChangeListener;
    @Mock private Preferences sharedPreferenceModel;

    private static final long ID = 42L;
    private EventDashboardPresenter eventDashboardPresenter;

    private static final Event EVENT = Event.builder().id(ID).state(Event.STATE_PUBLISHED).build();
    private static final Event TOGGLED_EVENT =
            Event.builder().id(ID).state(Event.STATE_DRAFT).build();
    private static final EventStatistics EVENT_STATISTICS =
            EventStatistics.builder().id("2").build();
    private static final OrderStatistics ORDER_STATISTICS =
            OrderStatistics.builder().id(2L).build();

    private static final List<Attendee> ATTENDEES =
            Arrays.asList(
                    Attendee.builder().isCheckedIn(false).build(),
                    Attendee.builder().isCheckedIn(true).build(),
                    Attendee.builder().isCheckedIn(false).build(),
                    Attendee.builder().isCheckedIn(false).build(),
                    Attendee.builder().isCheckedIn(true).build(),
                    Attendee.builder().isCheckedIn(true).build(),
                    Attendee.builder().isCheckedIn(false).build());

    private static final List<Ticket> TICKETS =
            Arrays.asList(
                    Ticket.builder().id(1L).quantity(21L).build(),
                    Ticket.builder().id(2L).quantity(50L).build(),
                    Ticket.builder().id(3L).quantity(43L).build());

    static {
        // Event set up
        EVENT.setName("Event Name");
        EVENT.setStartsAt("2004-05-21T9:30:00");
        EVENT.setEndsAt("2012-09-20T12:23:00");
        EVENT.setTickets(TICKETS);
    }

    @Before
    public void setUp() {
        eventDashboardPresenter =
                new EventDashboardPresenter(
                        eventRepository,
                        attendeeRepository,
                        orderRepository,
                        ticketAnalyser,
                        chartAnalyser,
                        utilModel,
                        databaseChangeListener,
                        sharedPreferenceModel);

        eventDashboardPresenter.attach(EVENT.getId(), eventDetailView);

        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(
                schedulerCallable -> Schedulers.trampoline());

        when(chartAnalyser.loadData(EVENT.getId())).thenReturn(Completable.complete());
        when(chartAnalyser.loadDataCheckIn(EVENT.getId())).thenReturn(Completable.complete());
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldLoadEventAndAttendeesAndStatisticsAutomatically() {
        when(databaseChangeListener.getNotifier()).thenReturn(PublishSubject.create());

        when(attendeeRepository.getAttendees(ID, false))
                .thenReturn(Observable.fromIterable(ATTENDEES));

        when(eventRepository.getEvent(ID, false)).thenReturn(Observable.just(EVENT));

        when(eventRepository.getEventStatistics(ID)).thenReturn(Observable.just(EVENT_STATISTICS));

        when(orderRepository.getOrderStatisticsForEvent(ID, false))
                .thenReturn(Observable.just(ORDER_STATISTICS));

        eventDashboardPresenter.start();

        verify(eventRepository).getEvent(ID, false);
        verify(attendeeRepository).getAttendees(ID, false);
        verify(eventRepository).getEventStatistics(ID);
        verify(orderRepository).getOrderStatisticsForEvent(ID, false);
    }

    @Test
    public void shouldDetachViewOnStop() {
        assertNotNull(eventDashboardPresenter.getView());

        eventDashboardPresenter.detach();

        assertTrue(eventDashboardPresenter.getDisposable().isDisposed());
    }

    @Test
    public void shouldShowEventError() {
        when(eventRepository.getEvent(ID, false)).thenReturn(TestUtil.ERROR_OBSERVABLE);
        when(eventRepository.getEventStatistics(ID)).thenReturn(Observable.just(EVENT_STATISTICS));
        when(orderRepository.getOrderStatisticsForEvent(ID, false))
                .thenReturn(Observable.just(ORDER_STATISTICS));

        eventDashboardPresenter.loadDetails(false);

        verify(eventDetailView).showError(Logger.TEST_MESSAGE);
    }

    @Test
    public void shouldLoadEventSuccessfully() {
        when(eventRepository.getEvent(ID, false)).thenReturn(Observable.just(EVENT));
        when(attendeeRepository.getAttendees(ID, false)).thenReturn(TestUtil.ERROR_OBSERVABLE);
        when(eventRepository.getEventStatistics(ID)).thenReturn(Observable.just(EVENT_STATISTICS));
        when(orderRepository.getOrderStatisticsForEvent(ID, false))
                .thenReturn(Observable.just(ORDER_STATISTICS));

        eventDashboardPresenter.loadDetails(false);

        verify(eventDetailView).showResult(EVENT);
        verify(ticketAnalyser).analyseTotalTickets(EVENT);
    }

    @Test
    public void shouldShowAttendeeError() {
        when(eventRepository.getEvent(ID, false)).thenReturn(Observable.just(EVENT));
        when(attendeeRepository.getAttendees(ID, false)).thenReturn(TestUtil.ERROR_OBSERVABLE);
        when(eventRepository.getEventStatistics(ID)).thenReturn(Observable.just(EVENT_STATISTICS));
        when(orderRepository.getOrderStatisticsForEvent(ID, false))
                .thenReturn(Observable.just(ORDER_STATISTICS));

        eventDashboardPresenter.loadDetails(false);

        verify(eventDetailView).showError(Logger.TEST_MESSAGE);
    }

    @Test
    public void shouldLoadAttendeesSuccessfully() {
        when(databaseChangeListener.getNotifier()).thenReturn(PublishSubject.create());

        when(attendeeRepository.getAttendees(ID, false))
                .thenReturn(Observable.fromIterable(ATTENDEES));
        when(eventRepository.getEvent(ID, false)).thenReturn(Observable.just(EVENT));
        when(eventRepository.getEventStatistics(ID)).thenReturn(Observable.just(EVENT_STATISTICS));
        when(orderRepository.getOrderStatisticsForEvent(ID, false))
                .thenReturn(Observable.just(ORDER_STATISTICS));

        eventDashboardPresenter.start();

        verify(ticketAnalyser).analyseSoldTickets(EVENT, ATTENDEES);
    }

    @Test
    public void shouldLoadEventStatisticsSuccessfully() {
        when(databaseChangeListener.getNotifier()).thenReturn(PublishSubject.create());

        when(attendeeRepository.getAttendees(ID, false))
                .thenReturn(Observable.fromIterable(ATTENDEES));
        when(eventRepository.getEvent(ID, false)).thenReturn(Observable.just(EVENT));
        when(eventRepository.getEventStatistics(ID)).thenReturn(Observable.just(EVENT_STATISTICS));
        when(orderRepository.getOrderStatisticsForEvent(ID, false))
                .thenReturn(Observable.just(ORDER_STATISTICS));

        eventDashboardPresenter.start();

        verify(eventDetailView).showStatistics(EVENT_STATISTICS);
    }

    @Test
    public void shouldShowEventStatisticsError() {
        when(eventRepository.getEvent(ID, false)).thenReturn(Observable.just(EVENT));
        when(attendeeRepository.getAttendees(ID, false))
                .thenReturn(Observable.fromIterable(ATTENDEES));
        when(eventRepository.getEventStatistics(ID)).thenReturn(TestUtil.ERROR_OBSERVABLE);
        when(orderRepository.getOrderStatisticsForEvent(ID, false))
                .thenReturn(Observable.just(ORDER_STATISTICS));

        eventDashboardPresenter.loadDetails(false);

        verify(eventDetailView).showError(Logger.TEST_MESSAGE);
    }

    @Test
    public void shouldLoadOrderStatisticsSuccessfully() {
        when(databaseChangeListener.getNotifier()).thenReturn(PublishSubject.create());

        when(attendeeRepository.getAttendees(ID, false))
                .thenReturn(Observable.fromIterable(ATTENDEES));
        when(eventRepository.getEvent(ID, false)).thenReturn(Observable.just(EVENT));
        when(eventRepository.getEventStatistics(ID)).thenReturn(Observable.just(EVENT_STATISTICS));
        when(orderRepository.getOrderStatisticsForEvent(ID, false))
                .thenReturn(Observable.just(ORDER_STATISTICS));

        eventDashboardPresenter.start();

        verify(eventDetailView).showOrderStatistics(ORDER_STATISTICS);
    }

    @Test
    public void shouldShowOrderStatisticsError() {
        when(eventRepository.getEvent(ID, false)).thenReturn(Observable.just(EVENT));
        when(attendeeRepository.getAttendees(ID, false))
                .thenReturn(Observable.fromIterable(ATTENDEES));
        when(eventRepository.getEventStatistics(ID)).thenReturn(Observable.just(EVENT_STATISTICS));
        when(orderRepository.getOrderStatisticsForEvent(ID, false))
                .thenReturn(TestUtil.ERROR_OBSERVABLE);

        eventDashboardPresenter.loadDetails(false);

        verify(eventDetailView).showError(Logger.TEST_MESSAGE);
    }

    @Test
    public void shouldHideProgressbarCorrectly() {
        when(databaseChangeListener.getNotifier()).thenReturn(PublishSubject.create());

        when(attendeeRepository.getAttendees(ID, false))
                .thenReturn(Observable.fromIterable(ATTENDEES));

        when(eventRepository.getEvent(ID, false)).thenReturn(Observable.just(EVENT));

        when(eventRepository.getEventStatistics(ID)).thenReturn(Observable.just(EVENT_STATISTICS));

        when(orderRepository.getOrderStatisticsForEvent(ID, false))
                .thenReturn(Observable.just(ORDER_STATISTICS));

        InOrder inOrder = Mockito.inOrder(eventDetailView);

        eventDashboardPresenter.start();

        inOrder.verify(eventDetailView).showProgress(true);
        inOrder.verify(eventDetailView).showProgress(false);
    }

    @Test
    public void shouldHideProgressbarOnEventError() {
        when(databaseChangeListener.getNotifier()).thenReturn(PublishSubject.create());

        when(eventRepository.getEvent(ID, false)).thenReturn(TestUtil.ERROR_OBSERVABLE);

        when(eventRepository.getEventStatistics(ID)).thenReturn(Observable.just(EVENT_STATISTICS));

        when(orderRepository.getOrderStatisticsForEvent(ID, false))
                .thenReturn(Observable.just(ORDER_STATISTICS));

        InOrder inOrder = Mockito.inOrder(eventDetailView);

        eventDashboardPresenter.start();

        inOrder.verify(eventDetailView).showProgress(true);
        inOrder.verify(eventDetailView).showProgress(false);
    }

    @Test
    public void shouldHideProgressbarOnAttendeeError() {
        when(databaseChangeListener.getNotifier()).thenReturn(PublishSubject.create());

        when(attendeeRepository.getAttendees(ID, false)).thenReturn(TestUtil.ERROR_OBSERVABLE);

        when(eventRepository.getEvent(ID, false)).thenReturn(Observable.just(EVENT));

        when(eventRepository.getEventStatistics(ID)).thenReturn(Observable.just(EVENT_STATISTICS));

        when(orderRepository.getOrderStatisticsForEvent(ID, false))
                .thenReturn(Observable.just(ORDER_STATISTICS));

        InOrder inOrder = Mockito.inOrder(eventDetailView);

        eventDashboardPresenter.start();

        inOrder.verify(eventDetailView).showProgress(true);
        inOrder.verify(eventDetailView).showProgress(false);
    }

    @Test
    public void shouldHideProgressbarOnEventStatisticsError() {
        when(databaseChangeListener.getNotifier()).thenReturn(PublishSubject.create());

        when(eventRepository.getEventStatistics(ID)).thenReturn(TestUtil.ERROR_OBSERVABLE);

        when(attendeeRepository.getAttendees(ID, false))
                .thenReturn(Observable.fromIterable(ATTENDEES));

        when(eventRepository.getEvent(ID, false)).thenReturn(Observable.just(EVENT));

        when(orderRepository.getOrderStatisticsForEvent(ID, false))
                .thenReturn(Observable.just(ORDER_STATISTICS));

        InOrder inOrder = Mockito.inOrder(eventDetailView);

        eventDashboardPresenter.start();

        inOrder.verify(eventDetailView).showProgress(true);
        inOrder.verify(eventDetailView).showProgress(false);
    }

    @Test
    public void shouldHideProgressbarOnCompleteError() {
        when(databaseChangeListener.getNotifier()).thenReturn(PublishSubject.create());

        when(eventRepository.getEvent(ID, false)).thenReturn(TestUtil.ERROR_OBSERVABLE);

        when(eventRepository.getEventStatistics(ID)).thenReturn(TestUtil.ERROR_OBSERVABLE);

        when(orderRepository.getOrderStatisticsForEvent(ID, false))
                .thenReturn(Observable.just(ORDER_STATISTICS));

        InOrder inOrder = Mockito.inOrder(eventDetailView);

        eventDashboardPresenter.start();

        inOrder.verify(eventDetailView).showProgress(true);
        inOrder.verify(eventDetailView).showProgress(false);
    }

    @Test
    public void shouldHideRefreshLayoutCorrectly() {
        when(attendeeRepository.getAttendees(ID, true))
                .thenReturn(Observable.fromIterable(ATTENDEES));

        when(eventRepository.getEvent(ID, true)).thenReturn(Observable.just(EVENT));

        when(eventRepository.getEventStatistics(ID)).thenReturn(Observable.just(EVENT_STATISTICS));

        when(orderRepository.getOrderStatisticsForEvent(ID, true))
                .thenReturn(Observable.just(ORDER_STATISTICS));

        InOrder inOrder = Mockito.inOrder(eventDetailView);

        eventDashboardPresenter.loadDetails(true);

        inOrder.verify(eventDetailView).showProgress(true);
        inOrder.verify(eventDetailView).onRefreshComplete(true);
        inOrder.verify(eventDetailView).showProgress(false);
    }

    @Test
    public void shouldHideRefreshLayoutOnEventError() {
        when(eventRepository.getEvent(ID, true)).thenReturn(TestUtil.ERROR_OBSERVABLE);

        when(eventRepository.getEventStatistics(ID)).thenReturn(Observable.just(EVENT_STATISTICS));

        when(orderRepository.getOrderStatisticsForEvent(ID, true))
                .thenReturn(Observable.just(ORDER_STATISTICS));

        InOrder inOrder = Mockito.inOrder(eventDetailView);

        eventDashboardPresenter.loadDetails(true);

        inOrder.verify(eventDetailView).showProgress(true);
        inOrder.verify(eventDetailView).onRefreshComplete(false);
        inOrder.verify(eventDetailView).showProgress(false);
    }

    @Test
    public void shouldHideRefreshLayoutOnAttendeeError() {
        when(attendeeRepository.getAttendees(ID, true)).thenReturn(TestUtil.ERROR_OBSERVABLE);

        when(eventRepository.getEvent(ID, true)).thenReturn(Observable.just(EVENT));

        when(eventRepository.getEventStatistics(ID)).thenReturn(Observable.just(EVENT_STATISTICS));

        when(orderRepository.getOrderStatisticsForEvent(ID, true))
                .thenReturn(Observable.just(ORDER_STATISTICS));

        InOrder inOrder = Mockito.inOrder(eventDetailView);

        eventDashboardPresenter.loadDetails(true);

        inOrder.verify(eventDetailView).showProgress(true);
        inOrder.verify(eventDetailView).onRefreshComplete(false);
        inOrder.verify(eventDetailView).showProgress(false);
    }

    @Test
    public void shouldHideRefreshLayoutOnEventStatisticsError() {
        when(eventRepository.getEvent(ID, true)).thenReturn(Observable.just(EVENT));

        when(attendeeRepository.getAttendees(ID, true))
                .thenReturn(Observable.fromIterable(ATTENDEES));

        when(eventRepository.getEventStatistics(ID)).thenReturn(TestUtil.ERROR_OBSERVABLE);

        when(orderRepository.getOrderStatisticsForEvent(ID, true))
                .thenReturn(Observable.just(ORDER_STATISTICS));

        InOrder inOrder = Mockito.inOrder(eventDetailView);

        eventDashboardPresenter.loadDetails(true);

        inOrder.verify(eventDetailView).showProgress(true);
        inOrder.verify(eventDetailView).onRefreshComplete(false);
        inOrder.verify(eventDetailView).showProgress(false);
    }

    @Test
    public void shouldHideRefreshLayoutOnCompleteError() {
        when(eventRepository.getEvent(ID, true)).thenReturn(TestUtil.ERROR_OBSERVABLE);

        when(eventRepository.getEventStatistics(ID)).thenReturn(TestUtil.ERROR_OBSERVABLE);

        when(orderRepository.getOrderStatisticsForEvent(ID, true))
                .thenReturn(TestUtil.ERROR_OBSERVABLE);

        InOrder inOrder = Mockito.inOrder(eventDetailView);

        eventDashboardPresenter.loadDetails(true);

        inOrder.verify(eventDetailView).showProgress(true);
        inOrder.verify(eventDetailView).onRefreshComplete(false);
        inOrder.verify(eventDetailView).showProgress(false);
    }

    @Test
    public void shouldToggleEventStateSuccessfully() {
        when(eventRepository.updateEvent(any(Event.class)))
                .thenReturn(Observable.just(TOGGLED_EVENT));

        eventDashboardPresenter.setEvent(EVENT);
        eventDashboardPresenter.toggleState();

        assertEquals(EVENT.state, TOGGLED_EVENT.state);
    }

    @Test
    public void shouldNotToggleEventStateOnError() {
        when(eventRepository.updateEvent(any(Event.class))).thenReturn(TestUtil.ERROR_OBSERVABLE);

        // Defined locally as a work around for unexpected failureg
        Event event = Event.builder().id(6L).state(Event.STATE_PUBLISHED).build();

        eventDashboardPresenter.setEvent(event);
        eventDashboardPresenter.toggleState();

        assertNotEquals(event.state, Event.STATE_DRAFT);
    }

    @Test
    public void shouldShowSuccessMessageOnToggleSuccess() {
        when(eventRepository.updateEvent(any(Event.class)))
                .thenReturn(Observable.just(TOGGLED_EVENT));

        eventDashboardPresenter.setEvent(EVENT);
        eventDashboardPresenter.toggleState();

        verify(eventDetailView).onSuccess(utilModel.getResourceString(R.string.publish_success));
    }

    @Test
    public void shouldShowErrorMessageOnToggleError() {
        when(eventRepository.updateEvent(any(Event.class))).thenReturn(TestUtil.ERROR_OBSERVABLE);

        eventDashboardPresenter.setEvent(EVENT);
        eventDashboardPresenter.toggleState();

        verify(eventDetailView).showError(anyString());
    }

    @Test
    public void shouldShowProgressbarOnToggle() {
        when(eventRepository.updateEvent(any(Event.class)))
                .thenReturn(Observable.just(TOGGLED_EVENT));

        eventDashboardPresenter.setEvent(EVENT);
        eventDashboardPresenter.toggleState();

        verify(eventDetailView).showProgress(true);
    }

    @Test
    public void shouldHideProgressbarOnToggleSuccess() {
        when(eventRepository.updateEvent(any(Event.class)))
                .thenReturn(Observable.just(TOGGLED_EVENT));

        eventDashboardPresenter.setEvent(EVENT);
        eventDashboardPresenter.toggleState();

        InOrder inOrder = Mockito.inOrder(eventDetailView, eventRepository);

        inOrder.verify(eventRepository).updateEvent(any(Event.class));
        inOrder.verify(eventDetailView).showProgress(true);
        inOrder.verify(eventDetailView).showResult(any(Event.class));
        inOrder.verify(eventDetailView).showProgress(false);
    }

    @Test
    public void shouldHideProgressbarOnToggleError() {
        when(eventRepository.updateEvent(any(Event.class)))
                .thenReturn(Observable.error(Logger.TEST_ERROR));

        eventDashboardPresenter.setEvent(EVENT);
        eventDashboardPresenter.toggleState();

        InOrder inOrder = Mockito.inOrder(eventDetailView, eventRepository);

        inOrder.verify(eventRepository).updateEvent(any(Event.class));
        inOrder.verify(eventDetailView).showProgress(true);
        inOrder.verify(eventDetailView).showResult(any(Event.class));
        inOrder.verify(eventDetailView).showProgress(false);
    }
}
