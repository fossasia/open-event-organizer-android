package org.fossasia.openevent.app.unit.model;

import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.common.app.rx.Logger;
import org.fossasia.openevent.app.common.data.contract.IUtilModel;
import org.fossasia.openevent.app.common.data.db.contract.IDatabaseRepository;
import org.fossasia.openevent.app.common.data.models.Event;
import org.fossasia.openevent.app.common.data.models.Event_Table;
import org.fossasia.openevent.app.common.data.models.User;
import org.fossasia.openevent.app.common.data.network.EventService;
import org.fossasia.openevent.app.common.data.repository.EventRepository;
import org.fossasia.openevent.app.common.utils.core.Utils;
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
import io.reactivex.internal.operators.observable.ObservableLift;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class EventRepositoryTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private EventRepository eventRepository;

    @Mock private EventService eventService;
    @Mock private IUtilModel utilModel;
    @Mock private IDatabaseRepository databaseRepository;

    private static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
        ".eyJuYmYiOjE0OTU3NDU0MDAsImlhdCI6MTQ5NTc0NTQwMCwiZXhwIjoyNDk1ODMxODAwLCJpZGVudGl0eSI6MzQ0fQ" +
        ".A_aC4hwK8sixZk4k9gzmzidO1wj2hjy_EH573uorK-E";
    private static final String AUTH = Utils.formatToken(TOKEN);

    private static final List<Event> EVENTS = Arrays.asList(
        Event.builder().id(12).build(),
        Event.builder().id(21).build(),
        Event.builder().id(52).build()
    );
    private static final int ID = 4;
    private static final Event EVENT = Event.builder().id(ID).state(Event.STATE_PUBLISHED).build();
    private static final Event UPDATED_EVENT = Event.builder().id(ID).state(Event.STATE_PUBLISHED).build();

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
        assertEquals(AUTH, "JWT " + TOKEN);
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

        verifyZeroInteractions(eventService);
    }

    @Test
    public void shouldSaveOrganizerInCache() {
        User user = new User();

        TestObserver testObserver = TestObserver.create();
        Completable completable = Completable.complete()
            .doOnSubscribe(testObserver::onSubscribe);

        when(utilModel.isConnected()).thenReturn(true);
        when(utilModel.getToken()).thenReturn(TOKEN);
        when(databaseRepository.getAllItems(User.class))
            .thenReturn(Observable.empty())
            .thenReturn(Observable.just(user));
        when(databaseRepository.save(User.class, user)).thenReturn(completable);
        when(eventService.getUser(344)).thenReturn(Observable.just(user));

        // No force reload ensures use of cache
        Observable<User> userObservable = eventRepository.getOrganiser(false);

        userObservable
            .test()
            .assertValue(user);

        // Verify loads from network
        verify(utilModel).getToken();
        verify(eventService).getUser(344);
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

        when(utilModel.getToken()).thenReturn(TOKEN);
        when(eventService.getUser(344)).thenReturn(Observable.just(user));
        when(databaseRepository.save(User.class, user)).thenReturn(Completable.complete());
        when(databaseRepository.getAllItems(User.class)).thenReturn(Observable.just(user));
        when(utilModel.isConnected()).thenReturn(true);

        // Force reload ensures no use of cache
        eventRepository.getOrganiser(true).subscribe();

        // Verify loads from network
        verify(eventService).getUser(344);
        verify(databaseRepository).getAllItems(User.class);
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
            .thenReturn(Observable.empty())
            .thenReturn(Observable.just(event));
        when(databaseRepository.save(Event.class, event)).thenReturn(completable);
        when(eventService.getEvent(id)).thenReturn(Observable.just(event));

        // No force reload ensures use of cache
        Observable<Event> userObservable = eventRepository.getEvent(23, false);

        userObservable.test()
            .assertSubscribed()
            .assertValue(event);

        testObserver.assertSubscribed();

        // Verify loads from network
        verify(eventService).getEvent(id);
        verify(databaseRepository).save(Event.class, event);
    }

    @Test
    public void shouldLoadEventFromCache() {
        long id = 45;

        Event event = new Event();
        when(databaseRepository.getItems(eq(Event.class), refEq(Event_Table.id.eq(id))))
            .thenReturn(Observable.just(event));

        // No force reload ensures use of cache
        Observable<Event> eventObservable = eventRepository.getEvent(id, false);
        eventObservable.test();

        verify(databaseRepository).getItems(eq(Event.class), refEq(Event_Table.id.eq(id)));
        verifyZeroInteractions(eventService);
    }

    @Test
    public void shouldFetchEventOnForceReload() {
        long id = 45;

        Event event = new Event();
        when(databaseRepository.save(Event.class, event)).thenReturn(Completable.complete());
        when(databaseRepository.getItems(eq(Event.class), refEq(Event_Table.id.eq(id))))
            .thenReturn(Observable.just(event));
        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.getEvent(id)).thenReturn(Observable.just(event));

        // Force reload ensures no use of cache
        Observable<Event> userObservable = eventRepository.getEvent(id, true);

        userObservable
            .test()
            .assertValue(event);

        // Verify loads from network
        verify(eventService).getEvent(id);
    }

    @Test
    public void shouldSaveEventsInCache() {
        TestObserver testObserver = TestObserver.create();
        Completable completable = Completable.complete()
            .doOnSubscribe(testObserver::onSubscribe);

        when(utilModel.isConnected()).thenReturn(true);
        when(utilModel.getToken()).thenReturn(TOKEN);
        when(databaseRepository.getAllItems(eq(Event.class)))
            .thenReturn(Observable.empty())
            .thenReturn(Observable.fromIterable(EVENTS));
        when(databaseRepository.saveList(Event.class, EVENTS)).thenReturn(completable);
        when(databaseRepository.deleteAll(Event.class)).thenReturn(completable);
        when(eventService.getEvents(344)).thenReturn(Observable.just(EVENTS));

        // No force reload ensures use of cache
        Observable<Event> eventObservable = eventRepository.getEvents(false);

        eventObservable
            .toList()
            .test()
            .assertValue(EVENTS);

        // Verify loads from network
        verify(utilModel).getToken();
        verify(eventService).getEvents(344);

        testObserver.assertSubscribed();
    }

    @Test
    public void shouldLoadEventsFromCache() {
        when(databaseRepository.getAllItems(eq(Event.class)))
            .thenReturn(Observable.fromIterable(EVENTS));
        // No force reload ensures use of cache
        Observable<Event> eventsObservable = eventRepository.getEvents(false);

        eventsObservable
            .toList()
            .test()
            .assertValue(EVENTS);

        verify(databaseRepository, times(2)).getAllItems(eq(Event.class));
        verifyZeroInteractions(eventService);
    }

    @Test
    public void shouldFetchEventsOnForceReload() {
        when(utilModel.isConnected()).thenReturn(true);
        when(utilModel.getToken()).thenReturn(TOKEN);
        when(databaseRepository.saveList(Event.class, EVENTS)).thenReturn(Completable.complete());
        when(databaseRepository.getAllItems(eq(Event.class)))
            .thenReturn(Observable.fromIterable(EVENTS));
        when(databaseRepository.deleteAll(Event.class)).thenReturn(Completable.complete());
        when(eventService.getEvents(344)).thenReturn(Observable.just(EVENTS));

        // Force reload ensures no use of cache
        Observable<Event> eventsObservable = eventRepository.getEvents(true);

        eventsObservable
            .toList()
            .test()
            .assertValue(EVENTS);

        // Verify loads from network
        verify(eventService).getEvents(344);
        verify(databaseRepository).getAllItems(eq(Event.class));
    }

    @Test
    public void shouldDeletePreviousDataOnForceReload() {
        when(utilModel.isConnected()).thenReturn(true);
        when(utilModel.getToken()).thenReturn(TOKEN);
        when(databaseRepository.saveList(Event.class, EVENTS)).thenReturn(Completable.complete());
        when(databaseRepository.deleteAll(Event.class)).thenReturn(Completable.complete());
        when(eventService.getEvents(344)).thenReturn(Observable.just(EVENTS));

        InOrder inOrder = Mockito.inOrder(databaseRepository);

        eventRepository.getEvents(true).test();

        inOrder.verify(databaseRepository).deleteAll(Event.class);
        inOrder.verify(databaseRepository).saveList(Event.class, EVENTS);
    }

    @Test
    public void shouldUpdateToggledEventToDatabaseOnSuccess() {
        TestObserver testObserver = TestObserver.create();
        Completable completable = Completable.complete()
            .doOnSubscribe(testObserver::onSubscribe);

        when(utilModel.isConnected()).thenReturn(true);
        when(utilModel.getToken()).thenReturn(TOKEN);
        when(eventService.patchEvent(EVENT.id, EVENT)).thenReturn(Observable.just(UPDATED_EVENT));
        when(databaseRepository.update(Event.class, UPDATED_EVENT)).thenReturn(completable);

        eventRepository.updateEvent(EVENT).test();

        testObserver.assertSubscribed();
    }

    @Test
    public void shouldNotUpdateToggledEventOnError() {
        TestObserver testObserver = TestObserver.create();
        Completable completable = Completable.complete()
            .doOnSubscribe(testObserver::onSubscribe);

        when(utilModel.isConnected()).thenReturn(true);
        when(utilModel.getToken()).thenReturn(TOKEN);
        when(eventService.patchEvent(EVENT.id, EVENT)).thenReturn(ObservableLift.error(Logger.TEST_ERROR));
        when(databaseRepository.update(Event.class, UPDATED_EVENT)).thenReturn(completable);

        eventRepository.updateEvent(EVENT).test();

        testObserver.assertNotSubscribed();
    }

}
