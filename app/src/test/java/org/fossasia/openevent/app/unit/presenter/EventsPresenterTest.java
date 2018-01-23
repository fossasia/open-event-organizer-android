package org.fossasia.openevent.app.unit.presenter;

import org.fossasia.openevent.app.common.data.models.Event;
import org.fossasia.openevent.app.common.data.models.User;
import org.fossasia.openevent.app.common.data.models.dto.ObservableString;
import org.fossasia.openevent.app.common.data.repository.contract.IEventRepository;
import org.fossasia.openevent.app.common.utils.core.DateUtils;
import org.fossasia.openevent.app.module.event.list.EventsPresenter;
import org.fossasia.openevent.app.module.event.list.contract.IEventsView;
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
import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.fossasia.openevent.app.unit.presenter.Util.ERROR_OBSERVABLE;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class EventsPresenterTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock private IEventsView eventListView;
    @Mock private IEventRepository eventRepository;

    private EventsPresenter eventsActivityPresenter;

    private static final String DATE_STRING = DateUtils.formatDateToIso(LocalDateTime.now());
    private static final ObservableString DATE = new ObservableString(DATE_STRING);

    private static final List<Event> EVENT_LIST = Arrays.asList(
        Event.builder().id(12L).startsAt(DATE).endsAt(DATE).build(),
        Event.builder().id(13L).startsAt(DATE).endsAt(DATE).build(),
        Event.builder().id(14L).startsAt(DATE).endsAt(DATE).build()
    );

    private static final User ORGANISER = new User();

    @Before
    public void setUp() {
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());

        eventsActivityPresenter = new EventsPresenter(eventRepository);
        eventsActivityPresenter.attach(eventListView);
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
    }

    @Test
    public void shouldLoadEventsAndOrganiserAutomatically() {
        when(eventRepository.getOrganiser(false))
            .thenReturn(Observable.just(ORGANISER));

        when(eventRepository.getEvents(false))
            .thenReturn(Observable.fromIterable(EVENT_LIST));

        eventsActivityPresenter.start();

        verify(eventRepository).getEvents(false);
    }

    @Test
    public void shouldDetachViewOnStop() {
        assertNotNull(eventsActivityPresenter.getView());

        eventsActivityPresenter.detach();

        assertTrue(eventsActivityPresenter.getDisposable().isDisposed());
    }

    @Test
    public void shouldLoadEventsSuccessfully() {
        when(eventRepository.getEvents(false))
            .thenReturn(Observable.fromIterable(EVENT_LIST));

        InOrder inOrder = Mockito.inOrder(eventRepository, eventListView);

        eventsActivityPresenter.loadUserEvents(false);

        inOrder.verify(eventRepository).getEvents(false);
        inOrder.verify(eventListView).showProgress(true);
        inOrder.verify(eventListView).showResults(EVENT_LIST);
        inOrder.verify(eventListView).showProgress(false);
    }

    @Test
    public void shouldRefreshEventsSuccessfully() {
        when(eventRepository.getEvents(true))
            .thenReturn(Observable.fromIterable(EVENT_LIST));

        InOrder inOrder = Mockito.inOrder(eventRepository, eventListView);

        eventsActivityPresenter.loadUserEvents(true);

        inOrder.verify(eventRepository).getEvents(true);
        inOrder.verify(eventListView).showProgress(true);
        inOrder.verify(eventListView).onRefreshComplete(true);
        inOrder.verify(eventListView).showResults(EVENT_LIST);
        inOrder.verify(eventListView).showProgress(false);
    }

    @Test
    public void shouldShowEmptyViewOnNoItemAfterSwipeRefresh() {
        ArrayList<Event> events = new ArrayList<>();
        when(eventRepository.getEvents(true))
            .thenReturn(Observable.fromIterable(events));

        InOrder inOrder = Mockito.inOrder(eventListView);

        eventsActivityPresenter.loadUserEvents(true);

        inOrder.verify(eventListView).showEmptyView(false);
        inOrder.verify(eventListView).showResults(events);
        inOrder.verify(eventListView).showEmptyView(true);
    }

    @Test
    public void shouldShowEmptyViewOnSwipeRefreshError() {
        when(eventRepository.getEvents(true))
            .thenReturn(ERROR_OBSERVABLE);

        InOrder inOrder = Mockito.inOrder(eventListView);

        eventsActivityPresenter.loadUserEvents(true);

        inOrder.verify(eventListView).showEmptyView(false);
        inOrder.verify(eventListView).showError(anyString());
        inOrder.verify(eventListView).showEmptyView(true);
    }

    @Test
    public void shouldNotShowEmptyViewOnSwipeRefreshSuccess() {
        when(eventRepository.getEvents(true))
            .thenReturn(Observable.fromIterable(EVENT_LIST));

        InOrder inOrder = Mockito.inOrder(eventListView);

        eventsActivityPresenter.loadUserEvents(true);

        inOrder.verify(eventListView).showEmptyView(false);
        inOrder.verify(eventListView).showResults(EVENT_LIST);
        inOrder.verify(eventListView).showEmptyView(false);
    }

    @Test
    public void shouldRefreshEventsOnError() {
        when(eventRepository.getEvents(true))
            .thenReturn(ERROR_OBSERVABLE);

        InOrder inOrder = Mockito.inOrder(eventRepository, eventListView);

        eventsActivityPresenter.loadUserEvents(true);

        inOrder.verify(eventRepository).getEvents(true);
        inOrder.verify(eventListView).showProgress(true);
        inOrder.verify(eventListView).showError(anyString());
        inOrder.verify(eventListView).onRefreshComplete(false);
        inOrder.verify(eventListView).showProgress(false);
    }

    @Test
    public void shouldShowEventError() {
        String error = "Test Error";
        when(eventRepository.getEvents(false))
            .thenReturn(ERROR_OBSERVABLE);

        InOrder inOrder = Mockito.inOrder(eventRepository, eventListView);

        eventsActivityPresenter.loadUserEvents(false);

        inOrder.verify(eventRepository).getEvents(false);
        inOrder.verify(eventListView).showProgress(true);
        inOrder.verify(eventListView).showError(error);
        inOrder.verify(eventListView).showProgress(false);
    }

    @Test
    public void shouldDisposeOnDetach() {
        eventsActivityPresenter.detach();
        assertTrue(eventsActivityPresenter.getDisposable().isDisposed());
    }

}
