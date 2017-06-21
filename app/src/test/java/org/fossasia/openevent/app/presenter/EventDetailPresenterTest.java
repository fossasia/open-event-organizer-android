package org.fossasia.openevent.app.presenter;

import org.fossasia.openevent.app.data.contract.IEventRepository;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.models.Ticket;
import org.fossasia.openevent.app.event.detail.EventDetailPresenter;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeast;
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

    private final int id = 42;
    private EventDetailPresenter eventDetailPresenter;

    private Event event = new Event(42);

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

        eventDetailPresenter = new EventDetailPresenter(eventRepository);
        eventDetailPresenter.attach(eventDetailView, event.getId());

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
        when(eventRepository.getAttendees(id, false))
            .thenReturn(Observable.fromIterable(attendees));

        when(eventRepository.getEvent(id, false))
            .thenReturn(Observable.just(event));

        eventDetailPresenter.start();

        verify(eventRepository).getEvent(id, false);
        verify(eventRepository).getAttendees(id, false);
    }

    @Test
    public void shouldDetachViewOnStop() {
        when(eventRepository.getAttendees(id, false))
            .thenReturn(Observable.fromIterable(attendees));

        when(eventRepository.getEvent(id, false))
            .thenReturn(Observable.just(event));

        eventDetailPresenter.start();

        assertNotNull(eventDetailPresenter.getView());

        eventDetailPresenter.detach();

        assertNull(eventDetailPresenter.getView());
    }

    @Test
    public void shouldShowEventError() {
        String error = "Test Error";
        when(eventRepository.getEvent(id, false))
            .thenReturn(Observable.error(new Throwable(error)));

        InOrder inOrder = Mockito.inOrder(eventRepository, eventDetailView);

        eventDetailPresenter.loadTickets(id, false);

        inOrder.verify(eventRepository).getEvent(id, false);
        inOrder.verify(eventDetailView).showEventLoadError(error);
    }

    @Test
    public void shouldLoadEventSuccessfully() {
        when(eventRepository.getEvent(id, false))
            .thenReturn(Observable.just(event));

        InOrder inOrder = Mockito.inOrder(eventRepository, eventDetailView);

        eventDetailPresenter.loadTickets(id, false);

        inOrder.verify(eventRepository).getEvent(id, false);
        inOrder.verify(eventDetailView).showEvent(event);

        assertEquals("2004-05-21", event.startDate.get());
        assertEquals("2012-09-20", event.endDate.get());
        assertEquals("12:23:00", event.eventStartTime.get());
        assertEquals(114, event.totalTickets.get());
    }

    @Test
    public void shouldShowAttendeeError() {
        String error = "Test Error";
        when(eventRepository.getAttendees(id, false))
            .thenReturn(Observable.error(new Throwable(error)));

        InOrder inOrder = Mockito.inOrder(eventRepository, eventDetailView);

        eventDetailPresenter.loadAttendees(id, false);

        inOrder.verify(eventRepository).getAttendees(id, false);
        inOrder.verify(eventDetailView).showEventLoadError(error);
    }

    @Test
    public void shouldDisplayCorrectStats() throws Exception {
        when(eventRepository.getAttendees(id, false))
            .thenReturn(Observable.fromIterable(attendees));

        when(eventRepository.getEvent(id, false))
            .thenReturn(Observable.just(event));

        // Load all info
        eventDetailPresenter.start();

        assertEquals(114, event.totalTickets.get());
        assertEquals(3, event.checkedIn.get());
        assertEquals(7, event.totalAttendees.get());
    }

    @Test
    public void shouldNotAccessView() {
        eventDetailPresenter.detach();

        eventDetailPresenter.loadTickets(id, false);
        eventDetailPresenter.loadAttendees(id, false);

        verifyZeroInteractions(eventDetailView);
    }

    @Test
    public void shouldHideProgressbarCorrectly() {
        when(eventRepository.getAttendees(id, false))
            .thenReturn(Observable.fromIterable(attendees));

        when(eventRepository.getEvent(id, false))
            .thenReturn(Observable.just(event));

        InOrder inOrder = Mockito.inOrder(eventDetailView);

        eventDetailPresenter.start();

        assertTrue(eventDetailPresenter.getProgress() >= 2);

        inOrder.verify(eventDetailView, atLeast(1)).showProgressBar(true);
        inOrder.verify(eventDetailView).showProgressBar(false);
    }
}
