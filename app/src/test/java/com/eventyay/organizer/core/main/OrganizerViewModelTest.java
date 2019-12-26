package com.eventyay.organizer.core.main;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.eventyay.organizer.common.ContextManager;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.auth.AuthService;
import com.eventyay.organizer.data.user.User;
import com.eventyay.organizer.data.user.UserRepository;
import com.f2prateek.rx.preferences2.Preference;
import com.f2prateek.rx.preferences2.RxSharedPreferences;

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
import io.reactivex.Observable;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class OrganizerViewModelTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthService authService;
    @Mock
    private RxSharedPreferences sharedPreferences;
    @Mock
    private ContextManager contextManager;
    @Mock
    private Preference<Boolean> booleanPref;

    @Mock
    Observer<User> organizer;
    @Mock
    Observer<String> error;
    @Mock
    Observer<Void> singleEventObserver;

    private OrganizerViewModel organizerViewModel;
    private static final User ORGANIZER = new User();

    @Before
    public void setUp() {
        organizerViewModel = new OrganizerViewModel(userRepository, authService, sharedPreferences, contextManager);
    }

    @Test
    public void shouldLoadOrganizerSuccessfully() {
        when(userRepository.getOrganizer(false))
            .thenReturn(Observable.just(ORGANIZER));

        InOrder inOrder = Mockito.inOrder(userRepository, organizer);

        organizerViewModel.getOrganizer().observeForever(organizer);

        organizerViewModel.getOrganizer();

        inOrder.verify(userRepository).getOrganizer(false);
        inOrder.verify(organizer).onChanged(ORGANIZER);
    }

    @Test
    public void shouldLogoutSuccessfully() {
        when(authService.logout())
            .thenReturn(Completable.complete());

        InOrder inOrder = Mockito.inOrder(authService, singleEventObserver);

        organizerViewModel.getLogoutAction().observeForever(singleEventObserver);

        organizerViewModel.logout();

        inOrder.verify(authService).logout();
        inOrder.verify(singleEventObserver).onChanged(null);
    }

    @Test
    public void shouldShowLogoutError() {
        String errorString = "Test Error";
        when(authService.logout())
            .thenReturn(Completable.error(Logger.TEST_ERROR));

        InOrder inOrder = Mockito.inOrder(authService, error);

        organizerViewModel.getError().observeForever(error);

        organizerViewModel.logout();

        inOrder.verify(authService).logout();
        inOrder.verify(error).onChanged(errorString);
    }

    @Test
    public void shouldSetLocalDatePreference() {
        when(booleanPref.asObservable()).thenReturn(Observable.just(true));
        when(sharedPreferences.getBoolean(anyString())).thenReturn(booleanPref);

        InOrder inOrder = Mockito.inOrder(sharedPreferences, singleEventObserver);

        organizerViewModel.getLocalDatePreferenceAction().observeForever(singleEventObserver);

        organizerViewModel.setLocalDatePreferenceAction();

        inOrder.verify(sharedPreferences).getBoolean(anyString());
        inOrder.verify(singleEventObserver).onChanged(null);
    }
}
