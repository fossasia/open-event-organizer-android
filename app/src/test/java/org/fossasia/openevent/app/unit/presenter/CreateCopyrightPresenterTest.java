package org.fossasia.openevent.app.unit.presenter;

import org.fossasia.openevent.app.data.models.Copyright;
import org.fossasia.openevent.app.data.repository.ICopyrightRepository;
import org.fossasia.openevent.app.core.event.copyright.CreateCopyrightPresenter;
import org.fossasia.openevent.app.core.event.copyright.ICreateCopyrightView;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class CreateCopyrightPresenterTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock private ICreateCopyrightView createCopyrightView;
    @Mock private ICopyrightRepository copyrightRepository;

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

    @Test
    public void shouldShowErrorOnFailure() {
        Copyright copyright = createCopyrightPresenter.getCopyright();

        when(copyrightRepository.createCopyright(copyright)).thenReturn(Observable.error(new Throwable("Error")));

        createCopyrightPresenter.createCopyright();

        InOrder inOrder = Mockito.inOrder(createCopyrightView);

        inOrder.verify(createCopyrightView).showProgress(true);
        inOrder.verify(createCopyrightView).showError("Error");
        inOrder.verify(createCopyrightView).showProgress(false);
    }

    @Test
    public void shouldShowSuccessOnCreated() {
        Copyright copyright = createCopyrightPresenter.getCopyright();

        when(copyrightRepository.createCopyright(copyright)).thenReturn(Observable.just(copyright));

        createCopyrightPresenter.createCopyright();

        InOrder inOrder = Mockito.inOrder(createCopyrightView);

        inOrder.verify(createCopyrightView).showProgress(true);
        inOrder.verify(createCopyrightView).onSuccess(anyString());
        inOrder.verify(createCopyrightView).dismiss();
        inOrder.verify(createCopyrightView).showProgress(false);
    }
}
