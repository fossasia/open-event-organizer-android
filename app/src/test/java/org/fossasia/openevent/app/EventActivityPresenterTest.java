package org.fossasia.openevent.app;

import org.fossasia.openevent.app.contract.model.EventModel;
import org.fossasia.openevent.app.contract.model.UtilModel;
import org.fossasia.openevent.app.contract.view.EventListView;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.ui.presenter.EventsActivityPresenter;
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
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(JUnit4.class)
public class EventActivityPresenterTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    EventListView eventListView;

    @Mock
    EventModel eventModel;

    @Mock
    UtilModel utilModel;

    private EventsActivityPresenter eventsActivityPresenter;

    private List<Event> eventList = Arrays.asList(
        new Event(12),
        new Event(13),
        new Event(14)
    );

    @Before
    public void setUp() {
        eventsActivityPresenter = new EventsActivityPresenter(eventListView, eventModel, utilModel);
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldLoadEventsAutomatically() {
        Mockito.when(eventModel.getEvents(false))
            .thenReturn(Observable.just(eventList));

        eventsActivityPresenter.attach();

        Mockito.verify(eventModel).getEvents(false);
    }

    @Test
    public void shouldDetachViewOnStop() {
        Mockito.when(eventModel.getEvents(false))
            .thenReturn(Observable.just(eventList));

        eventsActivityPresenter.attach();

        assertNotNull(eventsActivityPresenter.getView());

        eventsActivityPresenter.detach();

        assertNull(eventsActivityPresenter.getView());
    }

    @Test
    public void shouldLoadEventsSuccessfully() {
        Mockito.when(eventModel.getEvents(false))
            .thenReturn(Observable.just(eventList));

        InOrder inOrder = Mockito.inOrder(eventModel, eventListView);

        eventsActivityPresenter.loadUserEvents(false);

        inOrder.verify(eventListView).showProgressBar(true);
        inOrder.verify(eventModel).getEvents(false);
        inOrder.verify(eventListView).showEvents(eventList);
        inOrder.verify(eventListView).showProgressBar(false);
    }

    @Test
    public void shouldShowEventError() {
        String error = "Test Error";
        Mockito.when(eventModel.getEvents(false))
            .thenReturn(Observable.error(new Throwable(error)));

        InOrder inOrder = Mockito.inOrder(eventModel, eventListView);

        eventsActivityPresenter.loadUserEvents(false);

        inOrder.verify(eventListView).showProgressBar(true);
        inOrder.verify(eventModel).getEvents(false);
        inOrder.verify(eventListView).showEventError(error);
        inOrder.verify(eventListView).showProgressBar(false);
    }

    @Test
    public void shouldNotAccessView() {
        eventsActivityPresenter.detach();

        eventsActivityPresenter.loadUserEvents(false);

        Mockito.verifyNoMoreInteractions(eventListView);
    }

}
