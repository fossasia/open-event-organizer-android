package com.eventyay.organizer.core.copyright.update;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import com.eventyay.organizer.common.ContextManager;
import com.eventyay.organizer.core.event.copyright.update.UpdateCopyrightViewModel;
import com.eventyay.organizer.data.copyright.Copyright;
import com.eventyay.organizer.data.copyright.CopyrightRepository;
import com.eventyay.organizer.data.event.Event;
import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
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

@RunWith(JUnit4.class)
public class UpdateCopyrightViewModelTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule public TestRule rule = new InstantTaskExecutorRule();

    @Mock private CopyrightRepository copyrightRepository;
    @Mock private Event event;

    private UpdateCopyrightViewModel updateCopyrightViewModel;

    @Mock Observer<String> error;
    @Mock Observer<Boolean> progress;
    @Mock Observer<String> success;
    @Mock Observer<Void> dismiss;

    @Before
    public void setUp() {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(
                schedulerCallable -> Schedulers.trampoline());
        ContextManager.setSelectedEvent(event);

        updateCopyrightViewModel = new UpdateCopyrightViewModel(copyrightRepository);
        ContextManager.setSelectedEvent(null);
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldShowErrorOnInvalidYear() {
        Copyright copyright = updateCopyrightViewModel.getCopyright();
        copyright.setYear("25");

        InOrder inOrder = Mockito.inOrder(error);

        updateCopyrightViewModel.getError().observeForever(error);

        updateCopyrightViewModel.updateCopyright();

        inOrder.verify(error).onChanged("Please Enter a Valid Year");
    }

    @Test
    public void shouldAcceptCorrectYear() {
        ContextManager.setSelectedEvent(event);

        Copyright copyright = updateCopyrightViewModel.getCopyright();
        copyright.setYear("2018");

        when(copyrightRepository.updateCopyright(copyright)).thenReturn(Observable.just(copyright));

        InOrder inOrder = Mockito.inOrder(success);

        updateCopyrightViewModel.getSuccess().observeForever(success);

        updateCopyrightViewModel.updateCopyright();

        inOrder.verify(success).onChanged(anyString());

        ContextManager.setSelectedEvent(null);
    }

    @Test
    public void shouldShowErrorOnFailure() {
        ContextManager.setSelectedEvent(event);

        Copyright copyright = updateCopyrightViewModel.getCopyright();

        when(copyrightRepository.updateCopyright(copyright))
                .thenReturn(Observable.error(new Throwable("Error")));

        updateCopyrightViewModel.updateCopyright();

        updateCopyrightViewModel.getProgress().observeForever(progress);
        updateCopyrightViewModel.getError().observeForever(error);

        InOrder inOrder = Mockito.inOrder(progress, error);

        updateCopyrightViewModel.updateCopyright();

        inOrder.verify(progress).onChanged(true);
        inOrder.verify(error).onChanged("Error");
        inOrder.verify(progress).onChanged(false);

        ContextManager.setSelectedEvent(null);
    }

    @Test
    public void shouldShowSuccessOnUpdated() {
        String successString = "Copyright Updated";

        ContextManager.setSelectedEvent(event);

        Copyright copyright = updateCopyrightViewModel.getCopyright();
        copyright.setYear(null);

        when(copyrightRepository.updateCopyright(copyright)).thenReturn(Observable.just(copyright));

        InOrder inOrder = Mockito.inOrder(progress, dismiss, success);

        updateCopyrightViewModel.getProgress().observeForever(progress);
        updateCopyrightViewModel.getDismiss().observeForever(dismiss);
        updateCopyrightViewModel.getSuccess().observeForever(success);

        updateCopyrightViewModel.updateCopyright();

        inOrder.verify(progress).onChanged(true);
        inOrder.verify(success).onChanged(successString);
        inOrder.verify(dismiss).onChanged(null);
        inOrder.verify(progress).onChanged(false);

        ContextManager.setSelectedEvent(null);
    }
}
