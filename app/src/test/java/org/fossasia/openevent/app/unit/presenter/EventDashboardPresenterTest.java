package org.fossasia.openevent.app.unit.presenter;

import org.fossasia.openevent.app.common.app.rx.Logger;
import org.fossasia.openevent.app.common.data.models.Attendee;
import org.fossasia.openevent.app.common.data.models.Event;
import org.fossasia.openevent.app.common.data.models.Ticket;
import org.fossasia.openevent.app.common.data.repository.contract.IAttendeeRepository;
import org.fossasia.openevent.app.common.data.repository.contract.IEventRepository;
import org.fossasia.openevent.app.module.event.dashboard.EventDashboardPresenter;
import org.fossasia.openevent.app.module.event.dashboard.analyser.ChartAnalyser;
import org.fossasia.openevent.app.module.event.dashboard.analyser.TicketAnalyser;
import org.fossasia.openevent.app.module.event.dashboard.contract.IEventDashboardView;
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

import java.util.Arrays;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.fossasia.openevent.app.unit.presenter.Util.ERROR_OBSERVABLE;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class EventDashboardPresenterTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock private IEventDashboardView eventDetailView;
    @Mock private IEventRepository eventRepository;
    @Mock private TicketAnalyser ticketAnalyser;
    @Mock private IAttendeeRepository attendeeRepository;
    @Mock private ChartAnalyser chartAnalyser;

    private static final int ID = 42;
    private EventDashboardPresenter eventDashboardPresenter;

    private Event event = new Event(ID);

    private List<Attendee> attendees = Arrays.asList(
        new Attendee(false),
        new Attendee(true),
        new Attendee(false),
        new Attendee(false),
        new Attendee(true),
        new Attendee(true),
        new Attendee(false)
    );

    private List<Ticket> tickets = Arrays.asList(
        new Ticket(1, 21),
        new Ticket(2, 50),
        new Ticket(3, 43));

    @Before
    public void setUp() {
        // Event set up
        event.setName("Event Name");
        event.setStartsAt("2004-05-21T9:30:00");
        event.setEndsAt("2012-09-20T12:23:00");
        event.setTickets(tickets);

        eventDashboardPresenter = new EventDashboardPresenter(eventRepository, attendeeRepository, ticketAnalyser, chartAnalyser);

        eventDashboardPresenter.attach(event.getId(), eventDetailView);

        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());

        when(chartAnalyser.loadData(event.getId())).thenReturn(Completable.complete());
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldLoadEventAndAttendeesAutomatically() {
        when(attendeeRepository.getAttendees(ID, false))
            .thenReturn(Observable.fromIterable(attendees));

        when(eventRepository.getEvent(ID, false))
            .thenReturn(Observable.just(event));

        eventDashboardPresenter.start();

        verify(eventRepository).getEvent(ID, false);
        verify(attendeeRepository).getAttendees(ID, false);
    }

    @Test
    public void shouldDetachViewOnStop() {
        assertNotNull(eventDashboardPresenter.getView());

        eventDashboardPresenter.detach();

        eventDashboardPresenter.start();
        eventDashboardPresenter.loadDetails(true);

        assertNull(eventDashboardPresenter.getView());
    }

    @Test
    public void shouldShowEventError() {
        when(eventRepository.getEvent(ID, false))
            .thenReturn(ERROR_OBSERVABLE);

        eventDashboardPresenter.loadDetails(false);

        verify(eventDetailView).showError(Logger.TEST_MESSAGE);
    }

    @Test
    public void shouldLoadEventSuccessfully() {
        when(eventRepository.getEvent(ID, false))
            .thenReturn(Observable.just(event));
        when(attendeeRepository.getAttendees(ID, false))
            .thenReturn(ERROR_OBSERVABLE);

        eventDashboardPresenter.loadDetails(false);

        verify(eventDetailView).showResult(event);
        verify(ticketAnalyser).analyseTotalTickets(event);
    }

    @Test
    public void shouldShowAttendeeError() {
        when(eventRepository.getEvent(ID, false))
            .thenReturn(Observable.just(event));
        when(attendeeRepository.getAttendees(ID, false))
            .thenReturn(ERROR_OBSERVABLE);

        eventDashboardPresenter.loadDetails(false);

        verify(eventDetailView).showError(Logger.TEST_MESSAGE);
    }

    @Test
    public void shouldLoadAttendeesSuccessfully() {
        when(attendeeRepository.getAttendees(ID, false))
            .thenReturn(Observable.fromIterable(attendees));
        when(eventRepository.getEvent(ID, false))
            .thenReturn(Observable.just(event));

        eventDashboardPresenter.start();

        verify(ticketAnalyser).analyseSoldTickets(event, attendees);
    }

    @Test
    public void shouldNotAccessView() {
        eventDashboardPresenter.detach();

        eventDashboardPresenter.loadDetails(false);

        verifyZeroInteractions(eventDetailView);
    }

    @Test
    public void shouldHideProgressbarCorrectly() {
        when(attendeeRepository.getAttendees(ID, false))
            .thenReturn(Observable.fromIterable(attendees));

        when(eventRepository.getEvent(ID, false))
            .thenReturn(Observable.just(event));

        InOrder inOrder = Mockito.inOrder(eventDetailView);

        eventDashboardPresenter.start();

        inOrder.verify(eventDetailView).showProgress(true);
        inOrder.verify(eventDetailView).showProgress(false);
    }

    @Test
    public void shouldHideProgressbarOnEventError() {
        when(eventRepository.getEvent(ID, false))
            .thenReturn(ERROR_OBSERVABLE);

        InOrder inOrder = Mockito.inOrder(eventDetailView);

        eventDashboardPresenter.start();

        inOrder.verify(eventDetailView).showProgress(true);
        inOrder.verify(eventDetailView).showProgress(false);
    }

    @Test
    public void shouldHideProgressbarOnAttendeeError() {
        when(attendeeRepository.getAttendees(ID, false))
            .thenReturn(ERROR_OBSERVABLE);

        when(eventRepository.getEvent(ID, false))
            .thenReturn(Observable.just(event));

        InOrder inOrder = Mockito.inOrder(eventDetailView);

        eventDashboardPresenter.start();

        inOrder.verify(eventDetailView).showProgress(true);
        inOrder.verify(eventDetailView).showProgress(false);
    }

    @Test
    public void shouldHideProgressbarOnCompleteError() {
        when(eventRepository.getEvent(ID, false))
            .thenReturn(ERROR_OBSERVABLE);

        InOrder inOrder = Mockito.inOrder(eventDetailView);

        eventDashboardPresenter.start();

        inOrder.verify(eventDetailView).showProgress(true);
        inOrder.verify(eventDetailView).showProgress(false);
    }

    @Test
    public void shouldHideRefreshLayoutCorrectly() {
        when(attendeeRepository.getAttendees(ID, true))
            .thenReturn(Observable.fromIterable(attendees));

        when(eventRepository.getEvent(ID, true))
            .thenReturn(Observable.just(event));

        InOrder inOrder = Mockito.inOrder(eventDetailView);

        eventDashboardPresenter.loadDetails(true);

        inOrder.verify(eventDetailView).showProgress(true);
        inOrder.verify(eventDetailView).onRefreshComplete();
        inOrder.verify(eventDetailView).showProgress(false);
    }

    @Test
    public void shouldHideRefreshLayoutOnEventError() {
        when(eventRepository.getEvent(ID, true))
            .thenReturn(ERROR_OBSERVABLE);

        InOrder inOrder = Mockito.inOrder(eventDetailView);

        eventDashboardPresenter.loadDetails(true);

        inOrder.verify(eventDetailView).showProgress(true);
        inOrder.verify(eventDetailView).onRefreshComplete();
        inOrder.verify(eventDetailView).showProgress(false);
    }

    @Test
    public void shouldHideRefreshLayoutOnAttendeeError() {
        when(attendeeRepository.getAttendees(ID, true))
            .thenReturn(ERROR_OBSERVABLE);

        when(eventRepository.getEvent(ID, true))
            .thenReturn(Observable.just(event));

        InOrder inOrder = Mockito.inOrder(eventDetailView);

        eventDashboardPresenter.loadDetails(true);

        inOrder.verify(eventDetailView).showProgress(true);
        inOrder.verify(eventDetailView).onRefreshComplete();
        inOrder.verify(eventDetailView).showProgress(false);
    }

    @Test
    public void shouldHideRefreshLayoutOnCompleteError() {
        when(eventRepository.getEvent(ID, true))
            .thenReturn(ERROR_OBSERVABLE);

        InOrder inOrder = Mockito.inOrder(eventDetailView);

        eventDashboardPresenter.loadDetails(true);

        inOrder.verify(eventDetailView).showProgress(true);
        inOrder.verify(eventDetailView).onRefreshComplete();
        inOrder.verify(eventDetailView).showProgress(false);
    }
}
