package org.fossasia.openevent.app.presenter;

import org.fossasia.openevent.app.data.contract.IEventDataRepository;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.models.Ticket;
import org.fossasia.openevent.app.event.detail.EventDetailsPresenter;
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
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class EventDetailPresenterTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    IEventDetailView eventDetailView;

    @Mock
    IEventDataRepository eventModel;

    private final int id = 42;
    private EventDetailsPresenter eventDetailActivityPresenter;

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

        eventDetailActivityPresenter = new EventDetailsPresenter(event, eventDetailView, eventModel);

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
        when(eventModel.getAttendees(id, false))
            .thenReturn(Observable.just(attendees));

        when(eventModel.getEvent(id, false))
            .thenReturn(Observable.just(event));

        eventDetailActivityPresenter.attach();

        verify(eventModel).getEvent(id, false);
        verify(eventModel).getAttendees(id, false);
    }

    @Test
    public void shouldDetachViewOnStop() {
        when(eventModel.getAttendees(id, false))
            .thenReturn(Observable.just(attendees));

        when(eventModel.getEvent(id, false))
            .thenReturn(Observable.just(event));

        eventDetailActivityPresenter.attach();

        assertNotNull(eventDetailActivityPresenter.getView());

        eventDetailActivityPresenter.detach();

        assertNull(eventDetailActivityPresenter.getView());
    }

    @Test
    public void shouldShowEventError() {
        String error = "Test Error";
        when(eventModel.getEvent(id, false))
            .thenReturn(Observable.error(new Throwable(error)));

        InOrder inOrder = Mockito.inOrder(eventModel, eventDetailView);

        eventDetailActivityPresenter.loadTickets(id, false);

        inOrder.verify(eventDetailView).showProgressBar(true);
        inOrder.verify(eventModel).getEvent(id, false);
        inOrder.verify(eventDetailView).showEventLoadError(error);
        inOrder.verify(eventDetailView).showProgressBar(false);
    }

    @Test
    public void shouldLoadEventSuccessfully() {
        when(eventModel.getEvent(id, false))
            .thenReturn(Observable.just(event));

        InOrder inOrder = Mockito.inOrder(eventModel, eventDetailView);

        eventDetailActivityPresenter.loadTickets(id, false);

        inOrder.verify(eventDetailView).showProgressBar(true);
        inOrder.verify(eventModel).getEvent(id, false);
        inOrder.verify(eventDetailView).showEventName("Event Name");
        inOrder.verify(eventDetailView).showDates("2004-05-21", "2012-09-20");
        inOrder.verify(eventDetailView).showTime("12:23:00");
        inOrder.verify(eventDetailView).showTicketStats(0, 114);
        inOrder.verify(eventDetailView).showProgressBar(false);
    }

    @Test
    public void shouldShowAttendeeError() {
        String error = "Test Error";
        when(eventModel.getAttendees(id, false))
            .thenReturn(Observable.error(new Throwable(error)));

        InOrder inOrder = Mockito.inOrder(eventModel, eventDetailView);

        eventDetailActivityPresenter.loadAttendees(id, false);

        inOrder.verify(eventModel).getAttendees(id, false);
        inOrder.verify(eventDetailView).showEventLoadError(error);
    }

    @Test
    public void shouldLoadAttendeesSuccessfully() {
        when(eventModel.getAttendees(id, false))
            .thenReturn(Observable.just(attendees));

        InOrder inOrder = Mockito.inOrder(eventModel, eventDetailView);

        eventDetailActivityPresenter.loadAttendees(id, false);

        inOrder.verify(eventModel).getAttendees(id, false);
        inOrder.verify(eventDetailView).showAttendeeStats(3, 7);
    }

    @Test
    public void shouldDisplayCorrectStats() {
        when(eventModel.getAttendees(id, false))
            .thenReturn(Observable.just(attendees));

        when(eventModel.getEvent(id, false))
            .thenReturn(Observable.just(event));

        // Load all info
        eventDetailActivityPresenter.attach();

        Mockito.verify(eventDetailView, atLeastOnce()).showTicketStats(7, 114);
        Mockito.verify(eventDetailView, atLeastOnce()).showAttendeeStats(3, 7);
    }

    @Test
    public void shouldDisplayNormalizedAttendeeStats() {
        when(eventModel.getAttendees(id, false))
            .thenReturn(Observable.just(Collections.emptyList()));

        when(eventModel.getEvent(id, false))
            .thenReturn(Observable.just(event));

        // Load all info
        eventDetailActivityPresenter.attach();

        Mockito.verify(eventDetailView, atLeastOnce()).showTicketStats(0, 114);
        Mockito.verify(eventDetailView, atLeastOnce()).showAttendeeStats(0, 0);
    }

    @Test
    public void shouldDisplayNormalizedTicketStats() {
        when(eventModel.getAttendees(id, false))
            .thenReturn(Observable.just(attendees));

        event.setTickets(null);
        when(eventModel.getEvent(id, false))
            .thenReturn(Observable.just(event));

        // Load all info
        eventDetailActivityPresenter.attach();

        Mockito.verify(eventDetailView, atLeastOnce()).showTicketStats(0, 0);
        Mockito.verify(eventDetailView, atLeastOnce()).showAttendeeStats(3, 7);
    }

    @Test
    public void shouldNotAccessView() {
        eventDetailActivityPresenter.detach();

        eventDetailActivityPresenter.loadTickets(id, false);
        eventDetailActivityPresenter.loadAttendees(id, false);

        Mockito.verifyZeroInteractions(eventDetailView);
    }
}
