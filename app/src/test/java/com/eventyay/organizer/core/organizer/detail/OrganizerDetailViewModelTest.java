package com.eventyay.organizer.core.organizer.detail;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.auth.AuthService;
import com.eventyay.organizer.data.user.User;
import com.eventyay.organizer.data.user.UserRepository;

import org.junit.After;
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

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class OrganizerDetailViewModelTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthService authService;

    private OrganizerDetailViewModel organizerDetailViewModel;

    private static final User USER = new User();

    @Mock
    Observer<String> error;
    @Mock
    Observer<Boolean> progress;

    @Before
    public void setUp() {
        organizerDetailViewModel = new OrganizerDetailViewModel(userRepository, authService);

        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldLoadOrganizerSuccessfully() {
        when(userRepository.getOrganizer(anyBoolean())).thenReturn(Observable.just(USER));

        InOrder inOrder = Mockito.inOrder(progress);

        organizerDetailViewModel.getProgress().observeForever(progress);

        organizerDetailViewModel.loadOrganizer(false);

        inOrder.verify(progress).onChanged(true);
        inOrder.verify(progress).onChanged(false);
    }

    @Test
    public void shouldShowErrorOnOrganizerLoadFailure() {
        when(userRepository.getOrganizer(anyBoolean())).thenReturn(Observable.error(new Throwable("Error")));

        InOrder inOrder = Mockito.inOrder(progress, error);

        organizerDetailViewModel.getProgress().observeForever(progress);
        organizerDetailViewModel.getError().observeForever(error);

        organizerDetailViewModel.loadOrganizer(false);

        inOrder.verify(progress).onChanged(true);
        inOrder.verify(error).onChanged(anyString());
        inOrder.verify(progress).onChanged(false);
    }

    @Test
    public void shouldShowOrganizerDetailsOnSwipeRefreshSuccess() {
        when(userRepository.getOrganizer(true)).thenReturn(Observable.just(USER));

        organizerDetailViewModel.loadOrganizer(true);
    }

    @Test
    public void shouldShowErrorMessageOnSwipeRefreshError() {
        when(userRepository.getOrganizer(true)).thenReturn(Observable.error(new Throwable("Error")));

        InOrder inOrder = Mockito.inOrder(error);

        organizerDetailViewModel.getError().observeForever(error);
        organizerDetailViewModel.loadOrganizer(true);

        inOrder.verify(error).onChanged("Error");
    }

    @Test
    public void testProgressbarOnSwipeRefreshSuccess() {
        when(userRepository.getOrganizer(true)).thenReturn(Observable.just(USER));

        InOrder inOrder = Mockito.inOrder(progress);

        organizerDetailViewModel.getProgress().observeForever(progress);
        organizerDetailViewModel.getProgress().observeForever(progress);

        organizerDetailViewModel.loadOrganizer(true);

        inOrder.verify(progress).onChanged(true);
        inOrder.verify(progress).onChanged(false);
    }

    @Test
    public void testProgressbarOnSwipeRefreshError() {
        when(userRepository.getOrganizer(true)).thenReturn(Observable.error(Logger.TEST_ERROR));

        InOrder inOrder = Mockito.inOrder(progress, error);

        organizerDetailViewModel.getProgress().observeForever(progress);
        organizerDetailViewModel.getError().observeForever(error);
        organizerDetailViewModel.getProgress().observeForever(progress);

        organizerDetailViewModel.loadOrganizer(true);

        inOrder.verify(progress).onChanged(true);
        inOrder.verify(error).onChanged(anyString());
        inOrder.verify(progress).onChanged(false);
    }
}
