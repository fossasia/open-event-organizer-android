package com.eventyay.organizer.core.sponsor.create;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.eventyay.organizer.common.ContextManager;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.sponsor.Sponsor;
import com.eventyay.organizer.data.sponsor.SponsorRepository;
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
public class CreateSponsorViewModelTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Mock
    private SponsorRepository sponsorRepository;
    @Mock
    private Event event;

    private CreateSponsorViewModel createSponsorViewModel;

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

        createSponsorViewModel = new CreateSponsorViewModel(sponsorRepository);
        ContextManager.setSelectedEvent(null);
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldShowSuccessOnCreated() {
        String successString = "Sponsor Created";
        Sponsor sponsor = createSponsorViewModel.getSponsor();
        when(sponsorRepository.createSponsor(sponsor)).thenReturn(Observable.just(sponsor));
        ContextManager.setSelectedEvent(event);

        InOrder inOrder = Mockito.inOrder(progress, dismiss, success);

        createSponsorViewModel.getProgress().observeForever(progress);
        createSponsorViewModel.getDismiss().observeForever(dismiss);
        createSponsorViewModel.getSuccess().observeForever(success);

        createSponsorViewModel.createSponsor();

        inOrder.verify(progress).onChanged(true);
        inOrder.verify(success).onChanged(successString);
        inOrder.verify(dismiss).onChanged(null);
        inOrder.verify(progress).onChanged(false);

        ContextManager.setSelectedEvent(null);
    }

    @Test
    public void shouldShowErrorOnFailure() {
        Sponsor sponsor = createSponsorViewModel.getSponsor();
        when(sponsorRepository.createSponsor(sponsor)).thenReturn(Observable.error(new Throwable("Error")));
        ContextManager.setSelectedEvent(event);

        InOrder inOrder = Mockito.inOrder(progress, error);

        createSponsorViewModel.getProgress().observeForever(progress);
        createSponsorViewModel.getError().observeForever(error);

        createSponsorViewModel.createSponsor();

        inOrder.verify(progress).onChanged(true);
        inOrder.verify(error).onChanged("Error");
        inOrder.verify(progress).onChanged(false);

        ContextManager.setSelectedEvent(null);
    }

    @Test
    public void shouldShowSuccessOnUpdate() {
        String successString = "Sponsor Updated";
        Sponsor sponsor = createSponsorViewModel.getSponsor();
        when(sponsorRepository.updateSponsor(sponsor)).thenReturn(Observable.just(sponsor));
        ContextManager.setSelectedEvent(event);

        InOrder inOrder = Mockito.inOrder(progress, dismiss, success);

        createSponsorViewModel.getProgress().observeForever(progress);
        createSponsorViewModel.getDismiss().observeForever(dismiss);
        createSponsorViewModel.getSuccess().observeForever(success);

        createSponsorViewModel.updateSponsor();

        inOrder.verify(progress).onChanged(true);
        inOrder.verify(success).onChanged(successString);
        inOrder.verify(dismiss).onChanged(null);
        inOrder.verify(progress).onChanged(false);

        ContextManager.setSelectedEvent(null);
    }

    @Test
    public void shouldShowErrorOnUpdateFailure() {
        Sponsor sponsor = createSponsorViewModel.getSponsor();
        when(sponsorRepository.updateSponsor(sponsor)).thenReturn(Observable.error(new Throwable("Error")));
        ContextManager.setSelectedEvent(event);

        InOrder inOrder = Mockito.inOrder(progress, error);

        createSponsorViewModel.getProgress().observeForever(progress);
        createSponsorViewModel.getError().observeForever(error);

        createSponsorViewModel.updateSponsor();

        inOrder.verify(progress).onChanged(true);
        inOrder.verify(error).onChanged("Error");
        inOrder.verify(progress).onChanged(false);

        ContextManager.setSelectedEvent(null);
    }
}
