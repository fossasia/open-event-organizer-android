package com.eventyay.organizer.core.event.sales;

import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import com.eventyay.organizer.core.event.dashboard.analyser.TicketAnalyser;
import com.eventyay.organizer.core.event.list.sales.SalesSummaryViewModel;
import com.eventyay.organizer.data.attendee.Attendee;
import com.eventyay.organizer.data.attendee.AttendeeRepository;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.event.EventRepository;
import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import java.util.Arrays;
import java.util.List;
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

@RunWith(JUnit4.class)
public class SalesSummaryViewModelTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Mock private EventRepository eventRepository;
    @Mock private AttendeeRepository attendeeRepository;
    @Mock private TicketAnalyser ticketAnalyser;

    @Mock Observer<String> error;
    @Mock Observer<Boolean> progress;
    @Mock Observer<Event> event;

    private static final List<Attendee> ATTENDEES =
            Arrays.asList(
                    Attendee.builder().isCheckedIn(false).build(),
                    Attendee.builder().isCheckedIn(true).build(),
                    Attendee.builder().isCheckedIn(false).build(),
                    Attendee.builder().isCheckedIn(false).build(),
                    Attendee.builder().isCheckedIn(true).build(),
                    Attendee.builder().isCheckedIn(true).build(),
                    Attendee.builder().isCheckedIn(false).build());

    private SalesSummaryViewModel salesSummaryViewModel;
    private static final Event EVENT = new Event();
    private static final long ID = 10L;

    @Before
    public void setUp() {
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(
                schedulerCallable -> Schedulers.trampoline());

        salesSummaryViewModel =
                new SalesSummaryViewModel(eventRepository, attendeeRepository, ticketAnalyser);
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldLoadDetailsSuccessfully() {
        when(eventRepository.getEvent(ID, false)).thenReturn(Observable.just(EVENT));

        InOrder inOrder = Mockito.inOrder(event, eventRepository, progress);

        salesSummaryViewModel.getProgress().observeForever(progress);
        salesSummaryViewModel.getEventLiveData().observeForever(event);

        salesSummaryViewModel.loadDetails(ID, false);

        inOrder.verify(eventRepository).getEvent(ID, false);
        inOrder.verify(progress).onChanged(true);
        inOrder.verify(progress).onChanged(false);
        inOrder.verify(event).onChanged(EVENT);
    }

    @Test
    public void shouldLoadAttendeesSuccessfully() {
        when(eventRepository.getEvent(ID, false)).thenReturn(Observable.just(EVENT));

        when(attendeeRepository.getAttendees(ID, false))
                .thenReturn(Observable.fromIterable(ATTENDEES));

        InOrder inOrder = Mockito.inOrder(event, attendeeRepository, progress);

        salesSummaryViewModel.getProgress().observeForever(progress);

        salesSummaryViewModel.loadDetails(ID, false);

        inOrder.verify(attendeeRepository).getAttendees(ID, false);
        inOrder.verify(progress).onChanged(true);
        inOrder.verify(progress).onChanged(false);
    }

    @Test
    public void shouldShowErrorOnFailure() {
        when(eventRepository.getEvent(ID, false))
                .thenReturn(Observable.error(new Throwable("Error")));

        InOrder inOrder = Mockito.inOrder(eventRepository, progress, error);

        salesSummaryViewModel.getProgress().observeForever(progress);
        salesSummaryViewModel.getError().observeForever(error);

        salesSummaryViewModel.loadDetails(ID, false);

        inOrder.verify(eventRepository).getEvent(ID, false);
        inOrder.verify(progress).onChanged(true);
        inOrder.verify(error).onChanged("Error");
        inOrder.verify(progress).onChanged(false);
    }
}
