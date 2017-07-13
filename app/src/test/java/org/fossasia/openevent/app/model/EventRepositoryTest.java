package org.fossasia.openevent.app.model;

import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.data.db.contract.IDatabaseRepository;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.models.Event_Table;
import org.fossasia.openevent.app.data.models.User;
import org.fossasia.openevent.app.data.network.EventService;
import org.fossasia.openevent.app.data.repository.EventRepository;
import org.fossasia.openevent.app.utils.Constants;
import org.fossasia.openevent.app.utils.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
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
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class EventRepositoryTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

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
        eventRepository = new EventRepository(utilModel, databaseRepository, eventService);
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
        when(utilModel.isConnected()).thenReturn(false);
        when(databaseRepository.getAllItems(any())).thenReturn(Observable.empty());
        when(databaseRepository.getItems(any(), any())).thenReturn(Observable.empty());

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

        Mockito.verifyZeroInteractions(eventService);
    }

    @Test
    public void shouldSaveOrganizerInCache() {
        User user = new User();

        TestObserver testObserver = TestObserver.create();
        Completable completable = Completable.complete()
            .doOnSubscribe(testObserver::onSubscribe);

        when(utilModel.isConnected()).thenReturn(true);
        when(utilModel.getToken()).thenReturn(token);
        when(databaseRepository.getAllItems(User.class)).thenReturn(Observable.empty());
        when(databaseRepository.save(User.class, user)).thenReturn(completable);
        when(eventService.getUser(auth)).thenReturn(Observable.just(user));

        // No force reload ensures use of cache
        Observable<User> userObservable = eventRepository.getOrganiser(false);

        userObservable
            .test()
            .assertValue(user);

        // Verify loads from network
        verify(utilModel).getToken();
        verify(eventService).getUser(auth);
        verify(databaseRepository).save(User.class, user);
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

        when(utilModel.getToken()).thenReturn(token);
        when(eventService.getUser(auth)).thenReturn(Observable.just(user));
        when(databaseRepository.save(User.class, user)).thenReturn(Completable.complete());
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
        when(databaseRepository.getItems(eq(Event.class), refEq(Event_Table.id.eq(id))))
            .thenReturn(Observable.empty());
        when(databaseRepository.save(Event.class, event)).thenReturn(completable);
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
        verify(databaseRepository).save(Event.class, event);
    }

    @Test
    public void shouldLoadEventFromCache() {
        long id = 45;

        Event event = new Event();
        event.setComplete(true);
        when(databaseRepository.getItems(eq(Event.class), refEq(Event_Table.id.eq(id))))
            .thenReturn(Observable.just(event));

        // No force reload ensures use of cache
        Observable<Event> eventObservable = eventRepository.getEvent(id, false);
        eventObservable
            .test()
            .assertValue(event);

        verify(databaseRepository).getItems(eq(Event.class), refEq(Event_Table.id.eq(id)));
        verifyZeroInteractions(eventService);
    }

    @Test
    public void shouldFetchEventOnForceReload() {
        long id = 45;

        Event event = new Event();
        when(databaseRepository.save(Event.class, event)).thenReturn(Completable.complete());
        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.getEvent(id)).thenReturn(Observable.just(event));

        // Force reload ensures no use of cache
        Observable<Event> userObservable = eventRepository.getEvent(id, true);

        userObservable
            .test()
            .assertValue(event);

        // Verify loads from network
        verify(eventService).getEvent(id);
        verify(databaseRepository, never()).getItems(any(), any());
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

        when(utilModel.isConnected()).thenReturn(true);
        when(utilModel.getToken()).thenReturn(token);
        when(databaseRepository.getAllItems(eq(Event.class)))
            .thenReturn(Observable.empty());
        when(databaseRepository.saveList(Event.class, events)).thenReturn(completable);
        when(databaseRepository.deleteAll(Event.class)).thenReturn(completable);
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
    public void shouldStartCompleteEventDownloadOnPartialEvent() {
        List<Event> events = Arrays.asList(
            new Event(12),
            new Event(21),
            new Event(52)
        );

        EventRepository spiedRepository = spy(eventRepository);

        TestObserver testObserver1 = TestObserver.create();
        TestObserver testObserver2 = TestObserver.create();
        TestObserver testObserver3 = TestObserver.create();
        Observable ob1 = Observable.empty().doOnSubscribe(testObserver1::onSubscribe);
        Observable ob2 = Observable.empty().doOnSubscribe(testObserver2::onSubscribe);
        Observable ob3 = Observable.empty().doOnSubscribe(testObserver3::onSubscribe);

        doReturn(ob1).when(spiedRepository).getEvent(12, false);
        doReturn(ob2).when(spiedRepository).getEvent(21, false);
        doReturn(ob3).when(spiedRepository).getEvent(52, false);

        when(databaseRepository.getAllItems(eq(Event.class)))
            .thenReturn(Observable.empty());
        when(databaseRepository.saveList(Event.class, events)).thenReturn(Completable.complete());
        when(databaseRepository.deleteAll(Event.class)).thenReturn(Completable.complete());
        when(utilModel.isConnected()).thenReturn(true);
        when(utilModel.getToken()).thenReturn(token);
        when(eventService.getEvents(auth)).thenReturn(Observable.just(events));

        // No force reload ensures use of cache
        spiedRepository.getEvents(false).subscribe();

        verify(spiedRepository).getEvent(12, false);
        verify(spiedRepository).getEvent(21, false);
        verify(spiedRepository).getEvent(52, false);

        testObserver1.assertSubscribed();
        testObserver2.assertSubscribed();
        testObserver3.assertSubscribed();
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
        when(utilModel.getToken()).thenReturn(token);
        when(databaseRepository.saveList(Event.class, events)).thenReturn(Completable.complete());
        when(databaseRepository.deleteAll(Event.class)).thenReturn(Completable.complete());
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
    public void shouldDeletePreviousDataOnForceReload() {
        List<Event> events = Arrays.asList(
            new Event(12),
            new Event(21),
            new Event(52)
        );

        when(utilModel.isConnected()).thenReturn(true);
        when(utilModel.getToken()).thenReturn(token);
        when(databaseRepository.saveList(Event.class, events)).thenReturn(Completable.complete());
        when(databaseRepository.deleteAll(Event.class)).thenReturn(Completable.complete());
        when(eventService.getEvents(auth)).thenReturn(Observable.just(events));

        InOrder inOrder = Mockito.inOrder(databaseRepository);

        eventRepository.getEvents(true).test();

        inOrder.verify(databaseRepository).deleteAll(Event.class);
        inOrder.verify(databaseRepository).saveList(Event.class, events);
    }

}
