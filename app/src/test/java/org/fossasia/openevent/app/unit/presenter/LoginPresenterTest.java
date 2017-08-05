package org.fossasia.openevent.app.unit.presenter;

import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.common.app.rx.Logger;
import org.fossasia.openevent.app.common.data.contract.IAuthModel;
import org.fossasia.openevent.app.common.data.contract.ISharedPreferenceModel;
import org.fossasia.openevent.app.common.data.models.dto.Login;
import org.fossasia.openevent.app.common.data.network.HostSelectionInterceptor;
import org.fossasia.openevent.app.module.auth.login.LoginPresenter;
import org.fossasia.openevent.app.module.auth.login.contract.ILoginView;
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
import java.util.HashSet;
import java.util.Set;

import io.reactivex.Completable;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;

@RunWith(JUnit4.class)
public class LoginPresenterTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private ISharedPreferenceModel sharedPreferenceModel;

    private HostSelectionInterceptor interceptor = new HostSelectionInterceptor();

    @Mock
    private ILoginView loginView;

    @Mock
    private IAuthModel authModel;

    private LoginPresenter loginPresenter;

    private String email = "test";
    private String password = "test";
    private Login login = new Login(email, password);

    private Set<String> savedEmails = new HashSet<>(Arrays.asList("email1", "email2", "email3"));

    @Before
    public void setUp() {
        loginPresenter = new LoginPresenter(authModel, sharedPreferenceModel, interceptor);
        loginPresenter.attach(loginView);
        loginPresenter.getLogin().setEmail(email);
        loginPresenter.getLogin().setPassword(password);
    }

    @Test
    public void shouldShowSuccessOnStart() {
        Mockito.when(authModel.isLoggedIn()).thenReturn(true);

        loginPresenter.start();

        Mockito.verify(loginView).onSuccess(any());
    }

    @Test
    public void shouldNotShowSuccessOnStart() {
        Mockito.when(authModel.isLoggedIn()).thenReturn(false);

        loginPresenter.start();

        Mockito.verify(loginView, Mockito.never()).onSuccess(any());
    }

    @Test
    public void shouldDetachViewOnStop() {
        assertNotNull(loginPresenter.getView());

        loginPresenter.detach();

        assertNull(loginPresenter.getView());
    }

    @Test
    public void shouldLoginAutomatically() {
        Mockito.when(authModel.isLoggedIn()).thenReturn(true);

        loginPresenter.start();

        Mockito.verify(loginView).onSuccess(any());
    }

    @Test
    public void shouldNotLoginAutomatically() {
        Mockito.when(authModel.isLoggedIn()).thenReturn(false);

        loginPresenter.start();

        Mockito.verify(loginView, Mockito.never()).onSuccess(any());
    }

    @Test
    public void shouldLoginSuccessfully() {
        Mockito.when(authModel.login(login))
            .thenReturn(Completable.complete());

        InOrder inOrder = Mockito.inOrder(authModel, loginView);

        loginPresenter.start();
        loginPresenter.login();

        inOrder.verify(authModel).login(login);
        inOrder.verify(loginView).showProgress(true);
        inOrder.verify(loginView).onSuccess(any());
        inOrder.verify(loginView).showProgress(false);
    }

    @Test
    public void shouldShowLoginError() {
        String error = "Test Error";
        Mockito.when(authModel.login(login))
            .thenReturn(Completable.error(Logger.TEST_ERROR));

        InOrder inOrder = Mockito.inOrder(authModel, loginView);

        loginPresenter.start();
        loginPresenter.login();

        inOrder.verify(authModel).login(login);
        inOrder.verify(loginView).showProgress(true);
        inOrder.verify(loginView).showError(error);
        inOrder.verify(loginView).showProgress(false);
    }

    @Test
    public void shouldNotAccessView() {
        loginPresenter.attach(loginView);
        loginPresenter.detach();

        loginPresenter.start();

        Mockito.verifyNoMoreInteractions(loginView);
    }

    @Test
    public void shouldAttachEmailAutomatically() {
        Mockito.when(sharedPreferenceModel.getStringSet(Constants.SHARED_PREFS_SAVED_EMAIL, null)).thenReturn(savedEmails);
        Mockito.when(authModel.isLoggedIn()).thenReturn(false);

        loginPresenter.start();

        Mockito.verify(loginView).attachEmails(savedEmails);
    }

    @Test
    public void shouldNotAttachEmailAutomatically() {
        Mockito.when(sharedPreferenceModel.getStringSet(Constants.SHARED_PREFS_SAVED_EMAIL, null)).thenReturn(null);
        Mockito.when(authModel.isLoggedIn()).thenReturn(false);

        loginPresenter.start();

        Mockito.verify(loginView, Mockito.never()).attachEmails(savedEmails);
    }
}
