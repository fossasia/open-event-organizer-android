package org.fossasia.openevent.app.core.presenter;

import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.auth.AuthService;
import org.fossasia.openevent.app.data.Preferences;
import org.fossasia.openevent.app.data.auth.model.Login;
import org.fossasia.openevent.app.data.network.HostSelectionInterceptor;
import org.fossasia.openevent.app.core.auth.login.LoginPresenter;
import org.fossasia.openevent.app.core.auth.login.LoginView;
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
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@RunWith(JUnit4.class)
public class LoginPresenterTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock private Preferences sharedPreferenceModel;
    @Mock private LoginView loginView;
    @Mock private AuthService authModel;
    private final HostSelectionInterceptor interceptor = new HostSelectionInterceptor();

    private LoginPresenter loginPresenter;

    private static final String EMAIL = "test";
    private static final String PASSWORD = "test";
    private static final Login LOGIN = new Login(EMAIL, PASSWORD);

    private static final Set<String> SAVED_EMAILS = new HashSet<>(Arrays.asList("email1", "email2", "email3"));

    @Before
    public void setUp() {
        loginPresenter = new LoginPresenter(authModel, sharedPreferenceModel, interceptor);
        loginPresenter.attach(loginView);
        loginPresenter.getLogin().setEmail(EMAIL);
        loginPresenter.getLogin().setPassword(PASSWORD);
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

        assertTrue(loginPresenter.getDisposable().isDisposed());
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
        Mockito.when(authModel.login(LOGIN))
            .thenReturn(Completable.complete());

        InOrder inOrder = Mockito.inOrder(authModel, loginView);

        loginPresenter.start();
        loginPresenter.login();

        inOrder.verify(authModel).login(LOGIN);
        inOrder.verify(loginView).showProgress(true);
        inOrder.verify(loginView).onSuccess(any());
        inOrder.verify(loginView).showProgress(false);
    }

    @Test
    public void shouldShowLoginError() {
        String error = "Test Error";
        Mockito.when(authModel.login(LOGIN))
            .thenReturn(Completable.error(Logger.TEST_ERROR));

        InOrder inOrder = Mockito.inOrder(authModel, loginView);

        loginPresenter.start();
        loginPresenter.login();

        inOrder.verify(authModel).login(LOGIN);
        inOrder.verify(loginView).showProgress(true);
        inOrder.verify(loginView).showError(error);
        inOrder.verify(loginView).showProgress(false);
    }

    @Test
    public void shouldAttachEmailAutomatically() {
        Mockito.when(sharedPreferenceModel.getStringSet(Constants.SHARED_PREFS_SAVED_EMAIL, null)).thenReturn(SAVED_EMAILS);
        Mockito.when(authModel.isLoggedIn()).thenReturn(false);

        loginPresenter.start();

        Mockito.verify(loginView).attachEmails(SAVED_EMAILS);
    }

    @Test
    public void shouldNotAttachEmailAutomatically() {
        Mockito.when(sharedPreferenceModel.getStringSet(Constants.SHARED_PREFS_SAVED_EMAIL, null)).thenReturn(null);
        Mockito.when(authModel.isLoggedIn()).thenReturn(false);

        loginPresenter.start();

        Mockito.verify(loginView, Mockito.never()).attachEmails(SAVED_EMAILS);
    }
}
