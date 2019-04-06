package com.eventyay.organizer.core.auth.start;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.eventyay.organizer.common.Constants;
import com.eventyay.organizer.data.Preferences;
import com.eventyay.organizer.data.auth.AuthService;
import com.eventyay.organizer.data.auth.model.EmailRequest;
import com.eventyay.organizer.data.auth.model.EmailValidationResponse;
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

import io.reactivex.Observable;

import static org.mockito.Mockito.verify;

@RunWith(JUnit4.class)
public class StartViewModelTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Mock
    private Preferences sharedPreferenceModel;
    @Mock
    private AuthService authService;
    @Mock
    private HostSelectionInterceptor interceptor;
    @Mock
    private EncryptionService encryptionService;

    private static final String EMAIL = "test@gmail.com";
    private static final EmailRequest EMAIL_REQUEST = new EmailRequest(EMAIL);
    private StartViewModel startViewModel;
    private static final Set<String> SAVED_EMAILS = new HashSet<>(Arrays.asList("email1", "email2", "email3"));

    private static final EmailValidationResponse EMAIL_RESPONSE_REGISTERED = EmailValidationResponse.builder().result(true).build();

    @Mock
    private Observer<Boolean> isLoggedIn;
    @Mock
    private Observer<Boolean> isEmailRegistered;
    @Mock
    private Observer<String> error;
    @Mock
    private Observer<Set<String>> emailList;
    @Mock
    private Observer<Boolean> progress;

    @Before
    public void setUp() {
        startViewModel = new StartViewModel(authService, interceptor, sharedPreferenceModel, encryptionService);
    }

    @Test
    public void shouldNotLoginAutomatically() {
        Mockito.when(authService.isLoggedIn()).thenReturn(false);

        startViewModel.getLoginStatus().observeForever(isLoggedIn);

        verify(isLoggedIn, Mockito.never()).onChanged(true);
    }

    @Test
    public void shouldLoginAutomatically() {
        Mockito.when(authService.isLoggedIn()).thenReturn(true);

        startViewModel.getLoginStatus().observeForever(isLoggedIn);

        verify(isLoggedIn).onChanged(true);
    }

    @Test
    public void shouldAttachEmailAutomatically() {
        Mockito.when(sharedPreferenceModel.getStringSet(Constants.SHARED_PREFS_SAVED_EMAIL, null)).thenReturn(SAVED_EMAILS);
        Mockito.when(authService.isLoggedIn()).thenReturn(false);

        startViewModel.getEmailList().observeForever(emailList);

        verify(emailList).onChanged(SAVED_EMAILS);
    }

    @Test
    public void shouldNotAttachEmailAutomatically() {
        Mockito.when(sharedPreferenceModel.getStringSet(Constants.SHARED_PREFS_SAVED_EMAIL, null)).thenReturn(null);
        Mockito.when(authService.isLoggedIn()).thenReturn(false);

        startViewModel.getEmailList().observeForever(emailList);

        verify(emailList, Mockito.never()).onChanged(SAVED_EMAILS);
    }

    @Test
    public void shouldCheckIsEmailRegisteredSuccessfully() {
        Mockito.when(authService.checkEmailRegistered(EMAIL_REQUEST))
            .thenReturn(Observable.just(EMAIL_RESPONSE_REGISTERED));

        InOrder inOrder = Mockito.inOrder(authService, progress, isEmailRegistered);

        startViewModel.getProgress().observeForever(progress);
        startViewModel.getIsEmailRegistered().observeForever(isEmailRegistered);

        startViewModel.checkIsEmailRegistered(EMAIL_REQUEST);

        inOrder.verify(authService).checkEmailRegistered(EMAIL_REQUEST);
        inOrder.verify(progress).onChanged(true);
        inOrder.verify(isEmailRegistered).onChanged(false);
        inOrder.verify(progress).onChanged(false);
    }

    @Test
    public void shouldShowEmailCheckError() {
        String errorString = "Error";
        Mockito.when(authService.checkEmailRegistered(EMAIL_REQUEST))
            .thenReturn(Observable.error(new Throwable("Error")));

        InOrder inOrder = Mockito.inOrder(authService, progress, error);

        startViewModel.getError().observeForever(error);
        startViewModel.getProgress().observeForever(progress);

        startViewModel.checkIsEmailRegistered(EMAIL_REQUEST);

        inOrder.verify(authService).checkEmailRegistered(EMAIL_REQUEST);
        inOrder.verify(progress).onChanged(true);
        inOrder.verify(error).onChanged(errorString);
        inOrder.verify(progress).onChanged(false);
    }

}
