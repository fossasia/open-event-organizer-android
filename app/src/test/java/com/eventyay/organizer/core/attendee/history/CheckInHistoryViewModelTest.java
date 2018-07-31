package com.eventyay.organizer.core.attendee.history;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;

import com.eventyay.organizer.core.presenter.TestUtil;
import com.eventyay.organizer.data.attendee.Attendee;
import com.eventyay.organizer.data.attendee.AttendeeRepository;
import com.eventyay.organizer.data.attendee.CheckInDetail;

import org.junit.After;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class CheckInHistoryViewModelTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Mock
    private AttendeeRepository attendeeRepository;
    @Mock
    Observer<String> error;
    @Mock
    Observer<Boolean> progress;

    private CheckInHistoryViewModel checkInHistoryViewModel;

    private static final long EVENT_ID = 5L;

    private static final String SCAN_IN = "Scan In";
    private static final String SCAN_OUT = "Scan Out";
    private static final String CHECKIN_TIMES = "2018-07-20T20:36:32.822+03:00," +
        "2018-07-22T20:36:32.822+03:00,2018-07-24T20:36:32.822+03:00";
    private static final String CHECKOUT_TIMES = "2018-07-21T20:36:32.822+03:00," +
        "2018-07-23T20:36:32.822+03:00";

    private static final Attendee ATTENDEE = Attendee.builder().id(2L).checkinTimes(CHECKIN_TIMES)
        .checkoutTimes(CHECKOUT_TIMES).build();

    @Before
    public void setUp() {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());

        checkInHistoryViewModel = new CheckInHistoryViewModel(attendeeRepository);
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldLoadCheckInDetailsSuccessfully() {
        List<CheckInDetail> ch = new ArrayList<>();
        List<CheckInDetail> checkInDetails = checkInHistoryViewModel.getCheckInDetails(ATTENDEE, ch);

        assertEquals(checkInDetails.get(0).getCheckTime(), "2018-07-20T20:36:32.822+03:00");
        assertEquals(checkInDetails.get(0).getScanAction(), SCAN_IN);
        assertEquals(checkInDetails.get(1).getCheckTime(), "2018-07-21T20:36:32.822+03:00");
        assertEquals(checkInDetails.get(1).getScanAction(), SCAN_OUT);
        assertEquals(checkInDetails.get(2).getCheckTime(), "2018-07-22T20:36:32.822+03:00");
        assertEquals(checkInDetails.get(2).getScanAction(), SCAN_IN);
        assertEquals(checkInDetails.get(3).getCheckTime(), "2018-07-23T20:36:32.822+03:00");
        assertEquals(checkInDetails.get(3).getScanAction(), SCAN_OUT);
        assertEquals(checkInDetails.get(4).getCheckTime(), "2018-07-24T20:36:32.822+03:00");
        assertEquals(checkInDetails.get(4).getScanAction(), SCAN_IN);
    }

    @Test
    public void shouldShowProgressWhenLoadingAttendees() {
        when(attendeeRepository.getAttendee(EVENT_ID, false))
            .thenReturn(Observable.just(ATTENDEE));

        InOrder inOrder = Mockito.inOrder(attendeeRepository, progress);

        checkInHistoryViewModel.getProgress().observeForever(progress);

        checkInHistoryViewModel.loadAttendee(EVENT_ID, false);

        inOrder.verify(attendeeRepository).getAttendee(EVENT_ID, false);
        inOrder.verify(progress).onChanged(true);
        inOrder.verify(progress).onChanged(false);
    }

    @Test
    public void shouldShowErrorWhenLoadingAttendees() {
        String errorString = "Test Error";
        when(attendeeRepository.getAttendee(EVENT_ID, false))
            .thenReturn(TestUtil.ERROR_OBSERVABLE);

        InOrder inOrder = Mockito.inOrder(attendeeRepository, progress, error);

        checkInHistoryViewModel.getProgress().observeForever(progress);
        checkInHistoryViewModel.getError().observeForever(error);

        checkInHistoryViewModel.loadAttendee(EVENT_ID, false);

        inOrder.verify(attendeeRepository).getAttendee(EVENT_ID, false);
        inOrder.verify(progress).onChanged(true);
        inOrder.verify(error).onChanged(errorString);
        inOrder.verify(progress).onChanged(false);
    }
}
