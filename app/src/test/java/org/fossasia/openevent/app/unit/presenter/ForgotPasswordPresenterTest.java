package org.fossasia.openevent.app.unit.presenter;

import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.IAuthModel;
import org.fossasia.openevent.app.data.ISharedPreferenceModel;
import org.fossasia.openevent.app.data.models.RequestToken;
import org.fossasia.openevent.app.data.network.HostSelectionInterceptor;
import org.fossasia.openevent.app.core.auth.forgot.request.ForgotPasswordPresenter;
import org.fossasia.openevent.app.core.auth.forgot.request.IForgotPasswordView;

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
public class ForgotPasswordPresenterTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private ISharedPreferenceModel sharedPreferenceModel;
    @Mock private IForgotPasswordView forgotPasswordView;
    @Mock private IAuthModel authModel;
    private final HostSelectionInterceptor interceptor = new HostSelectionInterceptor();

    private ForgotPasswordPresenter forgotPasswordPresenter;

    private static final String EMAIL = "test";
    private static final RequestToken REQUEST_TOKEN = new RequestToken(EMAIL);

    private static final Set<String> SAVED_EMAILS = new HashSet<>(Arrays.asList("email1", "email2", "email3"));

    @Before
    public void setUp() {
        forgotPasswordPresenter = new ForgotPasswordPresenter(authModel, sharedPreferenceModel, interceptor);
        forgotPasswordPresenter.attach(forgotPasswordView);
        forgotPasswordPresenter.getEmailId().setEmail(EMAIL);
    }

    @Test
    public void shouldDetachViewOnStop() {
        assertNotNull(forgotPasswordPresenter.getView());

        forgotPasswordPresenter.detach();

        assertTrue(forgotPasswordPresenter.getDisposable().isDisposed());
    }

    @Test
    public void shouldRequestTokenSuccessfully() {
        Mockito.when(authModel.requestToken(REQUEST_TOKEN))
            .thenReturn(Completable.complete());

        InOrder inOrder = Mockito.inOrder(authModel, forgotPasswordView);

        forgotPasswordPresenter.start();
        forgotPasswordPresenter.requestToken();

        inOrder.verify(authModel).requestToken(REQUEST_TOKEN);
        inOrder.verify(forgotPasswordView).showProgress(true);
        inOrder.verify(forgotPasswordView).onSuccess(any());
        inOrder.verify(forgotPasswordView).showProgress(false);
    }

    @Test
    public void shouldShowRequestTokenError() {
        String error = "Test Error";
        Mockito.when(authModel.requestToken(REQUEST_TOKEN))
            .thenReturn(Completable.error(Logger.TEST_ERROR));

        InOrder inOrder = Mockito.inOrder(authModel, forgotPasswordView);

        forgotPasswordPresenter.start();
        forgotPasswordPresenter.requestToken();

        inOrder.verify(authModel).requestToken(REQUEST_TOKEN);
        inOrder.verify(forgotPasswordView).showProgress(true);
        inOrder.verify(forgotPasswordView).showError(error);
        inOrder.verify(forgotPasswordView).showProgress(false);
    }

    @Test
    public void shouldAttachEmailAutomatically() {
        Mockito.when(sharedPreferenceModel.getStringSet(Constants.SHARED_PREFS_SAVED_EMAIL, null)).thenReturn(SAVED_EMAILS);

        forgotPasswordPresenter.start();

        Mockito.verify(forgotPasswordView).attachEmails(SAVED_EMAILS);
    }

    @Test
    public void shouldNotAttachEmailAutomatically() {
        Mockito.when(sharedPreferenceModel.getStringSet(Constants.SHARED_PREFS_SAVED_EMAIL, null)).thenReturn(null);

        forgotPasswordPresenter.start();

        Mockito.verify(forgotPasswordView, Mockito.never()).attachEmails(SAVED_EMAILS);
    }
}
