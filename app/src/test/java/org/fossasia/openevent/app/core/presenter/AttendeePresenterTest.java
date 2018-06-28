package org.fossasia.openevent.app.core.presenter;

import com.raizlabs.android.dbflow.structure.BaseModel;

import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.attendee.AttendeeRepository;
import org.fossasia.openevent.app.data.db.DbFlowDatabaseChangeListener;
import org.fossasia.openevent.app.data.db.DatabaseChangeListener;
import org.fossasia.openevent.app.data.attendee.Attendee;
import org.fossasia.openevent.app.core.attendee.list.AttendeesPresenter;
import org.fossasia.openevent.app.core.attendee.list.AttendeesView;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
@SuppressWarnings("PMD.TooManyMethods")
public class AttendeePresenterTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock private AttendeesView attendeesView;
    @Mock private AttendeeRepository attendeeRepository;
    @Mock private DatabaseChangeListener<Attendee> changeListener;

    private static final long ID = 42;
    private AttendeesPresenter attendeesPresenter;

    private static final List<Attendee> ATTENDEES = Arrays.asList(
        Attendee.builder().id(12).build(),
        Attendee.builder().id(34).build(),
        Attendee.builder().id(56).build(),
        Attendee.builder().id(91).build(),
        Attendee.builder().id(29).build(),
        Attendee.builder().id(90).build(),
        Attendee.builder().id(123).build()
    );

    static {
        for (Attendee attendee : ATTENDEES) {
            attendee.setFirstname("testFirstName" + attendee.getId());
            attendee.setLastname("testLastName" + attendee.getId());
            attendee.setEmail("testEmail" + attendee.getId() + "@test.com");
        }
    }

    @Before
    public void setUp() {
        attendeesPresenter = new AttendeesPresenter(attendeeRepository, changeListener);
        attendeesPresenter.attach(ID, attendeesView);

        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldLoadAttendeesAutomatically() {
        when(attendeeRepository.getAttendees(ID, false))
            .thenReturn(Observable.fromIterable(ATTENDEES));
        when(changeListener.getNotifier()).thenReturn(PublishSubject.create());

        attendeesPresenter.start();

        verify(attendeeRepository).getAttendees(ID, false);
    }

    @Test
    public void shouldDetachViewOnStop() {
        when(attendeeRepository.getAttendees(ID, false))
            .thenReturn(Observable.fromIterable(ATTENDEES));
        when(changeListener.getNotifier()).thenReturn(PublishSubject.create());

        attendeesPresenter.start();

        assertNotNull(attendeesPresenter.getView());

        attendeesPresenter.detach();

        assertTrue(attendeesPresenter.getDisposable().isDisposed());
    }

    @Test
    public void shouldShowAttendeeError() {
        when(attendeeRepository.getAttendees(ID, false))
            .thenReturn(TestUtil.ERROR_OBSERVABLE);

        InOrder inOrder = Mockito.inOrder(attendeeRepository, attendeesView);

        attendeesPresenter.loadAttendees(false);

        inOrder.verify(attendeesView).showScanButton(false);
        inOrder.verify(attendeeRepository).getAttendees(ID, false);
        inOrder.verify(attendeesView).showProgress(true);
        inOrder.verify(attendeesView).showError(Logger.TEST_MESSAGE);
        inOrder.verify(attendeesView).showProgress(false);
    }

    @Test
    public void shouldLoadAttendeesSuccessfully() {
        when(attendeeRepository.getAttendees(ID, false))
            .thenReturn(Observable.fromIterable(ATTENDEES));

        InOrder inOrder = Mockito.inOrder(attendeeRepository, attendeesView);

        attendeesPresenter.loadAttendees(false);

        // TODO: Fix flaky test for ATTENDEES

        inOrder.verify(attendeesView).showScanButton(false);
        inOrder.verify(attendeeRepository).getAttendees(ID, false);
        inOrder.verify(attendeesView).showProgress(true);
        inOrder.verify(attendeesView).showResults(any());
        inOrder.verify(attendeesView).showScanButton(true);
        inOrder.verify(attendeesView).showProgress(false);
    }

    @Test
    public void shouldRefreshAttendeesSuccessfully() {
        when(attendeeRepository.getAttendees(ID, true))
            .thenReturn(Observable.fromIterable(ATTENDEES));

        InOrder inOrder = Mockito.inOrder(attendeeRepository, attendeesView);

        attendeesPresenter.loadAttendees(true);

        // TODO: Fix flaky test for ATTENDEES

        inOrder.verify(attendeesView).showScanButton(false);
        inOrder.verify(attendeeRepository).getAttendees(ID, true);
        inOrder.verify(attendeesView).showProgress(true);
        inOrder.verify(attendeesView).onRefreshComplete(true);
        inOrder.verify(attendeesView).showResults(any());
        inOrder.verify(attendeesView).showScanButton(true);
        inOrder.verify(attendeesView).showProgress(false);
    }

    @Test
    public void shouldShowEmptyViewOnNoItemAfterSwipeRefresh() {
        ArrayList<Attendee> attendees = new ArrayList<>();
        when(attendeeRepository.getAttendees(ID, true))
            .thenReturn(Observable.fromIterable(attendees));

        InOrder inOrder = Mockito.inOrder(attendeesView);

        attendeesPresenter.loadAttendees(true);

        inOrder.verify(attendeesView).showEmptyView(false);
        inOrder.verify(attendeesView).showResults(attendees);
        inOrder.verify(attendeesView).showEmptyView(true);
    }

    @Test
    public void shouldNotShowScanButtonOnNoItemAfterSwipeRefresh() {
        ArrayList<Attendee> attendees = new ArrayList<>();
        when(attendeeRepository.getAttendees(ID, true))
            .thenReturn(Observable.fromIterable(attendees));

        InOrder inOrder = Mockito.inOrder(attendeesView);

        attendeesPresenter.loadAttendees(true);

        inOrder.verify(attendeesView).showScanButton(false);
        inOrder.verify(attendeesView).onRefreshComplete(true);
        inOrder.verify(attendeesView).showScanButton(false);
    }

    @Test
    public void shouldShowEmptyViewOnSwipeRefreshError() {
        when(attendeeRepository.getAttendees(ID, true))
            .thenReturn(TestUtil.ERROR_OBSERVABLE);

        InOrder inOrder = Mockito.inOrder(attendeesView);

        attendeesPresenter.loadAttendees(true);

        inOrder.verify(attendeesView).showEmptyView(false);
        inOrder.verify(attendeesView).showError(Logger.TEST_MESSAGE);
        inOrder.verify(attendeesView).showEmptyView(true);
    }

    @Test
    public void shouldNotShowScanButtonOnSwipeRefreshError() {
        when(attendeeRepository.getAttendees(ID, true))
            .thenReturn(TestUtil.ERROR_OBSERVABLE);

        InOrder inOrder = Mockito.inOrder(attendeesView);

        attendeesPresenter.loadAttendees(true);

        inOrder.verify(attendeesView).showScanButton(false);
        inOrder.verify(attendeesView).onRefreshComplete(false);
        inOrder.verify(attendeesView).showScanButton(false);
    }

    @Test
    public void shouldNotShowEmptyViewIfNonEmptyAttendeeListOnSwipeRefreshError() {
        attendeesPresenter.setAttendeeList(ATTENDEES);

        when(attendeeRepository.getAttendees(ID, true))
            .thenReturn(TestUtil.ERROR_OBSERVABLE);

        InOrder inOrder = Mockito.inOrder(attendeesView);

        attendeesPresenter.loadAttendees(true);

        inOrder.verify(attendeesView).showEmptyView(false);
        inOrder.verify(attendeesView).showError(Logger.TEST_MESSAGE);
        inOrder.verify(attendeesView).showEmptyView(false);
    }

    @Test
    public void shouldShowScanButtonIfNonEmptyAttendeeListOnSwipeRefreshError() {
        attendeesPresenter.setAttendeeList(ATTENDEES);

        when(attendeeRepository.getAttendees(ID, true))
            .thenReturn(Observable.error(Logger.TEST_ERROR));

        InOrder inOrder = Mockito.inOrder(attendeesView);

        attendeesPresenter.loadAttendees(true);

        inOrder.verify(attendeesView).showScanButton(false);
        inOrder.verify(attendeesView).onRefreshComplete(false);
        inOrder.verify(attendeesView).showScanButton(true);
    }

    @Test
    public void shouldNotShowEmptyViewOnSwipeRefreshSuccess() {
        when(attendeeRepository.getAttendees(ID, true))
            .thenReturn(Observable.fromIterable(ATTENDEES));

        InOrder inOrder = Mockito.inOrder(attendeesView);

        attendeesPresenter.loadAttendees(true);

        // TODO: Fix flaky test for ATTENDEES

        inOrder.verify(attendeesView).showEmptyView(false);
        inOrder.verify(attendeesView).showResults(any());
        inOrder.verify(attendeesView).showEmptyView(false);
    }

    @Test
    public void shouldShowScanButtonOnSwipeRefreshSuccess() {
        when(attendeeRepository.getAttendees(ID, true))
            .thenReturn(Observable.fromIterable(ATTENDEES));

        InOrder inOrder = Mockito.inOrder(attendeesView);

        attendeesPresenter.loadAttendees(true);

        inOrder.verify(attendeesView).showScanButton(false);
        inOrder.verify(attendeesView).onRefreshComplete(true);
        inOrder.verify(attendeesView).showScanButton(true);
    }

    @Test
    public void shouldRefreshAttendeesOnError() {
        when(attendeeRepository.getAttendees(ID, true))
            .thenReturn(TestUtil.ERROR_OBSERVABLE);

        InOrder inOrder = Mockito.inOrder(attendeeRepository, attendeesView);

        attendeesPresenter.loadAttendees(true);

        inOrder.verify(attendeesView).showScanButton(false);
        inOrder.verify(attendeeRepository).getAttendees(ID, true);
        inOrder.verify(attendeesView).showProgress(true);
        inOrder.verify(attendeesView).showError(anyString());
        inOrder.verify(attendeesView).onRefreshComplete(false);
        inOrder.verify(attendeesView).showProgress(false);
    }

    @Test
    public void shouldToggleAttendeesSuccessfully() {
        PublishSubject<DbFlowDatabaseChangeListener.ModelChange<Attendee>> publishSubject = PublishSubject.create();

        when(attendeeRepository.getAttendees(ID, false)).thenReturn(Observable.fromIterable(ATTENDEES));
        when(attendeeRepository.getAttendee(ATTENDEES.get(2).getId(), false)).thenReturn(Observable.just(ATTENDEES.get(2)));
        when(changeListener.getNotifier()).thenReturn(publishSubject);

        attendeesPresenter.start();
        publishSubject.onNext(new DbFlowDatabaseChangeListener.ModelChange<>(ATTENDEES.get(2), BaseModel.Action.UPDATE));

        verify(attendeesView).updateAttendee(ATTENDEES.get(2));
    }

}
