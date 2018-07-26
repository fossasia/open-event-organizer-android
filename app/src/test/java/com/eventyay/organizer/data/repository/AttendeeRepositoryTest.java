package com.eventyay.organizer.data.repository;

import com.raizlabs.android.dbflow.sql.language.SQLOperator;

import com.eventyay.organizer.common.Constants;
import com.eventyay.organizer.data.AbstractObservable;
import com.eventyay.organizer.data.Repository;
import com.eventyay.organizer.data.attendee.Attendee;
import com.eventyay.organizer.data.attendee.AttendeeApi;
import com.eventyay.organizer.data.attendee.AttendeeRepositoryImpl;
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

    private AttendeeRepositoryImpl attendeeRepository;

    @Mock private AttendeeApi attendeeApi;
    @Mock private Repository repository;

    private static final List<Attendee> ATTENDEES = Arrays.asList(
        new Attendee(),
        new Attendee(),
        new Attendee()
    );

    @Before
    public void setUp() {
        when(repository.observableOf(Attendee.class)).thenReturn(new AbstractObservable.AbstractObservableBuilder<>(repository));
        attendeeRepository = new AttendeeRepositoryImpl(repository, attendeeApi);
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

        when(repository.isConnected()).thenReturn(true);
        when(repository.getItems(eq(Attendee.class), any(SQLOperator.class))).thenReturn(Observable.empty());
        when(repository.syncSave(eq(Attendee.class), any(), any(), any())).thenReturn(completable);
        when(attendeeApi.getAttendees(43)).thenReturn(Observable.just(ATTENDEES));

        // No force reload ensures use of cache
        attendeeRepository.getAttendees(43, false).test();

        testObserver.assertSubscribed();

        // Verify loads from network
        verify(attendeeApi).getAttendees(43);
    }

    @Test
    public void shouldLoadAttendeesFromCache() {
        when(repository.getItems(eq(Attendee.class), any(SQLOperator.class)))
            .thenReturn(Observable.fromIterable(ATTENDEES));

        // No force reload ensures use of cache
        Observable<Attendee> attendeeObservable = attendeeRepository.getAttendees(67, false);

        attendeeObservable
            .toList()
            .test()
            .assertNoErrors()
            .assertValue(ATTENDEES);

        verifyZeroInteractions(attendeeApi);
    }

    @Test
    public void shouldFetchAttendeesOnForceReload() {
        when(repository.isConnected()).thenReturn(true);
        when(attendeeApi.getAttendees(23)).thenReturn(Observable.just(ATTENDEES));
        when(repository.syncSave(eq(Attendee.class), any(), any(), any())).thenReturn(Completable.complete());
        when(repository.getItems(eq(Attendee.class), any(SQLOperator.class)))
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
        verify(attendeeApi).getAttendees(23);
    }

    @Test
    public void shouldSendErrorOnNetworkDown() {
        when(repository.isConnected()).thenReturn(false);
        when(repository.getItems(any(), any())).thenReturn(Observable.empty());

        attendeeRepository.getAttendees(43, false)
            .test()
            .assertErrorMessage(Constants.NO_NETWORK);

        attendeeRepository.getAttendees(43, true)
            .test()
            .assertErrorMessage(Constants.NO_NETWORK);

        attendeeRepository.toggleAttendeeCheckStatus(ATTENDEES.get(0))
            .test()
            .assertErrorMessage(Constants.NO_NETWORK);

        verifyZeroInteractions(attendeeApi);
    }

    @Test
    public void shouldSaveToggledAttendeeCheck() {
        Attendee attendee = Attendee.builder().id(89).build();

        attendee.setCheckedIn(true);

        TestObserver testObserver = TestObserver.create();
        Completable completable = Completable.complete()
            .doOnSubscribe(testObserver::onSubscribe);

        when(repository.isConnected()).thenReturn(true);
        when(repository.getItems(eq(Attendee.class), any())).thenReturn(Observable.just(attendee));
        when(repository.update(Attendee.class, attendee)).thenReturn(completable);
        when(attendeeApi.patchAttendee(89, attendee)).thenReturn(Observable.just(attendee));

        Observable<Attendee> attendeeObservable = attendeeRepository.toggleAttendeeCheckStatus(attendee);

        attendeeObservable.test();

        testObserver.assertSubscribed();
    }

}
