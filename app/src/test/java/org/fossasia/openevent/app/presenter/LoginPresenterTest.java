package org.fossasia.openevent.app.presenter;

import org.fossasia.openevent.app.data.contract.ILoginModel;
import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.login.LoginPresenter;
import org.fossasia.openevent.app.login.contract.ILoginView;
import org.fossasia.openevent.app.utils.Constants;
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

    private Set<String> savedEmails = new HashSet<>(Arrays.asList("email1", "email2", "email3"));

    @Before
    public void setUp() {
        loginPresenter = new LoginPresenter(loginModel, utilModel);
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

        Mockito.verify(loginView, Mockito.never()).onLoginSuccess();
    }

    @Test
    public void shouldLoginSuccessfully() {
        Mockito.when(loginModel.login(email, password))
            .thenReturn(Completable.complete());
        Mockito.when(utilModel.deleteDatabase())
            .thenReturn(Completable.complete());

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
            .thenReturn(Completable.error(new Throwable(error)));

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

    @Test
    public void shouldAttachEmailAutomatically() {
        Mockito.when(utilModel.getStringSet(Constants.SHARED_PREFS_SAVED_EMAIL, null)).thenReturn(savedEmails);
        Mockito.when(loginModel.isLoggedIn()).thenReturn(false);

        loginPresenter.attach(loginView);

        Mockito.verify(loginView).attachEmails(savedEmails);
    }

    @Test
    public void shouldNotAttachEmailAutomatically() {
        Mockito.when(utilModel.getStringSet(Constants.SHARED_PREFS_SAVED_EMAIL, null)).thenReturn(null);
        Mockito.when(loginModel.isLoggedIn()).thenReturn(false);

        loginPresenter.attach(loginView);

        Mockito.verify(loginView, Mockito.never()).attachEmails(savedEmails);
    }
}
