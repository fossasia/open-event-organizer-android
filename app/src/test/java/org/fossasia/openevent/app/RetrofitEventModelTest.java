package org.fossasia.openevent.app;

import org.fossasia.openevent.app.contract.model.UtilModel;
import org.fossasia.openevent.app.data.cache.ObjectCache;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.models.User;
import org.fossasia.openevent.app.data.network.api.EventService;
import org.fossasia.openevent.app.data.network.api.NetworkService;
import org.fossasia.openevent.app.data.network.api.RetrofitEventModel;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * EventModel implementation test with actual ObjectCache
 */
public class RetrofitEventModelTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private ObjectCache objectCache = ObjectCache.getInstance();

    private RetrofitEventModel retrofitEventModel;

    @Mock
    EventService eventService;

    @Mock
    UtilModel utilModel;

    private String token = "TestToken";
    private String auth = NetworkService.formatToken(token);

    @Before
    public void setUp() {
        when(utilModel.getToken()).thenReturn(token);

        retrofitEventModel = new RetrofitEventModel(utilModel);
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

        User stored = (User) objectCache.getValue(RetrofitEventModel.ORGANIZER);
        assertEquals(stored, user);
    }

    @Test
    public void shouldLoadOrganizerFromCache() {
        // Clear cache
        objectCache.clear();

        User user = new User();
        objectCache.saveObject(RetrofitEventModel.ORGANIZER, user);

        // No force reload ensures use of cache
        Observable<User> userObservable = retrofitEventModel.getOrganiser(false);

        userObservable.test().assertNoErrors();
        userObservable.test().assertValue(user);

        verify(eventService, never()).getUser(auth);
    }

    @Test
    public void shouldFetchOrganizerOnForceReload() {
        // Clear cache
        objectCache.clear();

        User user = new User();
        objectCache.saveObject(RetrofitEventModel.ORGANIZER, user);

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
        when(eventService.getEvent(id, auth)).thenReturn(Observable.just(event));

        // No force reload ensures use of cache
        Observable<Event> userObservable = retrofitEventModel.getEvent(23, false);

        userObservable.test().assertNoErrors();
        userObservable.test().assertValue(event);

        // Verify loads from network
        verify(eventService).getEvent(id, auth);

        Event stored = (Event) objectCache.getValue(RetrofitEventModel.EVENT + id);
        assertEquals(stored, event);
    }

    @Test
    public void shouldLoadEventFromCache() {
        int id = 45;

        // Clear cache
        objectCache.clear();

        Event event = new Event();
        objectCache.saveObject(RetrofitEventModel.EVENT + id, event);

        // No force reload ensures use of cache
        Observable<Event> userObservable = retrofitEventModel.getEvent(id, false);

        userObservable.test().assertNoErrors();
        userObservable.test().assertValue(event);

        verify(eventService, never()).getEvent(id, auth);
    }

    @Test
    public void shouldFetchEventOnForceReload() {
        int id = 45;

        // Clear cache
        objectCache.clear();

        Event event = new Event();
        objectCache.saveObject(RetrofitEventModel.EVENT + id, event);

        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.getEvent(id, auth)).thenReturn(Observable.just(event));

        // Force reload ensures no use of cache
        Observable<Event> userObservable = retrofitEventModel.getEvent(id, true);

        userObservable.test().assertNoErrors();
        userObservable.test().assertValue(event);

        // Verify loads from network
        verify(eventService).getEvent(id, auth);
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

        // Saving is asynchronous, so we need to wait for few moments
        try {
            Thread.sleep(10);
        } catch (InterruptedException ie) {
            // Shouldn't happen
        }

        List<Event> stored = (List<Event>) objectCache.getValue(RetrofitEventModel.EVENTS);
        assertEquals(stored, events);

        // Also check for a particular event being saved
        Event storedEvent = (Event) objectCache.getValue(RetrofitEventModel.EVENT + events.get(1).getId());
        assertEquals(storedEvent, events.get(1));
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
        objectCache.saveObject(RetrofitEventModel.EVENTS, events);

        // No force reload ensures use of cache
        Observable<List<Event>> eventsObservable = retrofitEventModel.getEvents(false);

        eventsObservable.test().assertNoErrors();
        eventsObservable.test().assertValue(events);

        verify(eventService, never()).getEvents(auth);
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
        objectCache.saveObject(RetrofitEventModel.EVENTS, events);

        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.getEvents(auth)).thenReturn(Observable.just(events));

        // Force reload ensures no use of cache
        Observable<List<Event>> eventsObservable = retrofitEventModel.getEvents(true);

        eventsObservable.test().assertNoErrors();
        eventsObservable.test().assertValue(events);

        // Verify loads from network
        verify(eventService).getEvents(auth);
    }

}
