package org.fossasia.openevent.app.unit.presenter;

import com.raizlabs.android.dbflow.structure.BaseModel;

import org.fossasia.openevent.app.data.db.DatabaseChangeListener;
import org.fossasia.openevent.app.data.db.IDatabaseChangeListener;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.repository.IAttendeeRepository;
import org.fossasia.openevent.app.core.attendee.checkin.AttendeeCheckInPresenter;
import org.fossasia.openevent.app.core.attendee.checkin.IAttendeeCheckInView;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class AttendeeCheckInPresenterTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock private IAttendeeRepository attendeeRepository;
    @Mock private IAttendeeCheckInView attendeeCheckInView;
    @Mock private IDatabaseChangeListener<Attendee> databaseChangeListener;
    private PublishSubject<DatabaseChangeListener.ModelChange<Attendee>> notifier;

    private static final long ID = 42;
    private static final Attendee ATTENDEE = new Attendee();

    static {
        ATTENDEE.setId(ID);
        ATTENDEE.setEvent(Event.builder().id(ID).build());
    }

    private AttendeeCheckInPresenter attendeeCheckInPresenter;

    @Before
    public void setUp() {
        attendeeCheckInPresenter = new AttendeeCheckInPresenter(attendeeRepository, databaseChangeListener);
        attendeeCheckInPresenter.attach(ID, attendeeCheckInView);
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
    public void shouldDetachViewOnStop() {
        assertNotNull(attendeeCheckInPresenter.getView());

        attendeeCheckInPresenter.detach();

        assertTrue(attendeeCheckInPresenter.getDisposable().isDisposed());
    }

    @Test
    public void shouldLoadAttendeeAutomatically() {
        setLoadAttendeeBehaviour();
        attendeeCheckInPresenter.start();

        verify(attendeeRepository).getAttendee(ID, false);
        verify(attendeeCheckInView).showResult(ATTENDEE);
    }

    @Test
    public void shouldStartListeningAutomatically() {
        setLoadAttendeeBehaviour();
        attendeeCheckInPresenter.start();

        verify(databaseChangeListener).startListening();
    }

    @Test
    public void shouldStopListeningOnDetach() {
        attendeeCheckInPresenter.detach();

        verify(databaseChangeListener).stopListening();
    }

    @Test
    public void shouldHandleAttendeeChange() {
        attendeeCheckInPresenter.setAttendee(ATTENDEE);
        Attendee toggled = getCheckedInAttendee();
        when(databaseChangeListener.getNotifier()).thenReturn(notifier);
        when(attendeeRepository.getAttendee(ID, false))
            .thenReturn(Observable.empty())
            .thenReturn(Observable.just(toggled));

        attendeeCheckInPresenter.start();

        notifier.onNext(new DatabaseChangeListener.ModelChange<>(ATTENDEE, BaseModel.Action.UPDATE));

        verify(attendeeCheckInView).showResult(toggled);
    }

    @Test
    public void shouldHandleTogglingError() {
        attendeeCheckInPresenter.setAttendee(ATTENDEE);
        when(attendeeRepository.scheduleToggle(ATTENDEE)).thenReturn(Completable.error(new Throwable()));

        attendeeCheckInPresenter.toggleCheckIn();

        verify(attendeeCheckInView).showError(any());
    }

}
