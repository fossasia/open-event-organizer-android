package com.eventyay.organizer.core.organizer.password;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.Preferences;
import com.eventyay.organizer.data.auth.AuthService;
import com.eventyay.organizer.data.auth.model.ChangePassword;
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

import io.reactivex.Completable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@RunWith(JUnit4.class)
public class ChangePasswordViewModelTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Mock
    private AuthService authModel;
    private final HostSelectionInterceptor interceptor = new HostSelectionInterceptor();

    @Mock
    private Preferences sharedPreferenceModel;

    private ChangePasswordViewModel changePasswordViewModel;

    private static final String OLD_PASSWORD = "oldTest";
    private static final String OLD_WRONG_PASSWORD = "oldWrongTest";
    private static final String NEW_PASSWORD = "newTest";
    private static final String CONFIRM_NEW_PASSWORD = "newTest";
    private static final String CONFIRM_NEW_WRONG_PASSWORD = "newWrongTest";

    private static final ChangePassword CHANGE_PASSWORD = new ChangePassword(OLD_PASSWORD, NEW_PASSWORD, CONFIRM_NEW_PASSWORD);

    @Mock
    Observer<String> error;
    @Mock
    Observer<Boolean> progress;
    @Mock
    Observer<String> success;

    @Before
    public void setUp() {
        changePasswordViewModel = new ChangePasswordViewModel(authModel, interceptor, sharedPreferenceModel);
    }

    @Test
    public void shouldChangePasswordSuccessfully() {
        Mockito.when(authModel.changePassword(CHANGE_PASSWORD))
            .thenReturn(Completable.complete());

        InOrder inOrder = Mockito.inOrder(authModel, progress, success);

        changePasswordViewModel.getProgress().observeForever(progress);
        changePasswordViewModel.getSuccess().observeForever(success);

        changePasswordViewModel.changePasswordRequest(OLD_PASSWORD, NEW_PASSWORD, CONFIRM_NEW_PASSWORD);

        inOrder.verify(authModel).changePassword(CHANGE_PASSWORD);
        inOrder.verify(progress).onChanged(true);
        inOrder.verify(success).onChanged(any());
        inOrder.verify(progress).onChanged(false);
    }

    @Test
    public void shouldShowChangePasswordError() {
        String errorString = "Test Error";
        Mockito.when(authModel.changePassword(CHANGE_PASSWORD))
            .thenReturn(Completable.error(Logger.TEST_ERROR));

        InOrder inOrder = Mockito.inOrder(authModel, progress, error);

        changePasswordViewModel.getProgress().observeForever(progress);
        changePasswordViewModel.getError().observeForever(error);

        changePasswordViewModel.changePasswordRequest(OLD_PASSWORD, NEW_PASSWORD, CONFIRM_NEW_PASSWORD);

        inOrder.verify(authModel).changePassword(CHANGE_PASSWORD);
        inOrder.verify(progress).onChanged(true);
        inOrder.verify(error).onChanged(errorString);
        inOrder.verify(progress).onChanged(false);
    }

    @Test
    public void shouldAcceptSameNewAndConfirmPassword() {
        Mockito.when(authModel.changePassword(CHANGE_PASSWORD))
            .thenReturn(Completable.complete());

        changePasswordViewModel.changePasswordRequest(OLD_PASSWORD, NEW_PASSWORD, CONFIRM_NEW_PASSWORD);

        verify(error, Mockito.never()).onChanged(any());
        verify(authModel).changePassword(CHANGE_PASSWORD);
    }

    @Test
    public void shouldShowErrorOnDifferentNewAndConfirmPassword() {
        Mockito.when(authModel.changePassword(CHANGE_PASSWORD))
            .thenReturn(Completable.complete());

        changePasswordViewModel.changePasswordRequest(OLD_PASSWORD, NEW_PASSWORD, CONFIRM_NEW_WRONG_PASSWORD);

        changePasswordViewModel.getError().observeForever(error);

        verify(error).onChanged(any());
        verify(authModel, Mockito.never()).changePassword(any());
    }

    @Test
    public void shouldAcceptCorrectOldPassword() {
        Mockito.when(authModel.changePassword(CHANGE_PASSWORD))
            .thenReturn(Completable.complete());

        InOrder inOrder = Mockito.inOrder(authModel, progress, success);

        changePasswordViewModel.getProgress().observeForever(progress);
        changePasswordViewModel.getSuccess().observeForever(success);

        changePasswordViewModel.changePasswordRequest(OLD_PASSWORD, NEW_PASSWORD, CONFIRM_NEW_PASSWORD);

        inOrder.verify(authModel).changePassword(CHANGE_PASSWORD);
        inOrder.verify(progress).onChanged(true);
        inOrder.verify(success).onChanged(any());
        inOrder.verify(progress).onChanged(false);
    }

    @Test
    public void shouldShowErrorOnWrongOldPassword() {
        String errorString = "Test Error";

        Mockito.when(authModel.changePassword(new ChangePassword(any(), NEW_PASSWORD, CONFIRM_NEW_PASSWORD)))
            .thenReturn(Completable.error(Logger.TEST_ERROR));

        Mockito.when(authModel.changePassword(CHANGE_PASSWORD))
            .thenReturn(Completable.complete());

        InOrder inOrder = Mockito.inOrder(authModel, error);

        changePasswordViewModel.getError().observeForever(error);

        changePasswordViewModel.changePasswordRequest(OLD_WRONG_PASSWORD, NEW_PASSWORD, CONFIRM_NEW_PASSWORD);

        inOrder.verify(authModel).changePassword(any());
        inOrder.verify(error).onChanged(errorString);
    }
}

