package com.eventyay.organizer.core.auth.signup;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.Preferences;
import com.eventyay.organizer.data.auth.AuthService;
import com.eventyay.organizer.data.network.HostSelectionInterceptor;
import com.eventyay.organizer.data.user.User;

import org.junit.After;
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
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class SignUpViewModelTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private AuthService authModel;
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private SignUpViewModel signUpViewModel;

    private final HostSelectionInterceptor interceptor = new HostSelectionInterceptor();

    @Mock
    private Preferences sharedPreferenceModel;

    private static final String EMAIL = "test@test.in";
    private static final String PASSWORD = "password";
    private static final User USER = User.builder().email(EMAIL).password(PASSWORD).build();

    private static final User REGISTERED_USER = User.builder().id(2).build();

    @Mock
    Observer<String> error;
    @Mock
    Observer<Boolean> progress;
    @Mock
    Observer<String> success;

    @Before
    public void setUp() {
        RxJavaPlugins.reset();
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());

        signUpViewModel = new SignUpViewModel(authModel, interceptor, sharedPreferenceModel);
        signUpViewModel.getUser().setEmail(EMAIL);
        signUpViewModel.getUser().setPassword(PASSWORD);
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldShowSuccessMessageOnSignUpSuccess() {
        when(authModel.signUp(USER)).thenReturn(Observable.just(REGISTERED_USER));
        InOrder inOrder = inOrder(progress, success);

        signUpViewModel.getProgress().observeForever(progress);
        signUpViewModel.getSuccess().observeForever(success);

        signUpViewModel.signUp();

        inOrder.verify(progress).onChanged(true);
        inOrder.verify(success).onChanged(anyString());
        inOrder.verify(progress).onChanged(false);
    }

    @Test
    public void shouldShowErrorMessageOnSignUpError() {
        when(authModel.signUp(USER)).thenReturn(Observable.error(new Throwable("Error")));
        InOrder inOrder = inOrder(progress, error);

        signUpViewModel.getProgress().observeForever(progress);
        signUpViewModel.getError().observeForever(error);

        signUpViewModel.signUp();

        inOrder.verify(progress).onChanged(true);
        inOrder.verify(error).onChanged("Error");
        inOrder.verify(progress).onChanged(false);
    }

    @Test
    public void testProgressbarOnSignUpSuccess() {
        when(authModel.signUp(USER)).thenReturn(Observable.just(REGISTERED_USER));

        signUpViewModel.getProgress().observeForever(progress);
        signUpViewModel.signUp();

        InOrder inOrder = inOrder(progress);

        inOrder.verify(progress).onChanged(true);
        inOrder.verify(progress).onChanged(false);
    }

    @Test
    public void testProgressbarOnSignUpError() {
        when(authModel.signUp(USER)).thenReturn(Observable.error(Logger.TEST_ERROR));
        signUpViewModel.getProgress().observeForever(progress);

        signUpViewModel.signUp();

        InOrder inOrder = inOrder(progress);

        inOrder.verify(progress).onChanged(true);
        inOrder.verify(progress).onChanged(false);
    }
}
