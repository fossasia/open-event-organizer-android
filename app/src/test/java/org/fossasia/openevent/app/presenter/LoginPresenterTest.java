package org.fossasia.openevent.app.presenter;

import org.fossasia.openevent.app.data.contract.ILoginModel;
import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.data.models.LoginResponse;
import org.fossasia.openevent.app.login.LoginPresenter;
import org.fossasia.openevent.app.login.contract.ILoginView;
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(JUnit4.class)
public class LoginPresenterTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    IUtilModel utilModel;

    @Mock
    ILoginView loginView;

    @Mock
    ILoginModel loginModel;

    private LoginPresenter loginPresenter;

    private String email = "test";
    private String password = "test";

    @Before
    public void setUp() {
        loginPresenter = new LoginPresenter(loginModel);
    }

    @Test
    public void shouldShowSuccessOnStart() {
        Mockito.when(loginModel.isLoggedIn()).thenReturn(true);

        loginPresenter.attach(loginView);

        Mockito.verify(loginView).onLoginSuccess();
    }

    @Test
    public void shouldNotShowSuccessOnStart() {
        Mockito.when(loginModel.isLoggedIn()).thenReturn(false);

        loginPresenter.attach(loginView);

        Mockito.verify(loginView, Mockito.never()).onLoginSuccess();
    }

    @Test
    public void shouldDetachViewOnStop() {
        loginPresenter.attach(loginView);

        assertNotNull(loginPresenter.getView());

        loginPresenter.detach();

        assertNull(loginPresenter.getView());
    }

    @Test
    public void shouldLoginAutomatically() {
        Mockito.when(loginModel.isLoggedIn()).thenReturn(true);

        loginPresenter.attach(loginView);

        Mockito.verify(loginView).onLoginSuccess();
    }

    @Test
    public void shouldNotLoginAutomatically() {
        Mockito.when(loginModel.isLoggedIn()).thenReturn(false);

        loginPresenter.attach(loginView);

        Mockito.verifyZeroInteractions(loginView);
    }

    @Test
    public void shouldLoginSuccessfully() {
        String authToken = "testToken";
        Mockito.when(loginModel.login(email, password))
            .thenReturn(Observable.just(new LoginResponse(authToken)));

        InOrder inOrder = Mockito.inOrder(loginModel, loginView);

        loginPresenter.attach(loginView);
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

        loginPresenter.attach(loginView);
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
