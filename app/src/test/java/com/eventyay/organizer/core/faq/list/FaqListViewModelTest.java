package com.eventyay.organizer.core.faq.list;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.eventyay.organizer.common.ContextManager;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.db.DatabaseChangeListener;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.faq.Faq;
import com.eventyay.organizer.data.faq.FaqRepository;

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

import java.util.Arrays;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class FaqListViewModelTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();
    @Mock
    private FaqListView faqListView;
    @Mock
    private FaqRepository faqRepository;
    @Mock
    private DatabaseChangeListener<Faq> faqChangeListener;

    private FaqListViewModel faqListViewModel;

    @Mock
    private Event event;

    @Mock
    Observer<Boolean> progress;
    @Mock
    Observer<String> success;
    @Mock
    Observer<String> error;

    private static final long ID = 10L;

    private static final List<Faq> FAQS = Arrays.asList(
        Faq.builder().id(2L).question("q").answer("a").build(),
        Faq.builder().id(3L).question("qu").answer("an").build(),
        Faq.builder().id(4L).question("que").answer("ans").build()
    );

    private static final Faq FAQ = Faq.builder().id(5L).question("r").answer("n").build();

    @Before
    public void setUp() {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());

        ContextManager.setSelectedEvent(event);
        faqListViewModel = new FaqListViewModel(faqRepository, faqChangeListener);
        ContextManager.setSelectedEvent(null);
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldLoadFaqsSuccessfully() {
        when(faqRepository.getFaqs(anyLong(), anyBoolean()))
            .thenReturn(Observable.fromIterable(FAQS));
        when(faqChangeListener.getNotifier()).thenReturn(PublishSubject.create());

        InOrder inOrder = Mockito.inOrder(progress, success);

        faqListViewModel.getProgress().observeForever(progress);
        faqListViewModel.getSuccess().observeForever(success);

        faqListViewModel.loadFaqs(false);

        inOrder.verify(progress).onChanged(true);
        inOrder.verify(success).onChanged(anyString());
        inOrder.verify(progress).onChanged(false);
    }

    @Test
    public void shouldShowErrorOnFailure() {
        when(faqRepository.getFaqs(anyLong(), anyBoolean()))
            .thenReturn(Observable.error(Logger.TEST_ERROR));
        when(faqChangeListener.getNotifier()).thenReturn(PublishSubject.create());

        InOrder inOrder = Mockito.inOrder(progress, error);

        faqListViewModel.getProgress().observeForever(progress);
        faqListViewModel.getError().observeForever(error);

        faqListViewModel.loadFaqs(false);

        inOrder.verify(progress).onChanged(true);
        inOrder.verify(error).onChanged(anyString());
        inOrder.verify(progress).onChanged(false);
    }

    @Test
    public void shouldDeleteFaqWithIdSuccessfully() {
        when(faqRepository.deleteFaq(FAQ.getId())).thenReturn(Completable.complete());

        faqListViewModel.getFaqSelected(FAQ);
        faqListViewModel.getIsSelected().get(FAQ).set(true);
        faqListViewModel.deleteFaq(FAQ);

        assertFalse(faqListViewModel.getFaqSelected(FAQ).get());
    }

    @Test
    public void shouldShowErrorInFaqDeletion() {
        when(faqRepository.deleteFaq(FAQ.getId())).thenReturn(Completable.error(Logger.TEST_ERROR));

        faqListViewModel.deleteFaq(FAQ);

        faqListView.showError(Logger.TEST_ERROR.getMessage());
    }
}
