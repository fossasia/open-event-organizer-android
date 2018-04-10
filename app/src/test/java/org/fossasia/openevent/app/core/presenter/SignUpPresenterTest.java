package org.fossasia.openevent.app.core.presenter;

import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.auth.AuthService;
import org.fossasia.openevent.app.data.user.User;
import org.fossasia.openevent.app.data.network.HostSelectionInterceptor;
import org.fossasia.openevent.app.core.auth.signup.SignUpPresenter;
import org.fossasia.openevent.app.core.auth.signup.SignUpView;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.reactivex.Observable;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class SignUpPresenterTest {
    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock private AuthService authModel;
    @Mock private SignUpView signUpView;

    private SignUpPresenter signUpPresenter;

    private final HostSelectionInterceptor interceptor = new HostSelectionInterceptor();

    private static final String EMAIL = "test@test.in";
    private static final String PASSWORD = "password";
    private static final User USER = User.builder().email(EMAIL).password(PASSWORD).build();

    private static final User REGISTERED_USER = User.builder().id(2).build();

    @Before
    public void setUp() {
        signUpPresenter = new SignUpPresenter(authModel, interceptor);
        signUpPresenter.attach(signUpView);
        signUpPresenter.getUser().setEmail(EMAIL);
        signUpPresenter.getUser().setPassword(PASSWORD);
    }

    @Test
    public void shouldShowSuccessMessageOnSignUpSuccess() {
        when(authModel.signUp(USER)).thenReturn(Observable.just(REGISTERED_USER));

        signUpPresenter.signUp();

        verify(signUpView).onSuccess(anyString());
    }

    @Test
    public void shouldShowErrorMessageOnSignUpError() {
        when(authModel.signUp(USER)).thenReturn(Observable.error(Logger.TEST_ERROR));

        signUpPresenter.signUp();

        verify(signUpView).showError(Logger.TEST_MESSAGE);
    }

    @Test
    public void testProgressbarOnSignUpSuccess() {
        when(authModel.signUp(USER)).thenReturn(Observable.just(REGISTERED_USER));

        signUpPresenter.signUp();

        InOrder inOrder = inOrder(signUpView);

        inOrder.verify(signUpView).showProgress(true);
        inOrder.verify(signUpView).showProgress(false);
    }

    @Test
    public void testProgressbarOnSignUpError() {
        when(authModel.signUp(USER)).thenReturn(Observable.error(Logger.TEST_ERROR));

        signUpPresenter.signUp();

        InOrder inOrder = inOrder(signUpView);

        inOrder.verify(signUpView).showProgress(true);
        inOrder.verify(signUpView).showProgress(false);
    }
}
