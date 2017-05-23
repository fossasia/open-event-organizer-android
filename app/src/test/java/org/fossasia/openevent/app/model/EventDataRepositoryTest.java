package org.fossasia.openevent.app.model;

import org.fossasia.openevent.app.data.EventDataRepository;
import org.fossasia.openevent.app.data.cache.ObjectCache;
import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.models.User;
import org.fossasia.openevent.app.data.network.EventService;
import org.fossasia.openevent.app.data.network.NetworkService;
import org.fossasia.openevent.app.utils.Constants;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * EventModel implementation test with actual ObjectCache
 */
public class EventDataRepositoryTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private ObjectCache objectCache = ObjectCache.getInstance();

    private EventDataRepository retrofitEventModel;

    @Mock
    EventService eventService;

    @Mock
    IUtilModel utilModel;

    private String token = "TestToken";
    private String auth = NetworkService.formatToken(token);

    @Before
    public void setUp() {
        when(utilModel.getToken()).thenReturn(token);

        retrofitEventModel = new EventDataRepository(utilModel);
        retrofitEventModel.setEventService(eventService);
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void testFormatToken() {
        assertEquals(auth, "JWT " + token);
    }

    @Test
    public void shouldSendErrorOnNetworkDown() {
        // Clear cache
        objectCache.clear();

        Mockito.when(utilModel.isConnected()).thenReturn(false);

        retrofitEventModel.getEvents(false)
            .test()
            .assertErrorMessage(Constants.NO_NETWORK);

        retrofitEventModel.getEvents(true)
            .test()
            .assertErrorMessage(Constants.NO_NETWORK);

        retrofitEventModel.getEvent(21, false)
            .test()
            .assertErrorMessage(Constants.NO_NETWORK);

        retrofitEventModel.getEvent(21, true)
            .test()
            .assertErrorMessage(Constants.NO_NETWORK);

        retrofitEventModel.getOrganiser(false)
            .test()
            .assertErrorMessage(Constants.NO_NETWORK);

        retrofitEventModel.getOrganiser(true)
            .test()
            .assertErrorMessage(Constants.NO_NETWORK);


        Mockito.verifyNoMoreInteractions(eventService);
    }


    @Test
    public void shouldSaveOrganizerInCache() {
        // Clear cache
        objectCache.clear();

        User user = new User();

        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.getUser(auth)).thenReturn(Observable.just(user));

        // No force reload ensures use of cache
        Observable<User> userObservable = retrofitEventModel.getOrganiser(false);

        userObservable.test().assertNoErrors();
        userObservable.test().assertValue(user);

        // Verify loads from network
        verify(utilModel).getToken();
        verify(eventService).getUser(auth);

        User stored = (User) objectCache.getValue(EventDataRepository.ORGANIZER);
        assertEquals(stored, user);
    }

    @Test
    public void shouldLoadOrganizerFromCache() {
        // Clear cache
        objectCache.clear();

        User user = new User();
        objectCache.saveObject(EventDataRepository.ORGANIZER, user);

        // No force reload ensures use of cache
        Observable<User> userObservable = retrofitEventModel.getOrganiser(false);

        userObservable.test().assertNoErrors();
        userObservable.test().assertValue(user);

        verifyZeroInteractions(eventService);
    }

    @Test
    public void shouldFetchOrganizerOnForceReload() {
        // Clear cache
        objectCache.clear();

        User user = new User();
        objectCache.saveObject(EventDataRepository.ORGANIZER, user);

        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.getUser(auth)).thenReturn(Observable.just(user));

        // Force reload ensures no use of cache
        Observable<User> userObservable = retrofitEventModel.getOrganiser(true);

        userObservable.test().assertNoErrors();
        userObservable.test().assertValue(user);

        // Verify loads from network
        verify(eventService).getUser(auth);
    }

    @Test
    public void shouldSaveEventInCache() {
        int id = 23;

        // Clear cache
        objectCache.clear();

        Event event = new Event();

        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.getEvent(id)).thenReturn(Observable.just(event));

        // No force reload ensures use of cache
        Observable<Event> userObservable = retrofitEventModel.getEvent(23, false);

        userObservable.test().assertNoErrors();
        userObservable.test().assertValue(event);

        // Verify loads from network
        verify(eventService).getEvent(id);

        Event stored = (Event) objectCache.getValue(EventDataRepository.EVENT + id);
        assertEquals(stored, event);
    }

    @Test
    public void shouldLoadEventFromCache() {
        int id = 45;

        // Clear cache
        objectCache.clear();

        Event event = new Event();
        objectCache.saveObject(EventDataRepository.EVENT + id, event);

        // No force reload ensures use of cache
        Observable<Event> userObservable = retrofitEventModel.getEvent(id, false);

        userObservable.test().assertNoErrors();
        userObservable.test().assertValue(event);

        verifyZeroInteractions(eventService);
    }

    @Test
    public void shouldFetchEventOnForceReload() {
        int id = 45;

        // Clear cache
        objectCache.clear();

        Event event = new Event();
        objectCache.saveObject(EventDataRepository.EVENT + id, event);

        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.getEvent(id)).thenReturn(Observable.just(event));

        // Force reload ensures no use of cache
        Observable<Event> userObservable = retrofitEventModel.getEvent(id, true);

        userObservable.test().assertNoErrors();
        userObservable.test().assertValue(event);

        // Verify loads from network
        verify(eventService).getEvent(id);
    }

    @Test
    public void shouldSaveEventsInCache() {
        // Clear cache
        objectCache.clear();

        List<Event> events = Arrays.asList(
            new Event(12),
            new Event(21),
            new Event(52)
        );

        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.getEvents(auth)).thenReturn(Observable.just(events));

        // No force reload ensures use of cache
        Observable<List<Event>> userObservable = retrofitEventModel.getEvents(false);

        userObservable.test().assertNoErrors();
        userObservable.test().assertValue(events);

        // Verify loads from network
        verify(utilModel).getToken();
        verify(eventService).getEvents(auth);

        List<Event> stored = (List<Event>) objectCache.getValue(EventDataRepository.EVENTS);
        assertEquals(stored, events);
    }

    @Test
    public void shouldLoadEventsFromCache() {
        // Clear cache
        objectCache.clear();

        List<Event> events = Arrays.asList(
            new Event(12),
            new Event(21),
            new Event(52)
        );
        objectCache.saveObject(EventDataRepository.EVENTS, events);

        // No force reload ensures use of cache
        Observable<List<Event>> eventsObservable = retrofitEventModel.getEvents(false);

        eventsObservable.test().assertNoErrors();
        eventsObservable.test().assertValue(events);

        verifyZeroInteractions(eventService);
    }

    @Test
    public void shouldFetchEventsOnForceReload() {
        // Clear cache
        objectCache.clear();

        List<Event> events = Arrays.asList(
            new Event(12),
            new Event(21),
            new Event(52)
        );
        objectCache.saveObject(EventDataRepository.EVENTS, events);

        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.getEvents(auth)).thenReturn(Observable.just(events));

        // Force reload ensures no use of cache
        Observable<List<Event>> eventsObservable = retrofitEventModel.getEvents(true);

        eventsObservable.test().assertNoErrors();
        eventsObservable.test().assertValue(events);

        // Verify loads from network
        verify(eventService).getEvents(auth);
    }

    @Test
    public void shouldSaveAttendeesInCache() {
        // Clear cache
        objectCache.clear();

        List<Attendee> attendees = Arrays.asList(
            new Attendee(),
            new Attendee(),
            new Attendee()
        );

        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.getAttendees(43, auth)).thenReturn(Observable.just(attendees));

        // No force reload ensures use of cache
        Observable<List<Attendee>> attendeesObservable = retrofitEventModel.getAttendees(43, false);

        attendeesObservable.test().assertNoErrors();
        attendeesObservable.test().assertValue(attendees);

        // Verify loads from network
        verify(utilModel).getToken();
        verify(eventService).getAttendees(43, auth);

        List<Attendee> stored = (List<Attendee>) objectCache.getValue(EventDataRepository.ATTENDEES + 43);
        assertEquals(stored, attendees);
    }

    @Test
    public void shouldLoadAttendeesFromCache() {
        // Clear cache
        objectCache.clear();

        List<Attendee> attendees = Arrays.asList(
            new Attendee(),
            new Attendee(),
            new Attendee()
        );
        objectCache.saveObject(EventDataRepository.ATTENDEES + 67, attendees);

        // No force reload ensures use of cache
        Observable<List<Attendee>> attendeeObservable = retrofitEventModel.getAttendees(67, false);

        attendeeObservable.test().assertNoErrors();
        attendeeObservable.test().assertValue(attendees);

        verifyZeroInteractions(eventService);
    }

    @Test
    public void shouldFetchAttendeesOnForceReload() {
        // Clear cache
        objectCache.clear();

        List<Attendee> attendees = Arrays.asList(
            new Attendee(),
            new Attendee(),
            new Attendee()
        );
        objectCache.saveObject(EventDataRepository.ATTENDEES + 76, attendees);

        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.getAttendees(23, auth)).thenReturn(Observable.just(attendees));

        // Force reload ensures no use of cache
        Observable<List<Attendee>> attendeeObservable = retrofitEventModel.getAttendees(23, true);

        attendeeObservable.test().assertNoErrors();
        attendeeObservable.test().assertValue(attendees);

        // Verify loads from network
        verify(eventService).getAttendees(23, auth);
    }

}
