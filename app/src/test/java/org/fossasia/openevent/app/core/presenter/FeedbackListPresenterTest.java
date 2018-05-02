package org.fossasia.openevent.app.core.presenter;

import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.core.feedback.list.FeedbackListPresenter;
import org.fossasia.openevent.app.core.feedback.list.FeedbackListView;
import org.fossasia.openevent.app.data.feedback.Feedback;
import org.fossasia.openevent.app.data.feedback.FeedbackRepository;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
@SuppressWarnings("PMD.TooManyMethods")
public class FeedbackListPresenterTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    public FeedbackListView feedbackListView;
    @Mock
    public FeedbackRepository feedbackRepository;

    private FeedbackListPresenter feedbackListPresenter;

    private static final long ID = 10L;

    private static final List<Feedback> FEEDBACKS = Arrays.asList(
        Feedback.builder().id(2L).comment("Amazing!").build(),
        Feedback.builder().id(3L).comment("Awesome!").build(),
        Feedback.builder().id(4L).comment("Poor!").build()
    );

    @Before
    public void setUp() {
        feedbackListPresenter = new FeedbackListPresenter(feedbackRepository);
        feedbackListPresenter.attach(ID, feedbackListView);

        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldLoadFeedbackListAutomatically() {
        when(feedbackRepository.getFeedbacks(anyLong(), anyBoolean())).thenReturn(Observable.fromIterable(FEEDBACKS));

        feedbackListPresenter.start();

        verify(feedbackRepository).getFeedbacks(ID, false);
    }

    @Test
    public void shouldShowFeedbackListAutomatically() {
        when(feedbackRepository.getFeedbacks(ID, false)).thenReturn(Observable.fromIterable(FEEDBACKS));

       feedbackListPresenter.start();

        verify(feedbackListView).showResults(FEEDBACKS);
    }

    @Test
    public void shouldShowEmptyViewOnNoFeedbacks() {
        when(feedbackRepository.getFeedbacks(anyLong(), anyBoolean())).thenReturn(Observable.fromIterable(new ArrayList<>()));

        feedbackListPresenter.loadFeedbacks(true);

        verify(feedbackListView).showEmptyView(true);
    }

    @Test
    public void shouldShowFeedbackOnSwipeRefreshSuccess() {
        when(feedbackRepository.getFeedbacks(ID, true)).thenReturn(Observable.fromIterable(FEEDBACKS));

        feedbackListPresenter.loadFeedbacks(true);

        verify(feedbackListView).showResults(any());
    }

    @Test
    public void shouldShowErrorMessageOnSwipeRefreshError() {
        when(feedbackRepository.getFeedbacks(ID, true)).thenReturn(Observable.error(Logger.TEST_ERROR));

        feedbackListPresenter.loadFeedbacks(true);

        verify(feedbackListView).showError(Logger.TEST_ERROR.getMessage());
    }

    @Test
    public void testProgressbarOnSwipeRefreshSuccess() {
        when(feedbackRepository.getFeedbacks(ID, true)).thenReturn(Observable.fromIterable(FEEDBACKS));

        feedbackListPresenter.loadFeedbacks(true);

        InOrder inOrder = Mockito.inOrder(feedbackListView);

        inOrder.verify(feedbackListView).showProgress(true);
        inOrder.verify(feedbackListView).onRefreshComplete(true);
        inOrder.verify(feedbackListView).showProgress(false);
    }

    @Test
    public void testProgressbarOnSwipeRefreshError() {
        when(feedbackRepository.getFeedbacks(ID, true)).thenReturn(Observable.error(Logger.TEST_ERROR));

        feedbackListPresenter.loadFeedbacks(true);

        InOrder inOrder = Mockito.inOrder(feedbackListView);

        inOrder.verify(feedbackListView).showProgress(true);
        inOrder.verify(feedbackListView).onRefreshComplete(false);
        inOrder.verify(feedbackListView).showProgress(false);
    }

    @Test
    public void testProgressbarOnSwipeRefreshNoItem() {
        List<Feedback> emptyList = new ArrayList<>();
        when(feedbackRepository.getFeedbacks(ID, true)).thenReturn(Observable.fromIterable(emptyList));

        feedbackListPresenter.loadFeedbacks(true);

        InOrder inOrder = Mockito.inOrder(feedbackListView);

        inOrder.verify(feedbackListView).showProgress(true);
        inOrder.verify(feedbackListView).onRefreshComplete(true);
        inOrder.verify(feedbackListView).showProgress(false);
    }
}
