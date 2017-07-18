package org.fossasia.openevent.app.presenter;

import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.repository.contract.IAttendeeRepository;
import org.fossasia.openevent.app.data.repository.contract.IEventRepository;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.models.Ticket;
import org.fossasia.openevent.app.event.detail.EventDetailPresenter;
import org.fossasia.openevent.app.event.detail.TicketAnalyser;
import org.fossasia.openevent.app.event.detail.contract.IEventDetailView;
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

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.fossasia.openevent.app.presenter.Util.ERROR_OBSERVABLE;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class EventDetailPresenterTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    IEventDetailView eventDetailView;

    @Mock
    IEventRepository eventRepository;

    @Mock
    TicketAnalyser ticketAnalyser;

    @Mock
    IAttendeeRepository attendeeRepository;

    private final int id = 42;
    private EventDetailPresenter eventDetailPresenter;

    private Event event = new Event(id);

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
        event.setStartTime("2004-05-21T9:30:00");
        event.setEndTime("2012-09-20T12:23:00");
        event.setTickets(tickets);

        eventDetailPresenter = new EventDetailPresenter(eventRepository, attendeeRepository, ticketAnalyser);

        eventDetailPresenter.attach(eventDetailView);
        eventDetailPresenter.attachKey(event.getId());

        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldLoadEventAndAttendeesAutomatically() {
        when(attendeeRepository.getAttendees(id, false))
            .thenReturn(Observable.fromIterable(attendees));

        when(eventRepository.getEvent(id, false))
            .thenReturn(Observable.just(event));

        eventDetailPresenter.start();

        verify(eventRepository).getEvent(id, false);
        verify(attendeeRepository).getAttendees(id, false);
    }

    @Test
    public void shouldDetachViewOnStop() {
        assertNotNull(eventDetailPresenter.getView());

        eventDetailPresenter.detach();

        eventDetailPresenter.start();
        eventDetailPresenter.loadDetails(true);

        assertNull(eventDetailPresenter.getView());
    }

    @Test
    public void shouldShowEventError() {
        when(eventRepository.getEvent(id, false))
            .thenReturn(ERROR_OBSERVABLE);

        eventDetailPresenter.loadDetails(false);

        verify(eventDetailView).showError(Logger.TEST_MESSAGE);
    }

    @Test
    public void shouldLoadEventSuccessfully() {
        when(eventRepository.getEvent(id, false))
            .thenReturn(Observable.just(event));
        when(attendeeRepository.getAttendees(id, false))
            .thenReturn(ERROR_OBSERVABLE);

        eventDetailPresenter.loadDetails(false);

        verify(eventDetailView).showResult(event);
        verify(ticketAnalyser).analyseTotalTickets(event);
    }

    @Test
    public void shouldShowAttendeeError() {
        when(eventRepository.getEvent(id, false))
            .thenReturn(Observable.just(event));
        when(attendeeRepository.getAttendees(id, false))
            .thenReturn(ERROR_OBSERVABLE);

        eventDetailPresenter.loadDetails(false);

        verify(eventDetailView).showError(Logger.TEST_MESSAGE);
    }

    @Test
    public void shouldLoadAttendeesSuccessfully() {
        when(attendeeRepository.getAttendees(id, false))
            .thenReturn(Observable.fromIterable(attendees));
        when(eventRepository.getEvent(id, false))
            .thenReturn(Observable.just(event));

        eventDetailPresenter.start();

        verify(ticketAnalyser).analyseSoldTickets(event, attendees);
    }

    @Test
    public void shouldNotAccessView() {
        eventDetailPresenter.detach();

        eventDetailPresenter.loadDetails(false);

        verifyZeroInteractions(eventDetailView);
    }

    @Test
    public void shouldHideProgressbarCorrectly() {
        when(attendeeRepository.getAttendees(id, false))
            .thenReturn(Observable.fromIterable(attendees));

        when(eventRepository.getEvent(id, false))
            .thenReturn(Observable.just(event));

        InOrder inOrder = Mockito.inOrder(eventDetailView);

        eventDetailPresenter.start();

        inOrder.verify(eventDetailView).showProgress(true);
        inOrder.verify(eventDetailView).showProgress(false);
    }

    @Test
    public void shouldHideProgressbarOnEventError() {
        when(eventRepository.getEvent(id, false))
            .thenReturn(ERROR_OBSERVABLE);

        InOrder inOrder = Mockito.inOrder(eventDetailView);

        eventDetailPresenter.start();

        inOrder.verify(eventDetailView).showProgress(true);
        inOrder.verify(eventDetailView).showProgress(false);
    }

    @Test
    public void shouldHideProgressbarOnAttendeeError() {
        when(attendeeRepository.getAttendees(id, false))
            .thenReturn(ERROR_OBSERVABLE);

        when(eventRepository.getEvent(id, false))
            .thenReturn(Observable.just(event));

        InOrder inOrder = Mockito.inOrder(eventDetailView);

        eventDetailPresenter.start();

        inOrder.verify(eventDetailView).showProgress(true);
        inOrder.verify(eventDetailView).showProgress(false);
    }

    @Test
    public void shouldHideProgressbarOnCompleteError() {
        when(eventRepository.getEvent(id, false))
            .thenReturn(ERROR_OBSERVABLE);

        InOrder inOrder = Mockito.inOrder(eventDetailView);

        eventDetailPresenter.start();

        inOrder.verify(eventDetailView).showProgress(true);
        inOrder.verify(eventDetailView).showProgress(false);
    }

    @Test
    public void shouldHideRefreshLayoutCorrectly() {
        when(attendeeRepository.getAttendees(id, true))
            .thenReturn(Observable.fromIterable(attendees));

        when(eventRepository.getEvent(id, true))
            .thenReturn(Observable.just(event));

        InOrder inOrder = Mockito.inOrder(eventDetailView);

        eventDetailPresenter.loadDetails(true);

        inOrder.verify(eventDetailView).showProgress(true);
        inOrder.verify(eventDetailView).onRefreshComplete();
        inOrder.verify(eventDetailView).showProgress(false);
    }

    @Test
    public void shouldHideRefreshLayoutOnEventError() {
        when(eventRepository.getEvent(id, true))
            .thenReturn(ERROR_OBSERVABLE);

        InOrder inOrder = Mockito.inOrder(eventDetailView);

        eventDetailPresenter.loadDetails(true);

        inOrder.verify(eventDetailView).showProgress(true);
        inOrder.verify(eventDetailView).onRefreshComplete();
        inOrder.verify(eventDetailView).showProgress(false);
    }

    @Test
    public void shouldHideRefreshLayoutOnAttendeeError() {
        when(attendeeRepository.getAttendees(id, true))
            .thenReturn(ERROR_OBSERVABLE);

        when(eventRepository.getEvent(id, true))
            .thenReturn(Observable.just(event));

        InOrder inOrder = Mockito.inOrder(eventDetailView);

        eventDetailPresenter.loadDetails(true);

        inOrder.verify(eventDetailView).showProgress(true);
        inOrder.verify(eventDetailView).onRefreshComplete();
        inOrder.verify(eventDetailView).showProgress(false);
    }

    @Test
    public void shouldHideRefreshLayoutOnCompleteError() {
        when(eventRepository.getEvent(id, true))
            .thenReturn(ERROR_OBSERVABLE);

        InOrder inOrder = Mockito.inOrder(eventDetailView);

        eventDetailPresenter.loadDetails(true);

        inOrder.verify(eventDetailView).showProgress(true);
        inOrder.verify(eventDetailView).onRefreshComplete();
        inOrder.verify(eventDetailView).showProgress(false);
    }
}
