package org.fossasia.openevent.app.unit.presenter;

import org.fossasia.openevent.app.common.data.models.Attendee;
import org.fossasia.openevent.app.common.data.models.Event;
import org.fossasia.openevent.app.common.data.repository.contract.IAttendeeRepository;
import org.fossasia.openevent.app.module.attendee.checkin.AttendeeCheckInPresenter;
import org.fossasia.openevent.app.module.attendee.checkin.contract.IAttendeeCheckInView;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class AttendeeCheckInPresenterTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock private IAttendeeRepository attendeeRepository;
    @Mock private IAttendeeCheckInView attendeeCheckInView;

    private static final long ID = 42;
    private static final Attendee ATTENDEE = new Attendee(ID);

    private AttendeeCheckInPresenter attendeeCheckInPresenter;

    @Before
    public void setUp() {
        ATTENDEE.setEvent(new Event(ID));
        attendeeCheckInPresenter = new AttendeeCheckInPresenter(attendeeRepository);
        attendeeCheckInPresenter.attach(ID, attendeeCheckInView);

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
        when(attendeeRepository.getAttendee(ID, false))
            .thenReturn(Observable.just(ATTENDEE));
    }

    private void setToggleAttendeeBehaviour(Observable<Attendee> attendeeObservable) {
        when(attendeeRepository.toggleAttendeeCheckStatus(ID, ID)).thenReturn(attendeeObservable);
    }

    private Attendee getAttendee(boolean checkedIn) {
        Attendee attendee = new Attendee(ID);
        attendee.setCheckedIn(checkedIn);

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
    public void shouldHandleTogglingSuccess() {
        attendeeCheckInPresenter.setAttendee(ATTENDEE);
        Attendee toggled = getAttendee(true);
        setToggleAttendeeBehaviour(Observable.just(toggled));

        attendeeCheckInPresenter.toggleCheckIn();

        verify(attendeeCheckInView).showResult(toggled);
        verify(attendeeCheckInView).onSuccess(any());
    }

    @Test
    public void shouldShowCheckedInAfterToggling() {
        attendeeCheckInPresenter.setAttendee(ATTENDEE);
        Attendee toggled = getAttendee(true);
        setToggleAttendeeBehaviour(Observable.just(toggled));

        attendeeCheckInPresenter.toggleCheckIn();

        verify(attendeeCheckInView).onSuccess(contains("Checked In"));
    }

    @Test
    public void shouldShowCheckedOutAfterToggling() {
        attendeeCheckInPresenter.setAttendee(ATTENDEE);
        Attendee toggled = getAttendee(false);
        setToggleAttendeeBehaviour(Observable.just(toggled));

        attendeeCheckInPresenter.toggleCheckIn();

        verify(attendeeCheckInView).onSuccess(contains("Checked Out"));
    }

    @Test
    public void shouldHandleTogglingError() {
        attendeeCheckInPresenter.setAttendee(ATTENDEE);
        setToggleAttendeeBehaviour(Util.ERROR_OBSERVABLE);

        attendeeCheckInPresenter.toggleCheckIn();

        verify(attendeeCheckInView).showError(any());
    }

    @Test
    public void shouldShowProgressWhileTogglingSuccess() {
        attendeeCheckInPresenter.setAttendee(ATTENDEE);
        setToggleAttendeeBehaviour(Observable.just(ATTENDEE));

        attendeeCheckInPresenter.toggleCheckIn();

        InOrder inOrder = inOrder(attendeeCheckInView);

        inOrder.verify(attendeeCheckInView).showProgress(true);
        inOrder.verify(attendeeCheckInView).showResult(any());
        inOrder.verify(attendeeCheckInView).showProgress(false);
    }

    @Test
    public void shouldShowProgressWhileTogglingError() {
        attendeeCheckInPresenter.setAttendee(ATTENDEE);
        setToggleAttendeeBehaviour(Util.ERROR_OBSERVABLE);

        attendeeCheckInPresenter.toggleCheckIn();

        InOrder inOrder = inOrder(attendeeCheckInView);

        inOrder.verify(attendeeCheckInView).showProgress(true);
        inOrder.verify(attendeeCheckInView).showError(any());
        inOrder.verify(attendeeCheckInView).showProgress(false);
    }

}
