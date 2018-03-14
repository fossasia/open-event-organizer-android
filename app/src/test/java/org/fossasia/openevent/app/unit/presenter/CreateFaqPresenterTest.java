package org.fossasia.openevent.app.unit.presenter;

import org.fossasia.openevent.app.common.app.ContextManager;
import org.fossasia.openevent.app.common.data.models.Event;
import org.fossasia.openevent.app.common.data.models.Faq;
import org.fossasia.openevent.app.common.data.repository.contract.IFaqRepository;
import org.fossasia.openevent.app.module.faq.create.CreateFaqPresenter;
import org.fossasia.openevent.app.module.faq.create.contract.ICreateFaqView;
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
    private ICreateFaqView createFaqView;
    @Mock
    private IFaqRepository faqRepository;
    @Mock
    private Event event;

    private CreateFaqPresenter createFaqPresenter;
    private static final Faq FAQ = new Faq();

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
        when(faqRepository.createFaq(FAQ)).thenReturn(Observable.just(FAQ));

        createFaqPresenter.createFaq();

        InOrder inOrder = Mockito.inOrder(createFaqView);

        inOrder.verify(createFaqView).showProgress(true);
        inOrder.verify(createFaqView).onSuccess(anyString());
        inOrder.verify(createFaqView).dismiss();
        inOrder.verify(createFaqView).showProgress(false);
    }

    @Test
    public void shouldShowErrorOnFailure() {
        when(faqRepository.createFaq(FAQ)).thenReturn(Observable.error(new Throwable("Error")));

        createFaqPresenter.createFaq();

        InOrder inOrder = Mockito.inOrder(createFaqView);

        inOrder.verify(createFaqView).showProgress(true);
        inOrder.verify(createFaqView).showError("Error");
        inOrder.verify(createFaqView).showProgress(false);
    }
}
