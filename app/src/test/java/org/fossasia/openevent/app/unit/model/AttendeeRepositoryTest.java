package org.fossasia.openevent.app.unit.model;

import com.raizlabs.android.dbflow.sql.language.SQLOperator;

import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.data.IUtilModel;
import org.fossasia.openevent.app.data.db.IDatabaseRepository;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.data.network.EventService;
import org.fossasia.openevent.app.data.repository.AttendeeRepository;
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
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class AttendeeRepositoryTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    private AttendeeRepository attendeeRepository;

    @Mock private EventService eventService;
    @Mock private IUtilModel utilModel;
    @Mock private IDatabaseRepository databaseRepository;

    private static final List<Attendee> ATTENDEES = Arrays.asList(
        new Attendee(),
        new Attendee(),
        new Attendee()
    );

    @Before
    public void setUp() {
        attendeeRepository = new AttendeeRepository(utilModel, databaseRepository, eventService);
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldSaveAttendeesInCache() {
        TestObserver testObserver = TestObserver.create();
        Completable completable = Completable.complete()
            .doOnSubscribe(testObserver::onSubscribe);

        when(utilModel.isConnected()).thenReturn(true);
        when(databaseRepository.getItems(eq(Attendee.class), any(SQLOperator.class))).thenReturn(Observable.empty());
        when(databaseRepository.delete(eq(Attendee.class), any())).thenReturn(completable);
        when(databaseRepository.saveList(Attendee.class, ATTENDEES)).thenReturn(completable);
        when(eventService.getAttendees(43)).thenReturn(Observable.just(ATTENDEES));

        // No force reload ensures use of cache
        attendeeRepository.getAttendees(43, false).test();

        testObserver.assertSubscribed();

        // Verify loads from network
        verify(eventService).getAttendees(43);
    }

    @Test
    public void shouldLoadAttendeesFromCache() {
        when(databaseRepository.getItems(eq(Attendee.class), any(SQLOperator.class)))
            .thenReturn(Observable.fromIterable(ATTENDEES));

        // No force reload ensures use of cache
        Observable<Attendee> attendeeObservable = attendeeRepository.getAttendees(67, false);

        attendeeObservable
            .toList()
            .test()
            .assertNoErrors()
            .assertValue(ATTENDEES);

        verifyZeroInteractions(eventService);
    }

    @Test
    public void shouldFetchAttendeesOnForceReload() {
        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.getAttendees(23)).thenReturn(Observable.just(ATTENDEES));
        when(databaseRepository.deleteAll(Attendee.class)).thenReturn(Completable.complete());
        when(databaseRepository.saveList(Attendee.class, ATTENDEES)).thenReturn(Completable.complete());
        when(databaseRepository.getItems(eq(Attendee.class), any(SQLOperator.class)))
            .thenReturn(Observable.fromIterable(ATTENDEES));

        // Force reload ensures no use of cache
        Observable<List<Attendee>> attendeeObservable = attendeeRepository.getAttendees(23, true)
            .toList()
            .toObservable();

        attendeeObservable.
            test()
            .assertNoErrors()
            .assertValue(ATTENDEES);

        // Verify loads from network
        verify(eventService).getAttendees(23);
    }

    @Test
    public void shouldSendErrorOnNetworkDown() {
        when(utilModel.isConnected()).thenReturn(false);
        when(databaseRepository.getItems(any(), any())).thenReturn(Observable.empty());

        attendeeRepository.getAttendees(43, false)
            .test()
            .assertErrorMessage(Constants.NO_NETWORK);

        attendeeRepository.getAttendees(43, true)
            .test()
            .assertErrorMessage(Constants.NO_NETWORK);

        attendeeRepository.toggleAttendeeCheckStatus(ATTENDEES.get(0))
            .test()
            .assertErrorMessage(Constants.NO_NETWORK);

        verifyZeroInteractions(eventService);
    }

    @Test
    public void shouldSaveToggledAttendeeCheck() {
        Attendee attendee = Attendee.builder().id(89).build();

        attendee.setCheckedIn(true);

        TestObserver testObserver = TestObserver.create();
        Completable completable = Completable.complete()
            .doOnSubscribe(testObserver::onSubscribe);

        when(utilModel.isConnected()).thenReturn(true);
        when(databaseRepository.getItems(eq(Attendee.class), any())).thenReturn(Observable.just(attendee));
        when(databaseRepository.update(Attendee.class, attendee)).thenReturn(completable);
        when(eventService.patchAttendee(89, attendee)).thenReturn(Observable.just(attendee));

        Observable<Attendee> attendeeObservable = attendeeRepository.toggleAttendeeCheckStatus(attendee);

        attendeeObservable.test();

        testObserver.assertSubscribed();
    }

}
