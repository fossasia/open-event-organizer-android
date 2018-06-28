package org.fossasia.openevent.app.core.event.list;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;

import org.fossasia.openevent.app.core.presenter.TestUtil;
import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.data.event.EventRepository;
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
import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class EventsViewModelTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();
    @Mock private EventRepository eventRepository;

    @Mock
    Observer<List<Event>> events;
    @Mock
    Observer<String> error;
    @Mock
    Observer<Boolean> progress;
    @Mock
    Observer<Boolean> success;

    private EventsViewModel eventsViewModel;

    private static final String DATE = DateUtils.formatDateToIso(LocalDateTime.now());

    private static final List<Event> EVENT_LIST = Arrays.asList(
        Event.builder().id(12L).startsAt(DATE).endsAt(DATE).build(),
        Event.builder().id(13L).startsAt(DATE).endsAt(DATE).build(),
        Event.builder().id(14L).startsAt(DATE).endsAt(DATE).build()
    );

    @Before
    public void setUp() {
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());

        eventsViewModel = new EventsViewModel(eventRepository);
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
    }

    @Test
    public void shouldLoadEventsSuccessfully() {
        when(eventRepository.getEvents(false))
            .thenReturn(Observable.fromIterable(EVENT_LIST));

        InOrder inOrder = Mockito.inOrder(events, eventRepository, progress, success);

        eventsViewModel.getProgress().observeForever(progress);
        eventsViewModel.getSuccess().observeForever(success);
        eventsViewModel.getError().observeForever(error);

        events.onChanged(new ArrayList<Event>());

        eventsViewModel.loadUserEvents(false);

        inOrder.verify(events).onChanged(new ArrayList<>());
        inOrder.verify(eventRepository).getEvents(false);
        inOrder.verify(progress).onChanged(true);
        inOrder.verify(success).onChanged(true);
        inOrder.verify(progress).onChanged(false);
    }

    @Test
    public void shouldRefreshEventsSuccessfully() {
        when(eventRepository.getEvents(true))
            .thenReturn(Observable.fromIterable(EVENT_LIST));

        InOrder inOrder = Mockito.inOrder(events, eventRepository, progress, success, progress);

        eventsViewModel.getProgress().observeForever(progress);
        eventsViewModel.getSuccess().observeForever(success);
        eventsViewModel.getError().observeForever(error);

        events.onChanged(new ArrayList<Event>());

        eventsViewModel.loadUserEvents(true);

        inOrder.verify(eventRepository).getEvents(true);
        inOrder.verify(progress).onChanged(true);
        inOrder.verify(success).onChanged(true);
        inOrder.verify(progress).onChanged(false);
    }

    @Test
    public void shouldShowEventError() {
        String errorString = "Test Error";
        when(eventRepository.getEvents(false))
            .thenReturn(TestUtil.ERROR_OBSERVABLE);

        InOrder inOrder = Mockito.inOrder(eventRepository, progress, error);

        eventsViewModel.getProgress().observeForever(progress);
        eventsViewModel.getError().observeForever(error);

        events.onChanged(new ArrayList<Event>());

        eventsViewModel.loadUserEvents(false);

        inOrder.verify(eventRepository).getEvents(false);
        inOrder.verify(progress).onChanged(true);
        inOrder.verify(error).onChanged(errorString);
        inOrder.verify(progress).onChanged(false);
    }
}
