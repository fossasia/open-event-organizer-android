package com.eventyay.organizer.core.copyright.create;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;

import com.eventyay.organizer.common.ContextManager;
import com.eventyay.organizer.core.event.copyright.CreateCopyrightViewModel;
import com.eventyay.organizer.data.copyright.Copyright;
import com.eventyay.organizer.data.copyright.CopyrightRepository;
import com.eventyay.organizer.data.event.Event;

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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class CreateCopyrightViewModelTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Mock
    private CopyrightRepository copyrightRepository;
    @Mock
    private Event event;

    private CreateCopyrightViewModel createCopyrightViewModel;

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

        createCopyrightViewModel = new CreateCopyrightViewModel(copyrightRepository);
        ContextManager.setSelectedEvent(null);
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    private Event getEvent() {
        return Event.builder().id(2L).build();
    }

    @Test
    public void shouldShowErrorOnInvalidYear() {
        Copyright copyright = createCopyrightViewModel.getCopyright();
        copyright.setYear("25");

        InOrder inOrder = Mockito.inOrder(error);

        createCopyrightViewModel.getError().observeForever(error);

        createCopyrightViewModel.createCopyright();

        inOrder.verify(error).onChanged("Please Enter a Valid Year");
    }

    @Test
    public void shouldAcceptCorrectYear() {
        ContextManager.setSelectedEvent(getEvent());

        Copyright copyright = createCopyrightViewModel.getCopyright();
        copyright.setYear("2018");

        when(copyrightRepository.createCopyright(copyright)).thenReturn(Observable.just(copyright));

        InOrder inOrder = Mockito.inOrder(success);

        createCopyrightViewModel.getSuccess().observeForever(success);

        createCopyrightViewModel.createCopyright();

        inOrder.verify(success).onChanged(anyString());

        ContextManager.setSelectedEvent(null);
    }

    @Test
    public void shouldShowErrorOnFailure() {
        ContextManager.setSelectedEvent(getEvent());

        Copyright copyright = createCopyrightViewModel.getCopyright();

        when(copyrightRepository.createCopyright(copyright)).thenReturn(Observable.error(new Throwable("Error")));

        createCopyrightViewModel.createCopyright();

        createCopyrightViewModel.getProgress().observeForever(progress);
        createCopyrightViewModel.getError().observeForever(error);

        InOrder inOrder = Mockito.inOrder(progress, error);

        createCopyrightViewModel.createCopyright();

        inOrder.verify(progress).onChanged(true);
        inOrder.verify(error).onChanged("Error");
        inOrder.verify(progress).onChanged(false);

        ContextManager.setSelectedEvent(null);
    }

    @Test
    public void shouldShowSuccessOnCreated() {
        String successString = "Copyright Created";

        ContextManager.setSelectedEvent(getEvent());

        Copyright copyright = createCopyrightViewModel.getCopyright();
        copyright.setYear(null);

        when(copyrightRepository.createCopyright(copyright)).thenReturn(Observable.just(copyright));

        InOrder inOrder = Mockito.inOrder(progress, dismiss, success);

        createCopyrightViewModel.getProgress().observeForever(progress);
        createCopyrightViewModel.getDismiss().observeForever(dismiss);
        createCopyrightViewModel.getSuccess().observeForever(success);

        createCopyrightViewModel.createCopyright();

        inOrder.verify(progress).onChanged(true);
        inOrder.verify(success).onChanged(successString);
        inOrder.verify(dismiss).onChanged(null);
        inOrder.verify(progress).onChanged(false);

        ContextManager.setSelectedEvent(null);
    }
}
