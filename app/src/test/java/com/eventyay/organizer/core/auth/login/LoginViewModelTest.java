package com.eventyay.organizer.core.auth.login;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;

import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.Preferences;
import com.eventyay.organizer.data.auth.AuthService;
import com.eventyay.organizer.data.auth.model.Login;
import com.eventyay.organizer.data.encryption.EncryptionService;
import com.eventyay.organizer.data.network.HostSelectionInterceptor;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
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

import static org.mockito.Mockito.verify;


@RunWith(JUnit4.class)
public class LoginViewModelTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Mock
    private Preferences sharedPreferenceModel;
    @Mock
    private AuthService authModel;
    @Mock
    private HostSelectionInterceptor interceptor;
    @Mock
    private EncryptionService encryptionService;

    private static final String EMAIL = "test";
    private static final String PASSWORD = "test";
    private static final Login LOGIN = new Login(EMAIL, PASSWORD);
    private LoginViewModel loginViewModel;

    @Mock
    Observer<Void> actionLogin;
    @Mock
    Observer<String> error;
    @Mock
    Observer<Set<String>> email;
    @Mock
    Observer<Boolean> progress;

    @Before
    public void setUp() {
        loginViewModel = new LoginViewModel(authModel, interceptor, sharedPreferenceModel, encryptionService);
        loginViewModel.getLoginModel().setEmail(EMAIL);
        loginViewModel.getLoginModel().setPassword(PASSWORD);
    }

    @Test
    public void shouldLoginSuccessfully() {
        Mockito.when(authModel.login(LOGIN))
            .thenReturn(Completable.complete());

        InOrder inOrder = Mockito.inOrder(authModel, progress, actionLogin);

        loginViewModel.getProgress().observeForever(progress);
        loginViewModel.getActionLogIn().observeForever(actionLogin);

        loginViewModel.login();

        inOrder.verify(authModel).login(LOGIN);
        inOrder.verify(progress).onChanged(true);
        inOrder.verify(actionLogin).onChanged(null);
        inOrder.verify(progress).onChanged(false);
    }

    @Test
    public void shouldShowLoginError() {
        String errorString = "Test Error";
        Mockito.when(authModel.login(LOGIN))
            .thenReturn(Completable.error(Logger.TEST_ERROR));

        InOrder inOrder = Mockito.inOrder(authModel, progress, error);

        loginViewModel.getError().observeForever(error);
        loginViewModel.getProgress().observeForever(progress);

        loginViewModel.login();

        inOrder.verify(authModel).login(LOGIN);
        inOrder.verify(progress).onChanged(true);
        inOrder.verify(error).onChanged(errorString);
        inOrder.verify(progress).onChanged(false);
    }

    @Test
    public void shouldNotLoginAutomatically() {
        Mockito.when(authModel.isLoggedIn()).thenReturn(false);

        loginViewModel.getActionLogIn().observeForever(actionLogin);

        verify(actionLogin, Mockito.never()).onChanged(null);
    }
}
