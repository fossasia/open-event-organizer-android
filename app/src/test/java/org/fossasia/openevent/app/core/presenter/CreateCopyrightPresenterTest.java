package org.fossasia.openevent.app.core.presenter;

import org.fossasia.openevent.app.common.ContextManager;
import org.fossasia.openevent.app.data.copyright.Copyright;
import org.fossasia.openevent.app.data.copyright.CopyrightRepository;
import org.fossasia.openevent.app.core.event.copyright.CreateCopyrightPresenter;
import org.fossasia.openevent.app.core.event.copyright.CreateCopyrightView;
import org.fossasia.openevent.app.data.event.Event;
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

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class CreateCopyrightPresenterTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock private CreateCopyrightView createCopyrightView;
    @Mock private CopyrightRepository copyrightRepository;

    private CreateCopyrightPresenter createCopyrightPresenter;

    @Before
    public void setUp() {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());

        createCopyrightPresenter = new CreateCopyrightPresenter(copyrightRepository);
        createCopyrightPresenter.attach(createCopyrightView);
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    private Event getEvent() {
        return Event.builder().id(2L).build();
    }

    @Test
    public void shouldShowErrorOnInvalidYear() {
        Copyright copyright = createCopyrightPresenter.getCopyright();
        copyright.setYear("25");

        createCopyrightPresenter.createCopyright();

        verify(createCopyrightView).showError("Please Enter a Valid Year");
        verify(copyrightRepository, never()).createCopyright(any());
    }

    @Test
    public void shouldAcceptCorrectYear() {
        ContextManager.setSelectedEvent(getEvent());

        Copyright copyright = createCopyrightPresenter.getCopyright();
        copyright.setYear("2018");

        when(copyrightRepository.createCopyright(copyright)).thenReturn(Observable.just(copyright));

        createCopyrightPresenter.createCopyright();

        InOrder inOrder = Mockito.inOrder(createCopyrightView);

        inOrder.verify(createCopyrightView).showProgress(true);
        inOrder.verify(createCopyrightView).onSuccess(anyString());
        inOrder.verify(createCopyrightView).dismiss();
        inOrder.verify(createCopyrightView).showProgress(false);

        ContextManager.setSelectedEvent(null);
    }

    @Test
    public void shouldShowErrorOnFailure() {
        ContextManager.setSelectedEvent(getEvent());

        Copyright copyright = createCopyrightPresenter.getCopyright();

        when(copyrightRepository.createCopyright(copyright)).thenReturn(Observable.error(new Throwable("Error")));

        createCopyrightPresenter.createCopyright();

        InOrder inOrder = Mockito.inOrder(createCopyrightView);

        inOrder.verify(createCopyrightView).showProgress(true);
        inOrder.verify(createCopyrightView).showError("Error");
        inOrder.verify(createCopyrightView).showProgress(false);

        ContextManager.setSelectedEvent(null);
    }

    @Test
    public void shouldShowSuccessOnCreated() {
        ContextManager.setSelectedEvent(getEvent());

        Copyright copyright = createCopyrightPresenter.getCopyright();
        copyright.setYear(null);

        when(copyrightRepository.createCopyright(copyright)).thenReturn(Observable.just(copyright));

        createCopyrightPresenter.createCopyright();

        InOrder inOrder = Mockito.inOrder(createCopyrightView);

        inOrder.verify(createCopyrightView).showProgress(true);
        inOrder.verify(createCopyrightView).onSuccess(anyString());
        inOrder.verify(createCopyrightView).dismiss();
        inOrder.verify(createCopyrightView).showProgress(false);

        ContextManager.setSelectedEvent(null);
    }
}
