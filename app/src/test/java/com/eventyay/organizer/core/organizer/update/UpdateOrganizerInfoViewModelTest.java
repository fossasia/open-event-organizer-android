package com.eventyay.organizer.core.organizer.update;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;

import com.eventyay.organizer.common.ContextManager;
import com.eventyay.organizer.data.event.Event;
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

import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class UpdateOrganizerInfoViewModelTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();
    @Mock
    private Event event;
    @Mock
    private UserRepository userRepository;

    private UpdateOrganizerInfoViewModel updateOrganizerInfoViewModel;

    @Mock
    Observer<String> error;
    @Mock
    Observer<Boolean> progress;
    @Mock
    Observer<String> success;
    @Mock
    Observer<Void> dismiss;

    @Before
    public void setUp() {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
        ContextManager.setSelectedEvent(event);

        updateOrganizerInfoViewModel = new UpdateOrganizerInfoViewModel(userRepository);
        ContextManager.setSelectedEvent(null);
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldShowSuccessOnUpdate() {
        String successString = "User Updated";
        User user = updateOrganizerInfoViewModel.getUser();
        when(userRepository.updateUser(user)).thenReturn(Observable.just(user));
        ContextManager.setSelectedEvent(event);

        InOrder inOrder = Mockito.inOrder(progress, dismiss, success);

        updateOrganizerInfoViewModel.getProgress().observeForever(progress);
        updateOrganizerInfoViewModel.getDismiss().observeForever(dismiss);
        updateOrganizerInfoViewModel.getSuccess().observeForever(success);

        updateOrganizerInfoViewModel.updateOrganizer();

        inOrder.verify(progress).onChanged(true);
        inOrder.verify(success).onChanged(successString);
        inOrder.verify(dismiss).onChanged(null);
        inOrder.verify(progress).onChanged(false);

        ContextManager.setSelectedEvent(null);
    }

    @Test
    public void shouldShowErrorOnUpdateFailure() {
        User user = updateOrganizerInfoViewModel.getUser();
        when(userRepository.updateUser(user)).thenReturn(Observable.error(new Throwable("Error")));
        ContextManager.setSelectedEvent(event);

        InOrder inOrder = Mockito.inOrder(progress, error);

        updateOrganizerInfoViewModel.getProgress().observeForever(progress);
        updateOrganizerInfoViewModel.getError().observeForever(error);

        updateOrganizerInfoViewModel.updateOrganizer();

        inOrder.verify(progress).onChanged(true);
        inOrder.verify(error).onChanged("Error");
        inOrder.verify(progress).onChanged(false);

        ContextManager.setSelectedEvent(null);
    }

}
