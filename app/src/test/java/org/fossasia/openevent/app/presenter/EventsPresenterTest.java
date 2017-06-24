package org.fossasia.openevent.app.presenter;

import org.fossasia.openevent.app.data.contract.IEventRepository;
import org.fossasia.openevent.app.data.contract.ILoginModel;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.models.User;
import org.fossasia.openevent.app.data.models.UserDetail;
import org.fossasia.openevent.app.events.EventsPresenter;
import org.fossasia.openevent.app.events.contract.IEventsView;
import org.fossasia.openevent.app.utils.DateUtils;
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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class EventsPresenterTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    IEventsView eventListView;

    @Mock
    IEventRepository eventModel;

    @Mock
    ILoginModel loginModel;

    private EventsPresenter eventsActivityPresenter;

    private DateUtils dateUtils = new DateUtils();
    private String date = dateUtils.format(new Date());

    private List<Event> eventList = Arrays.asList(
        new Event(12, date, date),
        new Event(13, date, date),
        new Event(14, date, date)
    );

    private User organiser = new User();

    @Before
    public void setUp() {
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());

        eventsActivityPresenter = new EventsPresenter(eventModel, loginModel);
        eventsActivityPresenter.attach(eventListView);
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
    }

    @Test
    public void shouldLoadEventsAndOrganiserAutomatically() {
        when(eventModel.getOrganiser(false))
            .thenReturn(Observable.just(organiser));

        when(eventModel.getEvents(false))
            .thenReturn(Observable.fromIterable(eventList));

        eventsActivityPresenter.start();

        verify(eventModel).getEvents(false);
        verify(eventModel).getOrganiser(false);
    }

    @Test
    public void shouldDetachViewOnStop() {
        assertNotNull(eventsActivityPresenter.getView());

        eventsActivityPresenter.detach();

        assertNull(eventsActivityPresenter.getView());
    }

    @Test
    public void shouldLoadEventsSuccessfully() {
        when(eventModel.getEvents(false))
            .thenReturn(Observable.fromIterable(eventList));

        InOrder inOrder = Mockito.inOrder(eventModel, eventListView);

        eventsActivityPresenter.loadUserEvents(false);

        inOrder.verify(eventListView).showProgressBar(true);
        inOrder.verify(eventModel).getEvents(false);
        inOrder.verify(eventListView).showEvents(eventList);
        inOrder.verify(eventListView).showProgressBar(false);
    }

    @Test
    public void shouldRefreshEventsSuccessfully() {
        when(eventModel.getEvents(true))
            .thenReturn(Observable.fromIterable(eventList));

        InOrder inOrder = Mockito.inOrder(eventModel, eventListView);

        eventsActivityPresenter.loadUserEvents(true);

        inOrder.verify(eventListView).showProgressBar(true);
        inOrder.verify(eventModel).getEvents(true);
        inOrder.verify(eventListView).showEvents(eventList);
        inOrder.verify(eventListView).showProgressBar(false);
        inOrder.verify(eventListView).onRefreshComplete();
    }

    @Test
    public void shouldRefreshEventsOnError() {
        when(eventModel.getEvents(true))
            .thenReturn(Observable.error(new Throwable("Error")));

        InOrder inOrder = Mockito.inOrder(eventModel, eventListView);

        eventsActivityPresenter.loadUserEvents(true);

        inOrder.verify(eventListView).showProgressBar(true);
        inOrder.verify(eventModel).getEvents(true);
        inOrder.verify(eventListView).showEventError(anyString());
        inOrder.verify(eventListView).showProgressBar(false);
        inOrder.verify(eventListView).onRefreshComplete();
    }

    @Test
    public void shouldNotLoadInitialEventIfNotTwoPane() {
        when(eventModel.getEvents(false))
            .thenReturn(Observable.fromIterable(eventList));
        when(eventListView.isTwoPane())
            .thenReturn(false);

        eventsActivityPresenter.loadUserEvents(false);

        Mockito.verify(eventListView, never()).showInitialEvent();
    }

    @Test
    public void shouldLoadInitialEventFirstTimeIfTwoPane() {
        when(eventModel.getEvents(false))
            .thenReturn(Observable.fromIterable(eventList));
        when(eventListView.isTwoPane())
            .thenReturn(true);

        eventsActivityPresenter.loadUserEvents(false);

        Mockito.verify(eventListView).showInitialEvent();
    }

    @Test
    public void shouldNotLoadInitialEventSecondTimeIfTwoPane() {
        when(eventModel.getEvents(false))
            .thenReturn(Observable.fromIterable(eventList));
        when(eventListView.isTwoPane())
            .thenReturn(true);

        eventsActivityPresenter.loadUserEvents(false);
        eventsActivityPresenter.loadUserEvents(false);

        Mockito.verify(eventListView, atMost(1)).showInitialEvent();
    }

    @Test
    public void shouldShowEventError() {
        String error = "Test Error";
        when(eventModel.getEvents(false))
            .thenReturn(Observable.error(new Throwable(error)));

        InOrder inOrder = Mockito.inOrder(eventModel, eventListView);

        eventsActivityPresenter.loadUserEvents(false);

        inOrder.verify(eventListView).showProgressBar(true);
        inOrder.verify(eventModel).getEvents(false);
        inOrder.verify(eventListView).showEventError(error);
        inOrder.verify(eventListView).showProgressBar(false);
    }

    @Test
    public void shouldLoadOrganiserSuccessfully() {
        UserDetail userDetail = new UserDetail();
        userDetail.setFirstName("John");
        userDetail.setLastName("Wick");

        organiser.setUserDetail(userDetail);

        when(eventModel.getOrganiser(false)).thenReturn(Observable.just(organiser));

        InOrder inOrder = Mockito.inOrder(eventModel, eventListView);

        eventsActivityPresenter.loadOrganiser(false);

        inOrder.verify(eventModel).getOrganiser(false);
        inOrder.verify(eventListView).showOrganiserName("John Wick");
    }

    @Test
    public void testOrganiserNameRendering() {
        UserDetail userDetail = new UserDetail();
        userDetail.setFirstName("John");
        userDetail.setLastName("Wick");

        organiser.setUserDetail(userDetail);

        when(eventModel.getOrganiser(false)).thenReturn(Observable.just(organiser));

        eventsActivityPresenter.loadOrganiser(false);
        verify(eventListView).showOrganiserName("John Wick");

        userDetail.setFirstName("John");
        userDetail.setLastName(null);

        eventsActivityPresenter.loadOrganiser(false);
        verify(eventListView).showOrganiserName("John");

        userDetail.setFirstName(null);
        userDetail.setLastName("Wick");

        eventsActivityPresenter.loadOrganiser(false);
        verify(eventListView).showOrganiserName("Wick");

        userDetail.setFirstName(null);
        userDetail.setLastName(null);

        eventsActivityPresenter.loadOrganiser(false);
        verify(eventListView).showOrganiserName("");
    }

    @Test
    public void shouldShowOrganiserError() {
        String error = "Test Error";
        when(eventModel.getOrganiser(false))
            .thenReturn(Observable.error(new Throwable(error)));

        InOrder inOrder = Mockito.inOrder(eventModel, eventListView);

        eventsActivityPresenter.loadOrganiser(false);

        inOrder.verify(eventModel).getOrganiser(false);
        inOrder.verify(eventListView).showOrganiserLoadError(error);
    }

    @Test
    public void shouldLogout() {

        TestObserver testObserver = TestObserver.create();
        Completable completable = Completable.complete()
            .doOnSubscribe(testObserver::onSubscribe);

        when(loginModel.logout()).thenReturn(completable);

        eventsActivityPresenter.logout();

        testObserver.assertSubscribed();
        verify(loginModel).logout();
        verify(eventListView).onLogout();
    }

    @Test
    public void shouldNotAccessView() {
        eventsActivityPresenter.detach();

        eventsActivityPresenter.loadUserEvents(false);
        eventsActivityPresenter.loadOrganiser(false);
        eventsActivityPresenter.logout();

        verifyNoMoreInteractions(eventListView);
    }

}
