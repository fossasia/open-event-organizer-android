package org.fossasia.openevent.app.presenter;

import org.fossasia.openevent.app.data.contract.IEventRepository;
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
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class AttendeePresenterTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    IAttendeesView attendeesView;

    @Mock
    IEventRepository eventModel;

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
        attendeesPresenter = new AttendeesPresenter(eventModel);
        attendeesPresenter.attach(id, attendeesView);
        attendeesPresenter.setAttendeeList(attendees);

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
        when(eventModel.getAttendees(id, false))
            .thenReturn(Observable.just(attendees));

        attendeesPresenter.start();

        verify(eventModel).getAttendees(id, false);
    }

    @Test
    public void shouldDetachViewOnStop() {
        when(eventModel.getAttendees(id, false))
            .thenReturn(Observable.just(attendees));

        attendeesPresenter.start();

        assertNotNull(attendeesPresenter.getView());

        attendeesPresenter.detach();

        assertNull(attendeesPresenter.getView());
    }

    @Test
    public void shouldShowAttendeeError() {
        String error = "Test Error";
        when(eventModel.getAttendees(id, false))
            .thenReturn(Observable.error(new Throwable(error)));

        InOrder inOrder = Mockito.inOrder(eventModel, attendeesView);

        attendeesPresenter.loadAttendees(false);

        inOrder.verify(attendeesView).showProgressBar(true);
        inOrder.verify(attendeesView).showScanButton(false);
        inOrder.verify(eventModel).getAttendees(id, false);
        inOrder.verify(attendeesView).showErrorMessage(error);
        inOrder.verify(attendeesView).showProgressBar(false);
    }

    @Test
    public void shouldLoadAttendeesSuccessfully() {
        when(eventModel.getAttendees(id, false))
            .thenReturn(Observable.just(attendees));

        InOrder inOrder = Mockito.inOrder(eventModel, attendeesView);

        attendeesPresenter.loadAttendees(false);

        inOrder.verify(attendeesView).showProgressBar(true);
        inOrder.verify(attendeesView).showScanButton(false);
        inOrder.verify(eventModel).getAttendees(id, false);
        inOrder.verify(attendeesView).showAttendees(attendees);
        inOrder.verify(attendeesView).showProgressBar(false);
        inOrder.verify(attendeesView).showScanButton(true);
    }

    @Test
    public void shouldToggleAttendeesSuccessfully() {
        when(eventModel.toggleAttendeeCheckStatus(id, 56))
            .thenReturn(Observable.just(attendees.get(2)));

        InOrder inOrder = Mockito.inOrder(eventModel, attendeesView);

        // Set the list to search into
        attendeesPresenter.setAttendeeList(attendees);
        attendeesPresenter.toggleAttendeeCheckStatus(attendees.get(2));

        inOrder.verify(attendeesView).showProgressBar(true);
        inOrder.verify(eventModel).toggleAttendeeCheckStatus(id, 56);
        inOrder.verify(attendeesView).updateAttendee(2, attendees.get(2));
        inOrder.verify(attendeesView).showProgressBar(false);
    }

    @Test
    public void shouldToggleSortedAttendeesSuccessfully() {
        Attendee targetAttendee = attendees.get(3);

        when(eventModel.toggleAttendeeCheckStatus(id, targetAttendee.getId()))
            .thenReturn(Observable.just(targetAttendee));

        // Set the shuffled list to search into
        List<Attendee> shuffled = new ArrayList<>(attendees);
        Collections.shuffle(shuffled);

        attendeesPresenter.setAttendeeList(shuffled);
        attendeesPresenter.toggleAttendeeCheckStatus(targetAttendee);


        Mockito.verify(attendeesView).updateAttendee(shuffled.indexOf(targetAttendee), targetAttendee);
    }

    @Test
    public void shouldFailOnToggleNetworkError() {
        String error = "Test Error";
        when(eventModel.toggleAttendeeCheckStatus(id, 56))
            .thenReturn(Observable.error(new Throwable(error)));

        InOrder inOrder = Mockito.inOrder(eventModel, attendeesView);

        // Set the list to search into
        attendeesPresenter.setAttendeeList(attendees);
        attendeesPresenter.toggleAttendeeCheckStatus(attendees.get(2));

        inOrder.verify(attendeesView).showProgressBar(true);
        inOrder.verify(eventModel).toggleAttendeeCheckStatus(id, 56);
        inOrder.verify(attendeesView).showErrorMessage(error);
        inOrder.verify(attendeesView).showProgressBar(false);
    }

    @Test
    public void shouldShowErrorForItemNotFound() {
        String error = "Error in updating Attendee";
        when(eventModel.toggleAttendeeCheckStatus(id, 56))
            .thenReturn(Observable.just(new Attendee(23)));

        InOrder inOrder = Mockito.inOrder(eventModel, attendeesView);

        attendeesPresenter.toggleAttendeeCheckStatus(attendees.get(2));

        inOrder.verify(attendeesView).showProgressBar(true);
        inOrder.verify(eventModel).toggleAttendeeCheckStatus(id, 56);
        inOrder.verify(attendeesView).showErrorMessage(error);
        inOrder.verify(attendeesView).showProgressBar(false);
    }

    @Test
    public void shouldNotAccessView() {
        attendeesPresenter.detach();

        attendeesPresenter.loadAttendees(false);
        attendeesPresenter.toggleAttendeeCheckStatus(attendees.get(1));

        Mockito.verifyZeroInteractions(attendeesView);
    }
}
