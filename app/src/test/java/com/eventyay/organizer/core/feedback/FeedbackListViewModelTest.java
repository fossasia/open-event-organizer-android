package com.eventyay.organizer.core.feedback;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.eventyay.organizer.common.ContextManager;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.core.feedback.list.FeedbackListViewModel;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.feedback.Feedback;
import com.eventyay.organizer.data.feedback.FeedbackRepository;

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

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class FeedbackListViewModelTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Mock
    public FeedbackRepository feedbackRepository;

    private FeedbackListViewModel feedbackListViewModel;

    @Mock
    private Event event;

    @Mock
    Observer<Boolean> progress;
    @Mock
    Observer<String> success;
    @Mock
    Observer<String> error;

    private static final List<Feedback> FEEDBACKS = Arrays.asList(
        Feedback.builder().id(2L).comment("Amazing!").build(),
        Feedback.builder().id(3L).comment("Awesome!").build(),
        Feedback.builder().id(4L).comment("Poor!").build()
    );

    @Before
    public void setUp() {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());

        ContextManager.setSelectedEvent(event);
        feedbackListViewModel = new FeedbackListViewModel(feedbackRepository);
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldLoadFeedbacksSuccessfully() {
        when(feedbackRepository.getFeedbacks(anyLong(), anyBoolean()))
            .thenReturn(Observable.fromIterable(FEEDBACKS));

        InOrder inOrder = Mockito.inOrder(progress, success);

        feedbackListViewModel.getProgress().observeForever(progress);
        feedbackListViewModel.getSuccess().observeForever(success);

        feedbackListViewModel.loadFeedbacks(false);

        inOrder.verify(progress).onChanged(true);
        inOrder.verify(success).onChanged(anyString());
        inOrder.verify(progress).onChanged(false);
    }

    @Test
    public void shouldShowErrorOnFailure() {
        when(feedbackRepository.getFeedbacks(anyLong(), anyBoolean()))
            .thenReturn(Observable.error(Logger.TEST_ERROR));

        InOrder inOrder = Mockito.inOrder(progress, error);

        feedbackListViewModel.getProgress().observeForever(progress);
        feedbackListViewModel.getError().observeForever(error);

        feedbackListViewModel.loadFeedbacks(false);

        inOrder.verify(progress).onChanged(true);
        inOrder.verify(error).onChanged(anyString());
        inOrder.verify(progress).onChanged(false);
    }
}
