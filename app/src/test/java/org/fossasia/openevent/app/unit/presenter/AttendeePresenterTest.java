package org.fossasia.openevent.app.unit.presenter;

import com.raizlabs.android.dbflow.structure.BaseModel;

import org.fossasia.openevent.app.common.app.rx.Logger;
import org.fossasia.openevent.app.common.data.db.DatabaseChangeListener;
import org.fossasia.openevent.app.common.data.db.contract.IDatabaseChangeListener;
import org.fossasia.openevent.app.common.data.models.Attendee;
import org.fossasia.openevent.app.common.data.repository.contract.IAttendeeRepository;
import org.fossasia.openevent.app.module.attendee.list.AttendeesPresenter;
import org.fossasia.openevent.app.module.attendee.list.contract.IAttendeesView;
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

import static org.fossasia.openevent.app.unit.presenter.Util.ERROR_OBSERVABLE;
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
    @Mock private IAttendeesView attendeesView;
    @Mock private IAttendeeRepository attendeeRepository;
    @Mock private IDatabaseChangeListener<Attendee> changeListener;

    private static final long ID = 42;
    private AttendeesPresenter attendeesPresenter;

    private static final List<Attendee> ATTENDEES = Arrays.asList(
        new Attendee(12),
        new Attendee(34),
        new Attendee(56),
        new Attendee(91),
        new Attendee(29),
        new Attendee(90),
        new Attendee(123)
    );

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
            .thenReturn(ERROR_OBSERVABLE);

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
            .thenReturn(ERROR_OBSERVABLE);

        InOrder inOrder = Mockito.inOrder(attendeesView);

        attendeesPresenter.loadAttendees(true);

        inOrder.verify(attendeesView).showEmptyView(false);
        inOrder.verify(attendeesView).showError(Logger.TEST_MESSAGE);
        inOrder.verify(attendeesView).showEmptyView(true);
    }

    @Test
    public void shouldNotShowScanButtonOnSwipeRefreshError() {
        when(attendeeRepository.getAttendees(ID, true))
            .thenReturn(ERROR_OBSERVABLE);

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
            .thenReturn(ERROR_OBSERVABLE);

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
            .thenReturn(ERROR_OBSERVABLE);

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
        PublishSubject<DatabaseChangeListener.ModelChange<Attendee>> publishSubject = PublishSubject.create();

        when(attendeeRepository.getAttendees(ID, false)).thenReturn(Observable.fromIterable(ATTENDEES));
        when(changeListener.getNotifier()).thenReturn(publishSubject);

        attendeesPresenter.start();
        publishSubject.onNext(new DatabaseChangeListener.ModelChange<>(ATTENDEES.get(2), BaseModel.Action.UPDATE));

        verify(attendeesView).updateAttendee(ATTENDEES.get(2));
    }

}
