package com.eventyay.organizer.core.attendee.checkin;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;

import com.eventyay.organizer.data.attendee.AttendeeRepository;
import com.eventyay.organizer.data.db.DbFlowDatabaseChangeListener;
import com.eventyay.organizer.data.db.DatabaseChangeListener;
import com.eventyay.organizer.data.attendee.Attendee;
import com.eventyay.organizer.data.event.Event;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class AttendeeCheckInViewModelTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();
    @Mock private AttendeeRepository attendeeRepository;
    @Mock private DatabaseChangeListener<Attendee> databaseChangeListener;
    private PublishSubject<DbFlowDatabaseChangeListener.ModelChange<Attendee>> notifier;

    private static final long ID = 42;
    private static final Attendee ATTENDEE = new Attendee();

    static {
        ATTENDEE.setId(ID);
        ATTENDEE.setEvent(Event.builder().id(ID).build());
    }

    private AttendeeCheckInViewModel attendeeCheckInViewModel;

    @Mock
    Observer<Attendee> attendeeObserver;
    @Mock
    Observer<String> error;

    @Before
    public void setUp() {
        attendeeCheckInViewModel = new AttendeeCheckInViewModel(attendeeRepository, databaseChangeListener);
        notifier = PublishSubject.create();

        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    private void setLoadAttendeeBehaviour() {
        when(databaseChangeListener.getNotifier()).thenReturn(notifier);
        when(attendeeRepository.getAttendee(ID, false))
            .thenReturn(Observable.just(ATTENDEE));
    }

    private Attendee getCheckedInAttendee() {
        Attendee attendee = Attendee.builder().id(ID).build();
        attendee.setCheckedIn(true);

        return attendee;
    }

    @Test
    public void shouldLoadAttendeeAutomatically() {
        setLoadAttendeeBehaviour();
        attendeeCheckInViewModel.start(ID);

        InOrder inOrder = Mockito.inOrder(attendeeRepository, attendeeObserver);

        inOrder.verify(attendeeRepository).getAttendee(ID, false);
        attendeeCheckInViewModel.getAttendee().observeForever(attendeeObserver);

        verify(attendeeObserver).onChanged(ATTENDEE);
    }

    @Test
    public void shouldStartListeningAutomatically() {
        setLoadAttendeeBehaviour();
        attendeeCheckInViewModel.start(ID);

        verify(databaseChangeListener).startListening();
    }

    @Test
    public void shouldStopListeningOnDetach() {
        attendeeCheckInViewModel.onCleared();

        verify(databaseChangeListener).stopListening();
    }

    @Test
    public void shouldHandleAttendeeChange() {
        Attendee toggled = getCheckedInAttendee();
        when(databaseChangeListener.getNotifier()).thenReturn(notifier);
        when(attendeeRepository.getAttendee(ID, false))
            .thenReturn(Observable.empty())
            .thenReturn(Observable.just(toggled));

        attendeeCheckInViewModel.setAttendee(ATTENDEE);
        when(attendeeRepository.scheduleToggle(ATTENDEE)).thenReturn(Completable.complete());

        InOrder inOrder = Mockito.inOrder(attendeeRepository);

        attendeeCheckInViewModel.toggleCheckIn();

        inOrder.verify(attendeeRepository).scheduleToggle(ATTENDEE);
    }

    @Test
    public void shouldHandleTogglingError() {
        attendeeCheckInViewModel.setAttendee(ATTENDEE);
        when(attendeeRepository.scheduleToggle(ATTENDEE)).thenReturn(Completable.error(new Throwable()));

        InOrder inOrder = Mockito.inOrder(error);

        attendeeCheckInViewModel.getError().observeForever(error);

        attendeeCheckInViewModel.toggleCheckIn();

        inOrder.verify(error).onChanged(any());
    }

}
