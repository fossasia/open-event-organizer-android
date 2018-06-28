package org.fossasia.openevent.app.core.main;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;

import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.Bus;
import org.fossasia.openevent.app.data.Preferences;
import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.data.event.EventRepository;
import org.fossasia.openevent.app.utils.CurrencyUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.reactivex.Observable;

import static org.fossasia.openevent.app.common.rx.Logger.TEST_MESSAGE;
import static org.fossasia.openevent.app.core.main.MainActivity.EVENT_KEY;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class EventViewModelTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Mock
    private Preferences sharedPreferenceModel;
    @Mock
    private Bus bus;
    @Mock
    private CurrencyUtils currencyUtils;
    @Mock
    private EventRepository eventRepository;

    @Mock
    Observer<String> error;
    @Mock
    Observer<Event> event;
    @Mock
    Observer<Long> eventId;
    @Mock
    Observer<Void> singleEventAction;

    private EventViewModel eventViewModel;
    private static final Long ID = 1L;
    private static final Event EVENT = Event.builder().id(ID).build();

    @Before
    public void setUp() {
        eventViewModel = new EventViewModel(sharedPreferenceModel, bus, currencyUtils, eventRepository);
    }

    @Test
    public void shouldLoadSelectedEvent() {
        when(bus.getSelectedEvent())
            .thenReturn(Observable.just(EVENT));
        when(sharedPreferenceModel.getLong(EVENT_KEY, -1)).thenReturn(ID);

        InOrder inOrder = Mockito.inOrder(bus, sharedPreferenceModel, eventId, event, singleEventAction);

        eventViewModel.getEventId().observeForever(eventId);
        eventViewModel.getSelectedEvent().observeForever(event);
        eventViewModel.getShowDashboard().observeForever(singleEventAction);

        eventViewModel.onStart();

        inOrder.verify(bus).getSelectedEvent();
        inOrder.verify(sharedPreferenceModel).setLong(EVENT_KEY, ID);
        inOrder.verify(eventId).onChanged(ID);
        inOrder.verify(event).onChanged(EVENT);
        inOrder.verify(singleEventAction).onChanged(null);
    }

    @Test
    public void shouldShowErrorInLoadingSelectedEvent() {
        when(bus.getSelectedEvent())
            .thenReturn(Observable.error(Logger.TEST_ERROR));
        when(sharedPreferenceModel.getLong(EVENT_KEY, -1)).thenReturn(-1L);

        InOrder inOrder = Mockito.inOrder(bus, sharedPreferenceModel, error);

        eventViewModel.getError().observeForever(error);

        eventViewModel.onStart();

        inOrder.verify(bus).getSelectedEvent();
        inOrder.verify(error).onChanged(TEST_MESSAGE);
    }

    @Test
    public void shouldShowEventList() {
        when(bus.getSelectedEvent())
            .thenReturn(Observable.empty());
        when(sharedPreferenceModel.getLong(EVENT_KEY, -1)).thenReturn(-1L);

        InOrder inOrder = Mockito.inOrder(bus, sharedPreferenceModel, singleEventAction);

        eventViewModel.getShowEventList().observeForever(singleEventAction);

        eventViewModel.onStart();

        inOrder.verify(bus).getSelectedEvent();
        inOrder.verify(singleEventAction).onChanged(null);
    }
}
