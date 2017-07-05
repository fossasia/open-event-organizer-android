package org.fossasia.openevent.app.presenter;

import com.raizlabs.android.dbflow.sql.language.SQLOperator;

import org.fossasia.openevent.app.data.contract.IEventRepository;
import org.fossasia.openevent.app.data.db.contract.IDatabaseRepository;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.event.checkin.AttendeeCheckInPresenter;
import org.fossasia.openevent.app.event.checkin.contract.IAttendeeCheckInView;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class AttendeeCheckInPresenterTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    IDatabaseRepository databaseRepository;

    @Mock
    IEventRepository eventRepository;

    @Mock
    IAttendeeCheckInView attendeeCheckInView;

    private AttendeeCheckInPresenter attendeeCheckInPresenter;
    private final long id = 42;
    private Attendee attendee = new Attendee(id);

    @Before
    public void setUp() {
        attendeeCheckInPresenter = new AttendeeCheckInPresenter(databaseRepository, eventRepository);
        attendeeCheckInPresenter.attach(id, attendeeCheckInView);

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
        when(databaseRepository.getItems(eq(Attendee.class), any(SQLOperator.class)))
            .thenReturn(Observable.just(attendee));
    }

    private void setToggleAttendeeBehaviour(Observable<Attendee> attendeeObservable) {
        when(eventRepository.toggleAttendeeCheckStatus(0, id)).thenReturn(attendeeObservable);
    }

    private Attendee getAttendee(boolean checkedIn) {
        Attendee attendee = new Attendee(id);
        attendee.setCheckedIn(checkedIn);

        return attendee;
    }

    @Test
    public void shouldDetachViewOnStop() {
        assertNotNull(attendeeCheckInPresenter.getView());

        attendeeCheckInPresenter.detach();

        assertNull(attendeeCheckInPresenter.getView());
    }

    @Test
    public void shouldNotAccessView() {
        setLoadAttendeeBehaviour();

        attendeeCheckInPresenter.detach();
        attendeeCheckInPresenter.start();
        attendeeCheckInPresenter.toggleCheckIn();

        Mockito.verifyZeroInteractions(attendeeCheckInView);
    }

    @Test
    public void shouldLoadAttendeeAutomatically() {
        setLoadAttendeeBehaviour();
        attendeeCheckInPresenter.start();

        verify(databaseRepository).getItems(eq(Attendee.class), any(SQLOperator.class));
        verify(attendeeCheckInView).showAttendee(attendee);
    }

    @Test
    public void shouldHandleTogglingSuccess() {
        attendeeCheckInPresenter.setAttendee(attendee);
        Attendee toggled = getAttendee(true);
        setToggleAttendeeBehaviour(Observable.just(toggled));

        attendeeCheckInPresenter.toggleCheckIn();

        verify(attendeeCheckInView).showAttendee(toggled);
        verify(attendeeCheckInView).onSuccess(any());
    }

    @Test
    public void shouldShowCheckedInAfterToggling() {
        attendeeCheckInPresenter.setAttendee(attendee);
        Attendee toggled = getAttendee(true);
        setToggleAttendeeBehaviour(Observable.just(toggled));

        attendeeCheckInPresenter.toggleCheckIn();

        verify(attendeeCheckInView).onSuccess(contains("Checked In"));
    }

    @Test
    public void shouldShowCheckedOutAfterToggling() {
        attendeeCheckInPresenter.setAttendee(attendee);
        Attendee toggled = getAttendee(false);
        setToggleAttendeeBehaviour(Observable.just(toggled));

        attendeeCheckInPresenter.toggleCheckIn();

        verify(attendeeCheckInView).onSuccess(contains("Checked Out"));
    }

    @Test
    public void shouldHandleTogglingError() {
        attendeeCheckInPresenter.setAttendee(attendee);
        setToggleAttendeeBehaviour(Observable.error(new Throwable()));

        attendeeCheckInPresenter.toggleCheckIn();

        verify(attendeeCheckInView).onError(any());
    }

    @Test
    public void shouldShowProgressWhileTogglingSuccess() {
        attendeeCheckInPresenter.setAttendee(attendee);
        setToggleAttendeeBehaviour(Observable.just(attendee));

        attendeeCheckInPresenter.toggleCheckIn();

        InOrder inOrder = inOrder(attendeeCheckInView);

        inOrder.verify(attendeeCheckInView).showProgress(true);
        inOrder.verify(attendeeCheckInView).showAttendee(any());
        inOrder.verify(attendeeCheckInView).showProgress(false);
    }

    @Test
    public void shouldShowProgressWhileTogglingError() {
        attendeeCheckInPresenter.setAttendee(attendee);
        setToggleAttendeeBehaviour(Observable.error(new Throwable()));

        attendeeCheckInPresenter.toggleCheckIn();

        InOrder inOrder = inOrder(attendeeCheckInView);

        inOrder.verify(attendeeCheckInView).showProgress(true);
        inOrder.verify(attendeeCheckInView).onError(any());
        inOrder.verify(attendeeCheckInView).showProgress(false);
    }

}
