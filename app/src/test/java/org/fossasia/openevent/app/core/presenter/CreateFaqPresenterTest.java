package org.fossasia.openevent.app.core.presenter;

import org.fossasia.openevent.app.common.ContextManager;
import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.data.faq.Faq;
import org.fossasia.openevent.app.data.faq.FaqRepository;
import org.fossasia.openevent.app.core.faq.create.CreateFaqPresenter;
import org.fossasia.openevent.app.core.faq.create.CreateFaqView;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class CreateFaqPresenterTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private CreateFaqView createFaqView;
    @Mock
    private FaqRepository faqRepository;
    @Mock
    private Event event;

    private CreateFaqPresenter createFaqPresenter;

    @Before
    public void setUp() {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());

        ContextManager.setSelectedEvent(event);
        createFaqPresenter = new CreateFaqPresenter(faqRepository);
        createFaqPresenter.attach(createFaqView);
        ContextManager.setSelectedEvent(null);
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldShowSuccessOnCreated() {
        Faq faq = createFaqPresenter.getFaq();
        when(faqRepository.createFaq(faq)).thenReturn(Observable.just(faq));
        ContextManager.setSelectedEvent(event);

        createFaqPresenter.createFaq();

        InOrder inOrder = Mockito.inOrder(createFaqView);

        inOrder.verify(createFaqView).showProgress(true);
        inOrder.verify(createFaqView).onSuccess(anyString());
        inOrder.verify(createFaqView).dismiss();
        inOrder.verify(createFaqView).showProgress(false);

        ContextManager.setSelectedEvent(null);
    }

    @Test
    public void shouldShowErrorOnFailure() {
        Faq faq = createFaqPresenter.getFaq();
        when(faqRepository.createFaq(faq)).thenReturn(Observable.error(new Throwable("Error")));
        ContextManager.setSelectedEvent(event);

        createFaqPresenter.createFaq();

        InOrder inOrder = Mockito.inOrder(createFaqView);

        inOrder.verify(createFaqView).showProgress(true);
        inOrder.verify(createFaqView).showError("Error");
        inOrder.verify(createFaqView).showProgress(false);

        ContextManager.setSelectedEvent(null);
    }
}
