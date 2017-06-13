package org.fossasia.openevent.app.model;

import org.fossasia.openevent.app.data.EventRepository;
import org.fossasia.openevent.app.data.cache.ObjectCache;
import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.data.db.contract.IDatabaseRepository;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.models.Event_Table;
import org.fossasia.openevent.app.data.models.User;
import org.fossasia.openevent.app.data.network.EventService;
import org.fossasia.openevent.app.utils.Constants;
import org.fossasia.openevent.app.utils.Utils;
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

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * EventModel implementation test with actual ObjectCache
 */
public class EventRepositoryTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private ObjectCache objectCache = ObjectCache.getInstance();

    private EventRepository eventRepository;

    @Mock
    EventService eventService;

    @Mock
    IUtilModel utilModel;

    @Mock
    IDatabaseRepository databaseRepository;

    private String token = "TestToken";
    private String auth = Utils.formatToken(token);

    @Before
    public void setUp() {
        when(utilModel.getToken()).thenReturn(token);

        eventRepository = new EventRepository(utilModel, databaseRepository, ObjectCache.getInstance(), eventService);
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

        when(utilModel.isConnected()).thenReturn(false);
        when(databaseRepository.getAllItems(any())).thenReturn(Observable.empty());
        when(databaseRepository.getItem(any(), any())).thenReturn(Observable.empty());

        eventRepository.getEvents(false)
            .test()
            .assertErrorMessage(Constants.NO_NETWORK);

        eventRepository.getEvents(true)
            .test()
            .assertErrorMessage(Constants.NO_NETWORK);

        eventRepository.getEvent(21, false)
            .test()
            .assertErrorMessage(Constants.NO_NETWORK);

        eventRepository.getEvent(21, true)
            .test()
            .assertErrorMessage(Constants.NO_NETWORK);

        eventRepository.getOrganiser(false)
            .test()
            .assertErrorMessage(Constants.NO_NETWORK);

        eventRepository.getOrganiser(true)
            .test()
            .assertErrorMessage(Constants.NO_NETWORK);

        eventRepository.getAttendees(43, false)
            .test()
            .assertErrorMessage(Constants.NO_NETWORK);

        eventRepository.getAttendees(43, true)
            .test()
            .assertErrorMessage(Constants.NO_NETWORK);

        eventRepository.toggleAttendeeCheckStatus(43, 52)
            .test()
            .assertErrorMessage(Constants.NO_NETWORK);

        Mockito.verifyZeroInteractions(eventService);
    }

    @Test
    public void shouldSaveOrganizerInCache() {
        User user = new User();

        TestObserver testObserver = TestObserver.create();
        Completable completable = Completable.complete()
            .doOnSubscribe(testObserver::onSubscribe);

        when(utilModel.isConnected()).thenReturn(true);
        when(databaseRepository.getAllItems(User.class)).thenReturn(Observable.empty());
        when(databaseRepository.save(user)).thenReturn(completable);
        when(eventService.getUser(auth)).thenReturn(Observable.just(user));

        // No force reload ensures use of cache
        Observable<User> userObservable = eventRepository.getOrganiser(false);

        userObservable
            .test()
            .assertValue(user);

        // Verify loads from network
        verify(utilModel).getToken();
        verify(eventService).getUser(auth);
        testObserver.assertSubscribed();
    }

    @Test
    public void shouldLoadOrganizerFromCache() {
        User user = new User();

        when(databaseRepository.getAllItems(User.class)).thenReturn(Observable.just(user));

        // No force reload ensures use of cache
        Observable<User> userObservable = eventRepository.getOrganiser(false);

        userObservable
            .test()
            .assertValue(user);

        verifyZeroInteractions(eventService);
    }

    @Test
    public void shouldFetchOrganizerOnForceReload() {
        User user = new User();

        when(eventService.getUser(auth)).thenReturn(Observable.just(user));
        when(databaseRepository.save(user)).thenReturn(Completable.complete());
        when(utilModel.isConnected()).thenReturn(true);

        // Force reload ensures no use of cache
        eventRepository.getOrganiser(true).subscribe();

        // Verify loads from network
        verify(eventService).getUser(auth);
        verify(databaseRepository, never()).getAllItems(User.class);
    }

    @Test
    public void shouldSaveEventInCache() {
        long id = 23;

        Event event = new Event();

        TestObserver testObserver = TestObserver.create();
        Completable completable = Completable.complete()
            .doOnSubscribe(testObserver::onSubscribe);

        when(utilModel.isConnected()).thenReturn(true);
        when(databaseRepository.getItem(eq(Event.class), refEq(Event_Table.id.eq(id))))
            .thenReturn(Observable.empty());
        when(databaseRepository.save(event)).thenReturn(completable);
        when(eventService.getEvent(id)).thenReturn(Observable.just(event));

        // No force reload ensures use of cache
        Observable<Event> userObservable = eventRepository.getEvent(23, false);

        userObservable.test()
            .assertSubscribed()
            .assertValue(event)
            .assertValue(Event::isComplete);

        testObserver.assertSubscribed();

        // Verify loads from network
        verify(eventService).getEvent(id);
        verify(databaseRepository).save(event);
    }

    @Test
    public void shouldLoadEventFromCache() {
        long id = 45;

        Event event = new Event();
        event.setComplete(true);
        when(databaseRepository.getItem(eq(Event.class), refEq(Event_Table.id.eq(id))))
            .thenReturn(Observable.just(event));

        // No force reload ensures use of cache
        Observable<Event> eventObservable = eventRepository.getEvent(id, false);
        eventObservable
            .test()
            .assertValue(event);

        verify(databaseRepository).getItem(eq(Event.class), refEq(Event_Table.id.eq(id)));
        verifyZeroInteractions(eventService);
    }

    @Test
    public void shouldFetchEventOnForceReload() {
        long id = 45;

        Event event = new Event();
        when(databaseRepository.save(event)).thenReturn(Completable.complete());
        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.getEvent(id)).thenReturn(Observable.just(event));

        // Force reload ensures no use of cache
        Observable<Event> userObservable = eventRepository.getEvent(id, true);

        userObservable
            .test()
            .assertValue(event);

        // Verify loads from network
        verify(eventService).getEvent(id);
        verify(databaseRepository, never()).getItem(any(), any());
    }

    @Test
    public void shouldSaveEventsInCache() {
        List<Event> events = Arrays.asList(
            new Event(12),
            new Event(21),
            new Event(52)
        );

        TestObserver testObserver = TestObserver.create();
        Completable completable = Completable.complete()
            .doOnSubscribe(testObserver::onSubscribe);

        when(databaseRepository.getAllItems(eq(Event.class)))
            .thenReturn(Observable.empty());
        when(databaseRepository.saveList(Event.class, events)).thenReturn(completable);
        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.getEvents(auth)).thenReturn(Observable.just(events));

        // No force reload ensures use of cache
        Observable<Event> eventObservable = eventRepository.getEvents(false);

        eventObservable
            .toList()
            .test()
            .assertValue(events);

        // Verify loads from network
        verify(utilModel).getToken();
        verify(eventService).getEvents(auth);

        testObserver.assertSubscribed();
    }

    @Test
    public void shouldLoadEventsFromCache() {
        List<Event> events = Arrays.asList(
            new Event(12),
            new Event(21),
            new Event(52)
        );

        when(databaseRepository.getAllItems(eq(Event.class)))
            .thenReturn(Observable.fromIterable(events));
        // No force reload ensures use of cache
        Observable<Event> eventsObservable = eventRepository.getEvents(false);

        eventsObservable
            .toList()
            .test()
            .assertValue(events);

        verify(databaseRepository).getAllItems(eq(Event.class));
        verifyZeroInteractions(eventService);
    }

    @Test
    public void shouldFetchEventsOnForceReload() {
        List<Event> events = Arrays.asList(
            new Event(12),
            new Event(21),
            new Event(52)
        );

        when(utilModel.isConnected()).thenReturn(true);
        when(databaseRepository.saveList(Event.class, events)).thenReturn(Completable.complete());
        when(eventService.getEvents(auth)).thenReturn(Observable.just(events));

        // Force reload ensures no use of cache
        Observable<Event> eventsObservable = eventRepository.getEvents(true);

        eventsObservable
            .toList()
            .test()
            .assertValue(events);

        // Verify loads from network
        verify(eventService).getEvents(auth);
        verify(databaseRepository, never()).getAllItems(eq(Event.class));
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
        Observable<List<Attendee>> attendeesObservable = eventRepository.getAttendees(43, false);

        attendeesObservable.test().assertNoErrors();
        attendeesObservable.test().assertValue(attendees);

        // Verify loads from network
        verify(utilModel).getToken();
        verify(eventService).getAttendees(43, auth);

        List<Attendee> stored = (List<Attendee>) objectCache.getValue(EventRepository.ATTENDEES + 43);
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
        objectCache.saveObject(EventRepository.ATTENDEES + 67, attendees);

        // No force reload ensures use of cache
        Observable<List<Attendee>> attendeeObservable = eventRepository.getAttendees(67, false);

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
        objectCache.saveObject(EventRepository.ATTENDEES + 76, attendees);

        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.getAttendees(23, auth)).thenReturn(Observable.just(attendees));

        // Force reload ensures no use of cache
        Observable<List<Attendee>> attendeeObservable = eventRepository.getAttendees(23, true);

        attendeeObservable.test().assertNoErrors();
        attendeeObservable.test().assertValue(attendees);

        // Verify loads from network
        verify(eventService).getAttendees(23, auth);
    }

    @Test
    public void shouldSaveToggledAttendeeCheck() {
        // Clear cache
        objectCache.clear();

        List<Attendee> attendees = Arrays.asList(
            new Attendee(12),
            new Attendee(89),
            new Attendee(64)
        );

        Attendee attendee = attendees.get(1);

        attendee.setCheckedIn(false);

        objectCache.saveObject(EventRepository.ATTENDEES + 76, attendees);

        attendee.setCheckedIn(true);

        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.toggleAttendeeCheckStatus(76, 89, auth)).thenReturn(Observable.just(attendee));

        Observable<Attendee> attendeeObservable = eventRepository.toggleAttendeeCheckStatus(76, 89);

        attendeeObservable.test().assertNoErrors();
        attendeeObservable.test().assertValue((Attendee::isCheckedIn));

        // Verify loads from network
        verify(eventService).toggleAttendeeCheckStatus(76, 89, auth);

        // Verify correct caching
        List<Attendee> stored = (List<Attendee>) objectCache.getValue(EventRepository.ATTENDEES + 76);
        assertEquals(stored.get(1).getId(), attendee.getId());
        assertEquals(stored.get(1).isCheckedIn(), attendee.isCheckedIn());
    }

}
