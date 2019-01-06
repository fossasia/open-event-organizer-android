package com.eventyay.organizer.data.repository;

import com.eventyay.organizer.common.Constants;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.AbstractObservable;
import com.eventyay.organizer.data.Repository;
import com.eventyay.organizer.data.auth.AuthHolder;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.event.EventApi;
import com.eventyay.organizer.data.event.EventRepositoryImpl;
import com.eventyay.organizer.data.event.Event_Table;
import com.eventyay.organizer.data.event.ImageUploadApi;
import com.eventyay.organizer.data.user.User;
import com.eventyay.organizer.data.user.UserApi;
import com.eventyay.organizer.data.user.UserRepositoryImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@SuppressWarnings("PMD.TooManyMethods")
public class EventRepositoryTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private EventRepositoryImpl eventRepository;
    private UserRepositoryImpl userRepository;

    @Mock private EventApi eventApi;
    @Mock private UserApi userApi;
    @Mock private Repository repository;
    @Mock private AuthHolder authHolder;
    @Mock private ImageUploadApi imageUploadApi;

    private static final List<Event> EVENTS = Arrays.asList(
        Event.builder().id(12L).build(),
        Event.builder().id(21L).build(),
        Event.builder().id(52L).build()
    );
    private static final long ID = 4L;
    private static final Event EVENT = Event.builder().id(ID).state(Event.STATE_PUBLISHED).build();
    private static final Event UPDATED_EVENT = Event.builder().id(ID).state(Event.STATE_PUBLISHED).build();

    @Before
    public void setUp() {
        when(repository.observableOf(Event.class)).thenReturn(new AbstractObservable.AbstractObservableBuilder<>(repository));
        when(repository.observableOf(User.class)).thenReturn(new AbstractObservable.AbstractObservableBuilder<>(repository));
        eventRepository = new EventRepositoryImpl(repository, eventApi, authHolder, imageUploadApi);
        userRepository = new UserRepositoryImpl(userApi, repository, authHolder);
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldSendErrorOnNetworkDown() {
        when(repository.isConnected()).thenReturn(false);
        when(repository.getAllItems(any())).thenReturn(Observable.empty());
        when(repository.getItems(any(), any())).thenReturn(Observable.empty());

        eventRepository.getEvents(false)
            .test()
            .assertErrorMessage(Constants.NO_NETWORK);

        eventRepository.getEvents(true)
            .test()
            .assertErrorMessage(Constants.NO_NETWORK);

        eventRepository.getEvent(21L, false)
            .test()
            .assertErrorMessage(Constants.NO_NETWORK);

        eventRepository.getEvent(21L, true)
            .test()
            .assertErrorMessage(Constants.NO_NETWORK);

        userRepository.getOrganizer(false)
            .test()
            .assertErrorMessage(Constants.NO_NETWORK);

        userRepository.getOrganizer(true)
            .test()
            .assertErrorMessage(Constants.NO_NETWORK);

        verifyZeroInteractions(eventApi);
    }

    @Test
    public void shouldSaveOrganizerInCache() {
        User user = new User();

        TestObserver testObserver = TestObserver.create();
        Completable completable = Completable.complete()
            .doOnSubscribe(testObserver::onSubscribe);

        when(repository.isConnected()).thenReturn(true);
        when(authHolder.getIdentity()).thenReturn(344);
        when(repository.getItems(eq(User.class), any()))
            .thenReturn(Observable.empty())
            .thenReturn(Observable.just(user));
        when(repository.save(User.class, user)).thenReturn(completable);
        when(userApi.getOrganizer(344)).thenReturn(Observable.just(user));

        // No force reload ensures use of cache
        Observable<User> userObservable = userRepository.getOrganizer(false);

        userObservable
            .test()
            .assertValue(user);

        // Verify loads from network
        verify(userApi).getOrganizer(344);
        verify(repository).save(User.class, user);
        testObserver.assertSubscribed();
    }

    @Test
    public void shouldLoadOrganizerFromCache() {
        User user = new User();

        when(repository.getItems(eq(User.class), any())).thenReturn(Observable.just(user));

        // No force reload ensures use of cache
        Observable<User> userObservable = userRepository.getOrganizer(false);

        userObservable
            .test()
            .assertValue(user);

        verifyZeroInteractions(userApi);
    }

    @Test
    public void shouldFetchOrganizerOnForceReload() {
        User user = new User();

        when(authHolder.getIdentity()).thenReturn(344);
        when(userApi.getOrganizer(344)).thenReturn(Observable.just(user));
        when(repository.save(User.class, user)).thenReturn(Completable.complete());
        when(repository.getAllItems(User.class)).thenReturn(Observable.just(user));
        when(repository.isConnected()).thenReturn(true);

        // Force reload ensures no use of cache
        userRepository.getOrganizer(true).subscribe();

        // Verify loads from network
        verify(userApi).getOrganizer(344);
    }

    @Test
    public void shouldSaveEventInCache() {
        long id = 23L;

        Event event = new Event();

        TestObserver testObserver = TestObserver.create();
        Completable completable = Completable.complete()
            .doOnSubscribe(testObserver::onSubscribe);

        when(repository.isConnected()).thenReturn(true);
        when(repository.getItems(eq(Event.class), refEq(Event_Table.id.eq(id))))
            .thenReturn(Observable.empty())
            .thenReturn(Observable.just(event));
        when(repository.save(Event.class, event)).thenReturn(completable);
        when(eventApi.getEvent(id)).thenReturn(Observable.just(event));

        // No force reload ensures use of cache
        Observable<Event> userObservable = eventRepository.getEvent(23, false);

        userObservable.test()
            .assertSubscribed()
            .assertValue(event);

        testObserver.assertSubscribed();

        // Verify loads from network
        verify(eventApi).getEvent(id);
        verify(repository).save(Event.class, event);
    }

    @Test
    public void shouldLoadEventFromCache() {
        long id = 45L;

        Event event = new Event();
        when(repository.getItems(eq(Event.class), refEq(Event_Table.id.eq(id))))
            .thenReturn(Observable.just(event));

        // No force reload ensures use of cache
        Observable<Event> eventObservable = eventRepository.getEvent(id, false);
        eventObservable.test();

        verify(repository).getItems(eq(Event.class), refEq(Event_Table.id.eq(id)));
        verifyZeroInteractions(eventApi);
    }

    @Test
    public void shouldFetchEventOnForceReload() {
        long id = 45L;

        Event event = new Event();
        when(repository.save(Event.class, event)).thenReturn(Completable.complete());
        when(repository.getItems(eq(Event.class), refEq(Event_Table.id.eq(id))))
            .thenReturn(Observable.just(event));
        when(repository.isConnected()).thenReturn(true);
        when(eventApi.getEvent(id)).thenReturn(Observable.just(event));

        // Force reload ensures no use of cache
        Observable<Event> userObservable = eventRepository.getEvent(id, true);

        userObservable
            .test()
            .assertValue(event);

        // Verify loads from network
        verify(eventApi).getEvent(id);
    }

    @Test
    public void shouldSaveEventsInCache() {
        TestObserver testObserver = TestObserver.create();
        Completable completable = Completable.complete()
            .doOnSubscribe(testObserver::onSubscribe);

        when(repository.isConnected()).thenReturn(true);
        when(authHolder.getIdentity()).thenReturn(344);
        when(repository.getAllItems(eq(Event.class)))
            .thenReturn(Observable.empty())
            .thenReturn(Observable.fromIterable(EVENTS));
        when(repository.syncSave(eq(Event.class), eq(EVENTS), any(), any())).thenReturn(completable);
        when(eventApi.getEvents(344L)).thenReturn(Observable.just(EVENTS));

        // No force reload ensures use of cache
        Observable<Event> eventObservable = eventRepository.getEvents(false);

        eventObservable
            .toList()
            .test()
            .assertValue(EVENTS);

        // Verify loads from network
        verify(eventApi).getEvents(344L);
        verify(repository).syncSave(eq(Event.class), eq(EVENTS), any(), any());

        testObserver.assertSubscribed();
    }

    @Test
    public void shouldLoadEventsFromCache() {
        when(repository.getAllItems(eq(Event.class)))
            .thenReturn(Observable.fromIterable(EVENTS));
        // No force reload ensures use of cache
        Observable<Event> eventsObservable = eventRepository.getEvents(false);

        eventsObservable
            .toList()
            .test()
            .assertValue(EVENTS);

        verify(repository).getAllItems(eq(Event.class));
        verifyZeroInteractions(eventApi);
    }

    @Test
    public void shouldFetchEventsOnForceReload() {
        when(repository.isConnected()).thenReturn(true);
        when(authHolder.getIdentity()).thenReturn(344);
        when(repository.saveList(Event.class, EVENTS)).thenReturn(Completable.complete());
        when(repository.getAllItems(eq(Event.class)))
            .thenReturn(Observable.fromIterable(EVENTS));
        when(repository.syncSave(eq(Event.class), eq(EVENTS), any(), any())).thenReturn(Completable.complete());
        when(eventApi.getEvents(344)).thenReturn(Observable.just(EVENTS));

        // Force reload ensures no use of cache
        Observable<Event> eventsObservable = eventRepository.getEvents(true);

        eventsObservable
            .toList()
            .test()
            .assertValue(EVENTS);

        // Verify loads from network
        verify(eventApi).getEvents(344L);
        verify(repository, never()).getAllItems(eq(Event.class));
    }

    @Test
    public void shouldSaveOnForceReload() {
        when(repository.isConnected()).thenReturn(true);
        when(authHolder.getIdentity()).thenReturn(344);
        //when(repository.saveList(Event.class, EVENTS)).thenReturn(Completable.complete());
        //when(repository.deleteAll(Event.class)).thenReturn(Completable.complete());
        when(eventApi.getEvents(344L)).thenReturn(Observable.just(EVENTS));

        eventRepository.getEvents(true).test();

        verify(repository).syncSave(eq(Event.class), eq(EVENTS), any(), any());
    }

    @Test
    public void shouldUpdateToggledEventToDatabaseOnSuccess() {
        TestObserver testObserver = TestObserver.create();
        Completable completable = Completable.complete()
            .doOnSubscribe(testObserver::onSubscribe);

        when(repository.isConnected()).thenReturn(true);
        when(authHolder.getIdentity()).thenReturn(344);
        when(eventApi.patchEvent(EVENT.id, EVENT)).thenReturn(Observable.just(UPDATED_EVENT));
        when(repository.update(Event.class, UPDATED_EVENT)).thenReturn(completable);

        eventRepository.updateEvent(EVENT).test();

        testObserver.assertSubscribed();
    }

    @Test
    public void shouldNotUpdateToggledEventOnError() {
        TestObserver testObserver = TestObserver.create();
        Completable completable = Completable.complete()
            .doOnSubscribe(testObserver::onSubscribe);

        when(repository.isConnected()).thenReturn(true);
        when(authHolder.getIdentity()).thenReturn(344);
        when(eventApi.patchEvent(EVENT.id, EVENT)).thenReturn(ObservableLift.error(Logger.TEST_ERROR));
        when(repository.update(Event.class, UPDATED_EVENT)).thenReturn(completable);

        eventRepository.updateEvent(EVENT).test();

        testObserver.assertNotSubscribed();
    }

    @Test
    public void shouldSaveCreatedEventToDatabaseOnSuccess() {
        TestObserver testObserver = TestObserver.create();
        Completable completable = Completable.complete()
            .doOnSubscribe(testObserver::onSubscribe);

        when(repository.isConnected()).thenReturn(true);
        when(eventApi.postEvent(EVENT)).thenReturn(Observable.just(EVENT));
        when(repository.save(Event.class, EVENT)).thenReturn(completable);

        eventRepository.createEvent(EVENT).test();

        testObserver.assertSubscribed();
    }

    @Test
    public void shouldNotSaveCreatedEventOnError() {
        TestObserver testObserver = TestObserver.create();
        Completable completable = Completable.complete()
            .doOnSubscribe(testObserver::onSubscribe);

        when(repository.isConnected()).thenReturn(true);
        when(eventApi.postEvent(EVENT)).thenReturn(ObservableLift.error(Logger.TEST_ERROR));
        when(repository.save(Event.class, EVENT)).thenReturn(completable);

        eventRepository.createEvent(EVENT).test();

        testObserver.assertNotSubscribed();
    }
}
