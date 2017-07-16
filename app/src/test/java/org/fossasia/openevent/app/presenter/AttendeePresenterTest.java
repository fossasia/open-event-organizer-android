package org.fossasia.openevent.app.presenter;

import com.raizlabs.android.dbflow.structure.BaseModel;

import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.db.DatabaseChangeListener;
import org.fossasia.openevent.app.data.db.contract.IDatabaseChangeListener;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.data.repository.contract.IAttendeeRepository;
import org.fossasia.openevent.app.event.attendees.AttendeesPresenter;
import org.fossasia.openevent.app.event.attendees.contract.IAttendeesView;
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

import static org.fossasia.openevent.app.presenter.Util.ERROR_OBSERVABLE;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class AttendeePresenterTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    IAttendeesView attendeesView;

    @Mock
    IAttendeeRepository attendeeRepository;

    @Mock
    IDatabaseChangeListener<Attendee> changeListener;

    private final long id = 42;
    private AttendeesPresenter attendeesPresenter;

    private List<Attendee> attendees = Arrays.asList(
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
        attendeesPresenter.attach(id, attendeesView);

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
        when(attendeeRepository.getAttendees(id, false))
            .thenReturn(Observable.fromIterable(attendees));
        when(changeListener.getNotifier()).thenReturn(PublishSubject.create());

        attendeesPresenter.start();

        verify(attendeeRepository).getAttendees(id, false);
    }

    @Test
    public void shouldDetachViewOnStop() {
        when(attendeeRepository.getAttendees(id, false))
            .thenReturn(Observable.fromIterable(attendees));
        when(changeListener.getNotifier()).thenReturn(PublishSubject.create());

        attendeesPresenter.start();

        assertNotNull(attendeesPresenter.getView());

        attendeesPresenter.detach();

        assertNull(attendeesPresenter.getView());
    }

    @Test
    public void shouldShowAttendeeError() {
        when(attendeeRepository.getAttendees(id, false))
            .thenReturn(ERROR_OBSERVABLE);

        InOrder inOrder = Mockito.inOrder(attendeeRepository, attendeesView);

        attendeesPresenter.loadAttendees(false);

        inOrder.verify(attendeesView).showScanButton(false);
        inOrder.verify(attendeeRepository).getAttendees(id, false);
        inOrder.verify(attendeesView).showProgress(true);
        inOrder.verify(attendeesView).showError(Logger.TEST_MESSAGE);
        inOrder.verify(attendeesView).showProgress(false);
    }

    @Test
    public void shouldLoadAttendeesSuccessfully() {
        when(attendeeRepository.getAttendees(id, false))
            .thenReturn(Observable.fromIterable(attendees));

        InOrder inOrder = Mockito.inOrder(attendeeRepository, attendeesView);

        attendeesPresenter.loadAttendees(false);

        // TODO: Fix flaky test for attendees

        inOrder.verify(attendeesView).showScanButton(false);
        inOrder.verify(attendeeRepository).getAttendees(id, false);
        inOrder.verify(attendeesView).showProgress(true);
        inOrder.verify(attendeesView).showResults(any());
        inOrder.verify(attendeesView).showScanButton(true);
        inOrder.verify(attendeesView).showProgress(false);
    }

    @Test
    public void shouldRefreshAttendeesSuccessfully() {
        when(attendeeRepository.getAttendees(id, true))
            .thenReturn(Observable.fromIterable(attendees));

        InOrder inOrder = Mockito.inOrder(attendeeRepository, attendeesView);

        attendeesPresenter.loadAttendees(true);

        // TODO: Fix flaky test for attendees

        inOrder.verify(attendeesView).showScanButton(false);
        inOrder.verify(attendeeRepository).getAttendees(id, true);
        inOrder.verify(attendeesView).showProgress(true);
        inOrder.verify(attendeesView).showResults(any());
        inOrder.verify(attendeesView).showScanButton(true);
        inOrder.verify(attendeesView).onRefreshComplete();
        inOrder.verify(attendeesView).showProgress(false);
    }

    @Test
    public void shouldShowEmptyViewOnNoItemAfterSwipeRefresh() {
        ArrayList<Attendee> attendees = new ArrayList<>();
        when(attendeeRepository.getAttendees(id, true))
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
        when(attendeeRepository.getAttendees(id, true))
            .thenReturn(Observable.fromIterable(attendees));

        InOrder inOrder = Mockito.inOrder(attendeesView);

        attendeesPresenter.loadAttendees(true);

        inOrder.verify(attendeesView).showScanButton(false);
        inOrder.verify(attendeesView).showScanButton(false);
        inOrder.verify(attendeesView).onRefreshComplete();
    }

    @Test
    public void shouldShowEmptyViewOnSwipeRefreshError() {
        when(attendeeRepository.getAttendees(id, true))
            .thenReturn(ERROR_OBSERVABLE);

        InOrder inOrder = Mockito.inOrder(attendeesView);

        attendeesPresenter.loadAttendees(true);

        inOrder.verify(attendeesView).showEmptyView(false);
        inOrder.verify(attendeesView).showError(Logger.TEST_MESSAGE);
        inOrder.verify(attendeesView).showEmptyView(true);
    }

    @Test
    public void shouldNotShowScanButtonOnSwipeRefreshError() {
        when(attendeeRepository.getAttendees(id, true))
            .thenReturn(ERROR_OBSERVABLE);

        InOrder inOrder = Mockito.inOrder(attendeesView);

        attendeesPresenter.loadAttendees(true);

        inOrder.verify(attendeesView).showScanButton(false);
        inOrder.verify(attendeesView).showScanButton(false);
        inOrder.verify(attendeesView).onRefreshComplete();
    }

    @Test
    public void shouldNotShowEmptyViewIfNonEmptyAttendeeListOnSwipeRefreshError() {
        attendeesPresenter.setAttendeeList(attendees);

        when(attendeeRepository.getAttendees(id, true))
            .thenReturn(ERROR_OBSERVABLE);

        InOrder inOrder = Mockito.inOrder(attendeesView);

        attendeesPresenter.loadAttendees(true);

        inOrder.verify(attendeesView).showEmptyView(false);
        inOrder.verify(attendeesView).showError(Logger.TEST_MESSAGE);
        inOrder.verify(attendeesView).showEmptyView(false);
    }

    @Test
    public void shouldShowScanButtonIfNonEmptyAttendeeListOnSwipeRefreshError() {
        attendeesPresenter.setAttendeeList(attendees);

        when(attendeeRepository.getAttendees(id, true))
            .thenReturn(Observable.error(Logger.TEST_ERROR));

        InOrder inOrder = Mockito.inOrder(attendeesView);

        attendeesPresenter.loadAttendees(true);

        inOrder.verify(attendeesView).showScanButton(false);
        inOrder.verify(attendeesView).showScanButton(true);
        inOrder.verify(attendeesView).onRefreshComplete();
    }

    @Test
    public void shouldNotShowEmptyViewOnSwipeRefreshSuccess() {
        when(attendeeRepository.getAttendees(id, true))
            .thenReturn(Observable.fromIterable(attendees));

        InOrder inOrder = Mockito.inOrder(attendeesView);

        attendeesPresenter.loadAttendees(true);

        // TODO: Fix flaky test for attendees

        inOrder.verify(attendeesView).showEmptyView(false);
        inOrder.verify(attendeesView).showResults(any());
        inOrder.verify(attendeesView).showEmptyView(false);
    }

    @Test
    public void shouldShowScanButtonOnSwipeRefreshSuccess() {
        when(attendeeRepository.getAttendees(id, true))
            .thenReturn(Observable.fromIterable(attendees));

        InOrder inOrder = Mockito.inOrder(attendeesView);

        attendeesPresenter.loadAttendees(true);

        inOrder.verify(attendeesView).showScanButton(false);
        inOrder.verify(attendeesView).showScanButton(true);
        inOrder.verify(attendeesView).onRefreshComplete();
    }

    @Test
    public void shouldRefreshAttendeesOnError() {
        when(attendeeRepository.getAttendees(id, true))
            .thenReturn(ERROR_OBSERVABLE);

        InOrder inOrder = Mockito.inOrder(attendeeRepository, attendeesView);

        attendeesPresenter.loadAttendees(true);

        inOrder.verify(attendeesView).showScanButton(false);
        inOrder.verify(attendeeRepository).getAttendees(id, true);
        inOrder.verify(attendeesView).showProgress(true);
        inOrder.verify(attendeesView).showError(anyString());
        inOrder.verify(attendeesView).onRefreshComplete();
        inOrder.verify(attendeesView).showProgress(false);
    }

    @Test
    public void shouldToggleAttendeesSuccessfully() {
        PublishSubject<DatabaseChangeListener.ModelChange<Attendee>> publishSubject = PublishSubject.create();

        when(attendeeRepository.getAttendees(id, false)).thenReturn(Observable.fromIterable(attendees));
        when(changeListener.getNotifier()).thenReturn(publishSubject);

        attendeesPresenter.start();
        publishSubject.onNext(new DatabaseChangeListener.ModelChange<>(attendees.get(2), BaseModel.Action.UPDATE));

        Mockito.verify(attendeesView).updateAttendee(attendees.get(2));
    }

    @Test
    public void shouldNotAccessView() {
        attendeesPresenter.detach();

        attendeesPresenter.loadAttendees(false);

        Mockito.verifyZeroInteractions(attendeesView);
    }
}
