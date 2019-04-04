package com.eventyay.organizer.core.faq.create;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.eventyay.organizer.common.ContextManager;
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

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class CreateFaqViewModelTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Mock
    private FaqRepository faqRepository;
    @Mock
    private Event event;

    private CreateFaqViewModel createFaqViewModel;

    @Mock
    Observer<String> error;
    @Mock
    Observer<Boolean> progress;
    @Mock
    Observer<String> success;
    @Mock
    Observer<Void> dismiss;

    @Before
    public void setUp() {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
        ContextManager.setSelectedEvent(event);

        createFaqViewModel = new CreateFaqViewModel(faqRepository);
        ContextManager.setSelectedEvent(null);
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldShowSuccessOnCreated() {
        String successString = "Faq Created";
        Faq faq = createFaqViewModel.getFaq();
        when(faqRepository.createFaq(faq)).thenReturn(Observable.just(faq));
        ContextManager.setSelectedEvent(event);

        InOrder inOrder = Mockito.inOrder(progress, dismiss, success);

        createFaqViewModel.getProgress().observeForever(progress);
        createFaqViewModel.getDismiss().observeForever(dismiss);
        createFaqViewModel.getSuccess().observeForever(success);

        createFaqViewModel.createFaq();

        inOrder.verify(progress).onChanged(true);
        inOrder.verify(success).onChanged(successString);
        inOrder.verify(dismiss).onChanged(null);
        inOrder.verify(progress).onChanged(false);

        ContextManager.setSelectedEvent(null);
    }

    @Test
    public void shouldShowErrorOnFailure() {
        Faq faq = createFaqViewModel.getFaq();
        when(faqRepository.createFaq(faq)).thenReturn(Observable.error(new Throwable("Error")));
        ContextManager.setSelectedEvent(event);

        InOrder inOrder = Mockito.inOrder(progress, error);

        createFaqViewModel.getProgress().observeForever(progress);
        createFaqViewModel.getError().observeForever(error);

        createFaqViewModel.createFaq();

        inOrder.verify(progress).onChanged(true);
        inOrder.verify(error).onChanged("Error");
        inOrder.verify(progress).onChanged(false);

        ContextManager.setSelectedEvent(null);
    }
}
