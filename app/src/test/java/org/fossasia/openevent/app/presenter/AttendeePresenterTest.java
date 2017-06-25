package org.fossasia.openevent.app.presenter;

import com.raizlabs.android.dbflow.structure.BaseModel;

import org.fossasia.openevent.app.data.contract.IEventRepository;
import org.fossasia.openevent.app.data.db.DatabaseChangeListener;
import org.fossasia.openevent.app.data.db.contract.IDatabaseChangeListener;
import org.fossasia.openevent.app.data.models.Attendee;
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
    IEventRepository eventRepository;

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
        attendeesPresenter = new AttendeesPresenter(eventRepository, changeListener);
        attendeesPresenter.attach(id, attendeesView);

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
        when(eventRepository.getAttendees(id, false))
            .thenReturn(Observable.fromIterable(attendees));
        when(changeListener.getNotifier()).thenReturn(PublishSubject.create());

        attendeesPresenter.start();

        verify(eventRepository).getAttendees(id, false);
    }

    @Test
    public void shouldDetachViewOnStop() {
        when(eventRepository.getAttendees(id, false))
            .thenReturn(Observable.fromIterable(attendees));
        when(changeListener.getNotifier()).thenReturn(PublishSubject.create());

        attendeesPresenter.start();

        assertNotNull(attendeesPresenter.getView());

        attendeesPresenter.detach();

        assertNull(attendeesPresenter.getView());
    }

    @Test
    public void shouldShowAttendeeError() {
        String error = "Test Error";
        when(eventRepository.getAttendees(id, false))
            .thenReturn(Observable.error(new Throwable(error)));

        InOrder inOrder = Mockito.inOrder(eventRepository, attendeesView);

        attendeesPresenter.loadAttendees(false);

        inOrder.verify(attendeesView).showProgressBar(true);
        inOrder.verify(attendeesView).showScanButton(false);
        inOrder.verify(eventRepository).getAttendees(id, false);
        inOrder.verify(attendeesView).showErrorMessage(error);
        inOrder.verify(attendeesView).showProgressBar(false);
    }

    @Test
    public void shouldLoadAttendeesSuccessfully() {
        when(eventRepository.getAttendees(id, false))
            .thenReturn(Observable.fromIterable(attendees));

        InOrder inOrder = Mockito.inOrder(eventRepository, attendeesView);

        attendeesPresenter.loadAttendees(false);

        inOrder.verify(attendeesView).showProgressBar(true);
        inOrder.verify(attendeesView).showScanButton(false);
        inOrder.verify(eventRepository).getAttendees(id, false);
        inOrder.verify(attendeesView).showAttendees(attendees);
        inOrder.verify(attendeesView).showProgressBar(false);
        inOrder.verify(attendeesView).showScanButton(true);
    }

    @Test
    public void shouldRefreshAttendeesSuccessfully() {
        when(eventRepository.getAttendees(id, true))
            .thenReturn(Observable.fromIterable(attendees));

        InOrder inOrder = Mockito.inOrder(eventRepository, attendeesView);

        attendeesPresenter.loadAttendees(true);

        inOrder.verify(attendeesView).showProgressBar(true);
        inOrder.verify(attendeesView).showScanButton(false);
        inOrder.verify(eventRepository).getAttendees(id, true);
        inOrder.verify(attendeesView).showAttendees(attendees);
        inOrder.verify(attendeesView).showProgressBar(false);
        inOrder.verify(attendeesView).onRefreshComplete();
        inOrder.verify(attendeesView).showScanButton(true);
    }

    @Test
    public void shouldShowEmptyViewOnNoItemAfterSwipeRefresh() {
        ArrayList<Attendee> attendees = new ArrayList<>();
        when(eventModel.getAttendees(id, true))
            .thenReturn(Observable.fromIterable(attendees));

        InOrder inOrder = Mockito.inOrder(eventModel, attendeesView);

        attendeesPresenter.loadAttendees(true);

        inOrder.verify(attendeesView).showEmptyView(false);
        inOrder.verify(eventModel).getAttendees(id, true);
        inOrder.verify(attendeesView).showAttendees(attendees);
        inOrder.verify(attendeesView).showEmptyView(true);
        inOrder.verify(attendeesView).onRefreshComplete();
    }

    @Test
    public void shouldShowEmptyViewOnSwipeRefreshError() {
        String error = "Test Error";
        when(eventModel.getAttendees(id, true))
            .thenReturn(Observable.error(new Throwable(error)));

        InOrder inOrder = Mockito.inOrder(eventModel, attendeesView);

        attendeesPresenter.loadAttendees(true);

        inOrder.verify(attendeesView).showEmptyView(false);
        inOrder.verify(eventModel).getAttendees(id, true);
        inOrder.verify(attendeesView).showErrorMessage(error);
        inOrder.verify(attendeesView).showEmptyView(true);
        inOrder.verify(attendeesView).onRefreshComplete();
    }

    @Test
    public void shouldNotShowEmptyViewIfNonEmptyAttendeesListOnSwipeRefreshError() {
        attendeesPresenter.setAttendeeList(attendees);

        String error = "Test Error";
        when(eventModel.getAttendees(id, true))
            .thenReturn(Observable.error(new Throwable(error)));

        InOrder inOrder = Mockito.inOrder(eventModel, attendeesView);

        attendeesPresenter.loadAttendees(true);

        inOrder.verify(attendeesView).showEmptyView(false);
        inOrder.verify(eventModel).getAttendees(id, true);
        inOrder.verify(attendeesView).showErrorMessage(error);
        inOrder.verify(attendeesView).showEmptyView(false);
        inOrder.verify(attendeesView).onRefreshComplete();
    }

    @Test
    public void shouldNotShowEmptyViewOnSwipeRefreshSuccess() {
        when(eventModel.getAttendees(id, true))
            .thenReturn(Observable.fromIterable(attendees));

        InOrder inOrder = Mockito.inOrder(eventModel, attendeesView);

        attendeesPresenter.loadAttendees(true);

        inOrder.verify(attendeesView).showEmptyView(false);
        inOrder.verify(eventModel).getAttendees(id, true);
        inOrder.verify(attendeesView).showAttendees(attendees);
        inOrder.verify(attendeesView).showEmptyView(false);
        inOrder.verify(attendeesView).onRefreshComplete();
    }

    @Test
    public void shouldRefreshAttendeesOnError() {
        when(eventRepository.getAttendees(id, true))
            .thenReturn(Observable.error(new Throwable("Error")));

        InOrder inOrder = Mockito.inOrder(eventRepository, attendeesView);

        attendeesPresenter.loadAttendees(true);

        inOrder.verify(attendeesView).showProgressBar(true);
        inOrder.verify(attendeesView).showScanButton(false);
        inOrder.verify(eventRepository).getAttendees(id, true);
        inOrder.verify(attendeesView).showErrorMessage(anyString());
        inOrder.verify(attendeesView).showProgressBar(false);
        inOrder.verify(attendeesView).onRefreshComplete();
    }

    @Test
    public void shouldToggleAttendeesSuccessfully() {
        PublishSubject<DatabaseChangeListener.ModelChange<Attendee>> publishSubject = PublishSubject.create();

        when(eventRepository.getAttendees(id, false)).thenReturn(Observable.fromIterable(attendees));
        when(changeListener.getNotifier()).thenReturn(publishSubject);

        attendeesPresenter.start();
        publishSubject.onNext(new DatabaseChangeListener.ModelChange<>(attendees.get(2), BaseModel.Action.UPDATE));

        InOrder inOrder = Mockito.inOrder(eventRepository, attendeesView);

        inOrder.verify(attendeesView).showProgressBar(true);
        inOrder.verify(attendeesView).updateAttendee(4, attendees.get(2));
        inOrder.verify(attendeesView).showProgressBar(false);
    }

    @Test
    public void shouldShowErrorForItemNotFound() {
        String error = "Error in updating Attendee";
        PublishSubject<DatabaseChangeListener.ModelChange<Attendee>> publishSubject = PublishSubject.create();

        when(eventRepository.getAttendees(id, false)).thenReturn(Observable.fromIterable(attendees));
        when(changeListener.getNotifier()).thenReturn(publishSubject);

        attendeesPresenter.start();
        publishSubject.onNext(new DatabaseChangeListener.ModelChange<>(new Attendee(23), BaseModel.Action.UPDATE));

        InOrder inOrder = Mockito.inOrder(eventRepository, attendeesView);

        inOrder.verify(attendeesView).showProgressBar(true);
        inOrder.verify(attendeesView).showErrorMessage(error);
        inOrder.verify(attendeesView).showProgressBar(false);
    }

    @Test
    public void shouldNotAccessView() {
        attendeesPresenter.detach();

        attendeesPresenter.loadAttendees(false);
        //attendeesPresenter.toggleAttendeeCheckStatus(attendees.get(1));

        Mockito.verifyZeroInteractions(attendeesView);
    }
}
