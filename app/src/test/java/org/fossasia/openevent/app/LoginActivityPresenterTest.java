package org.fossasia.openevent.app;

import org.fossasia.openevent.app.contract.model.LoginModel;
import org.fossasia.openevent.app.contract.view.LoginView;
import org.fossasia.openevent.app.data.models.LoginResponse;
import org.fossasia.openevent.app.ui.presenter.LoginActivityPresenter;
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(JUnit4.class)
public class LoginActivityPresenterTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    LoginView loginView;

    @Mock
    LoginModel loginModel;

    private LoginActivityPresenter loginPresenter;

    private String email = "test";
    private String password = "test";

    @Before
    public void setUp() {
        loginPresenter = new LoginActivityPresenter(loginView, loginModel);
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldShowSuccessOnStart() {
        Mockito.when(loginModel.isLoggedIn()).thenReturn(true);

        loginPresenter.attach();

        Mockito.verify(loginView).onLoginSuccess();
    }

    @Test
    public void shouldNotShowSuccessOnStart() {
        Mockito.when(loginModel.isLoggedIn()).thenReturn(false);

        loginPresenter.attach();

        Mockito.verify(loginView, Mockito.never()).onLoginSuccess();
    }

    @Test
    public void shouldDetachViewOnStop() {
        loginPresenter.attach();

        assertNotNull(loginPresenter.getView());

        loginPresenter.detach();

        assertNull(loginPresenter.getView());
    }

    @Test
    public void shouldLoginSuccessfully() {
        String authToken = "testToken";
        Mockito.when(loginModel.login(email, password))
            .thenReturn(Observable.just(new LoginResponse(authToken)));

        InOrder inOrder = Mockito.inOrder(loginModel, loginView);

        loginPresenter.login(email, password);

        inOrder.verify(loginView).showProgressBar(true);
        inOrder.verify(loginModel).login(email, password);
        inOrder.verify(loginView).onLoginSuccess();
        inOrder.verify(loginView).showProgressBar(false);
    }

    @Test
    public void shouldShowLoginError() {
        String error = "Test Error";
        Mockito.when(loginModel.login(email, password))
            .thenReturn(Observable.error(new Throwable(error)));

        InOrder inOrder = Mockito.inOrder(loginModel, loginView);

        loginPresenter.login(email, password);

        inOrder.verify(loginView).showProgressBar(true);
        inOrder.verify(loginModel).login(email, password);
        inOrder.verify(loginView).onLoginError(error);
        inOrder.verify(loginView).showProgressBar(false);
    }

    @Test
    public void shouldNotAccessView() {
        loginPresenter.detach();

        loginPresenter.login(email, password);

        Mockito.verifyNoMoreInteractions(loginView);
    }

}
