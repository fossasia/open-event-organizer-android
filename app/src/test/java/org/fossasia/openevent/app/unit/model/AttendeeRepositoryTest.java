package org.fossasia.openevent.app.unit.model;

import com.raizlabs.android.dbflow.sql.language.SQLOperator;

import org.fossasia.openevent.app.common.data.contract.IUtilModel;
import org.fossasia.openevent.app.common.data.db.contract.IDatabaseRepository;
import org.fossasia.openevent.app.common.data.models.Attendee;
import org.fossasia.openevent.app.common.data.network.EventService;
import org.fossasia.openevent.app.common.data.repository.AttendeeRepository;
import org.fossasia.openevent.app.common.Constants;
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
        List<Attendee> attendees = Arrays.asList(
            new Attendee(),
            new Attendee(),
            new Attendee()
        );

        TestObserver testObserver = TestObserver.create();
        Completable completable = Completable.complete()
            .doOnSubscribe(testObserver::onSubscribe);

        when(utilModel.isConnected()).thenReturn(true);
        when(databaseRepository.getItems(eq(Attendee.class), any(SQLOperator.class))).thenReturn(Observable.empty());
        when(databaseRepository.deleteAll(Attendee.class)).thenReturn(completable);
        when(databaseRepository.saveList(Attendee.class, attendees)).thenReturn(completable);
        when(eventService.getAttendees(43)).thenReturn(Observable.just(attendees));

        // No force reload ensures use of cache
        attendeeRepository.getAttendees(43, false).test();

        testObserver.assertSubscribed();

        // Verify loads from network
        verify(eventService).getAttendees(43);
    }

    @Test
    public void shouldLoadAttendeesFromCache() {
        List<Attendee> attendees = Arrays.asList(
            new Attendee(),
            new Attendee(),
            new Attendee()
        );

        when(databaseRepository.getItems(eq(Attendee.class), any(SQLOperator.class)))
            .thenReturn(Observable.fromIterable(attendees));

        // No force reload ensures use of cache
        Observable<Attendee> attendeeObservable = attendeeRepository.getAttendees(67, false);

        attendeeObservable
            .toList()
            .test()
            .assertNoErrors()
            .assertValue(attendees);

        verifyZeroInteractions(eventService);
    }

    @Test
    public void shouldFetchAttendeesOnForceReload() {
        List<Attendee> attendees = Arrays.asList(
            new Attendee(),
            new Attendee(),
            new Attendee()
        );

        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.getAttendees(23)).thenReturn(Observable.just(attendees));
        when(databaseRepository.deleteAll(Attendee.class)).thenReturn(Completable.complete());
        when(databaseRepository.saveList(Attendee.class, attendees)).thenReturn(Completable.complete());

        // Force reload ensures no use of cache
        Observable<List<Attendee>> attendeeObservable = attendeeRepository.getAttendees(23, true)
            .toList()
            .toObservable();

        attendeeObservable.
            test()
            .assertNoErrors()
            .assertValue(attendees);

        // Verify loads from network
        verify(eventService).getAttendees(23);
    }

    @Test
    public void shouldDeletePreviousDataOnForceReload() {
        List<Attendee> attendees = Arrays.asList(
            new Attendee(),
            new Attendee(),
            new Attendee()
        );

        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.getAttendees(23)).thenReturn(Observable.just(attendees));
        when(databaseRepository.deleteAll(Attendee.class)).thenReturn(Completable.complete());
        when(databaseRepository.saveList(Attendee.class, attendees)).thenReturn(Completable.complete());

        InOrder inOrder = Mockito.inOrder(databaseRepository);

        attendeeRepository.getAttendees(23, true).test();

        inOrder.verify(databaseRepository).deleteAll(Attendee.class);
        inOrder.verify(databaseRepository).saveList(Attendee.class, attendees);
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

        attendeeRepository.toggleAttendeeCheckStatus(43, 52)
            .test()
            .assertErrorMessage(Constants.NO_NETWORK);

        verifyZeroInteractions(eventService);
    }

    @Test
    public void shouldSaveToggledAttendeeCheck() {
        Attendee attendee = new Attendee(89);

        attendee.setCheckedIn(true);

        TestObserver testObserver = TestObserver.create();
        Completable completable = Completable.complete()
            .doOnSubscribe(testObserver::onSubscribe);

        when(utilModel.isConnected()).thenReturn(true);
        when(databaseRepository.getItems(eq(Attendee.class), any())).thenReturn(Observable.just(attendee));
        when(databaseRepository.update(Attendee.class, attendee)).thenReturn(completable);
        when(eventService.patchAttendee(89, attendee)).thenReturn(Observable.just(attendee));

        Observable<Attendee> attendeeObservable = attendeeRepository.toggleAttendeeCheckStatus(76, 89);

        attendeeObservable.test();

        testObserver.assertSubscribed();
    }

}
