package com.eventyay.organizer.core.auth.reset;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.Preferences;
import com.eventyay.organizer.data.auth.AuthService;
import com.eventyay.organizer.data.network.HostSelectionInterceptor;

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

import io.reactivex.Completable;

@RunWith(JUnit4.class)
public class ResetPasswordViewModelTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();
    @Mock
    private AuthService tokenSubmitModel;
    private final HostSelectionInterceptor interceptor = new HostSelectionInterceptor();

    @Mock
    private Preferences sharedPreferenceModel;

    @Mock
    private Observer<String> error;
    @Mock
    private Observer<String> success;
    @Mock
    private Observer<String> message;
    @Mock
    private Observer<Boolean> progress;

    private ResetPasswordViewModel resetPasswordViewModel;

    private static final String TOKEN = "330080303746871156724079532103783727154";
    private static final String PASSWORD = "password";
    private static final String CONFIRM_PASSWORD = "password";
    private static final String EMAIL = "test";

    @Before
    public void setUp() {
        resetPasswordViewModel = new ResetPasswordViewModel(tokenSubmitModel, interceptor, sharedPreferenceModel);
        resetPasswordViewModel.getSubmitToken().setPassword(PASSWORD);
        resetPasswordViewModel.getSubmitToken().setToken(TOKEN);
        resetPasswordViewModel.getSubmitToken().setConfirmPassword(CONFIRM_PASSWORD);
        resetPasswordViewModel.getRequestToken().setEmail(EMAIL);
    }

    @Test
    public void shouldSubmitTokenSuccessfully() {
        Mockito.when(tokenSubmitModel.submitToken(resetPasswordViewModel.getSubmitToken()))
            .thenReturn(Completable.complete());

        InOrder inOrder = Mockito.inOrder(tokenSubmitModel, progress, success);

        resetPasswordViewModel.getSuccess().observeForever(success);
        resetPasswordViewModel.getProgress().observeForever(progress);

        resetPasswordViewModel.submitRequest(resetPasswordViewModel.getSubmitToken());

        inOrder.verify(tokenSubmitModel).submitToken(resetPasswordViewModel.getSubmitToken());
        inOrder.verify(progress).onChanged(true);
        inOrder.verify(success).onChanged("Password Changed Successfully");
        inOrder.verify(progress).onChanged(false);
    }

    @Test
    public void shouldShowSubmitTokenError() {
        String errorString = "Test Error";
        Mockito.when(tokenSubmitModel.submitToken(resetPasswordViewModel.getSubmitToken()))
            .thenReturn(Completable.error(Logger.TEST_ERROR));

        InOrder inOrder = Mockito.inOrder(tokenSubmitModel, progress, error);

        resetPasswordViewModel.getError().observeForever(error);
        resetPasswordViewModel.getProgress().observeForever(progress);

        resetPasswordViewModel.submitRequest(resetPasswordViewModel.getSubmitToken());

        inOrder.verify(tokenSubmitModel).submitToken(resetPasswordViewModel.getSubmitToken());
        inOrder.verify(progress).onChanged(true);
        inOrder.verify(error).onChanged(errorString);
        inOrder.verify(progress).onChanged(false);
    }

    @Test
    public void shouldRequestTokenSuccessfully() {
        Mockito.when(tokenSubmitModel.requestToken(resetPasswordViewModel.getRequestToken()))
            .thenReturn(Completable.complete());

        InOrder inOrder = Mockito.inOrder(tokenSubmitModel, progress, message);

        resetPasswordViewModel.getMessage().observeForever(message);
        resetPasswordViewModel.getProgress().observeForever(progress);

        resetPasswordViewModel.requestToken(EMAIL);

        inOrder.verify(tokenSubmitModel).requestToken(resetPasswordViewModel.getRequestToken());
        inOrder.verify(progress).onChanged(true);
        inOrder.verify(message).onChanged("Token sent successfully");
        inOrder.verify(progress).onChanged(false);
    }

    @Test
    public void shouldShowRequestTokenError() {
        String errorString = "Test Error";
        Mockito.when(tokenSubmitModel.requestToken(resetPasswordViewModel.getRequestToken()))
            .thenReturn(Completable.error(Logger.TEST_ERROR));

        InOrder inOrder = Mockito.inOrder(tokenSubmitModel, progress, error);

        resetPasswordViewModel.getError().observeForever(error);
        resetPasswordViewModel.getProgress().observeForever(progress);

        resetPasswordViewModel.requestToken(EMAIL);

        inOrder.verify(tokenSubmitModel).requestToken(resetPasswordViewModel.getRequestToken());
        inOrder.verify(progress).onChanged(true);
        inOrder.verify(error).onChanged(errorString);
        inOrder.verify(progress).onChanged(false);
    }
}
