package com.eventyay.organizer.core.speaker.details;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.session.SessionRepository;
import com.eventyay.organizer.data.speaker.Speaker;
import com.eventyay.organizer.data.speaker.SpeakerRepository;
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
public class SpeakerDetailsViewModelTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule public TestRule rule = new InstantTaskExecutorRule();

    private SpeakerDetailsViewModel speakerDetailsViewModel;

    @Mock private SpeakerRepository speakerRepository;
    @Mock private SessionRepository sessionRepository;

    private static final Speaker SPEAKER = new Speaker();
    private static final long SPEAKER_ID = 1;

    @Mock Observer<String> error;
    @Mock Observer<Speaker> speaker;

    @Before
    public void setUp() {
        speakerDetailsViewModel = new SpeakerDetailsViewModel(speakerRepository, sessionRepository);
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(
                schedulerCallable -> Schedulers.trampoline());
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldLoadSpeakerSuccessfully() {
        when(speakerRepository.getSpeaker(anyLong(), anyBoolean()))
                .thenReturn(Observable.just(SPEAKER));

        InOrder inOrder = Mockito.inOrder(speakerRepository, speaker);

        speakerDetailsViewModel.getSpeaker(SPEAKER_ID, false).observeForever(speaker);

        inOrder.verify(speakerRepository).getSpeaker(SPEAKER_ID, false);
        inOrder.verify(speaker).onChanged(SPEAKER);
    }

    @Test
    public void shouldShowErrorOnLoadingUnsuccessfully() {
        when(speakerRepository.getSpeaker(anyLong(), anyBoolean()))
                .thenReturn(Observable.error(new Throwable("Error")));

        InOrder inOrder = Mockito.inOrder(speakerRepository, speaker, error);

        speakerDetailsViewModel.getError().observeForever(error);
        speakerDetailsViewModel.getSpeaker(SPEAKER_ID, false).observeForever(speaker);

        inOrder.verify(speakerRepository).getSpeaker(SPEAKER_ID, false);
        inOrder.verify(error).onChanged("Error");
    }

    @Test
    public void shouldShowSpeakerDetailsOnSwipeRefresh() {
        when(speakerRepository.getSpeaker(SPEAKER_ID, true)).thenReturn(Observable.just(SPEAKER));

        InOrder inOrder = Mockito.inOrder(speakerRepository, speaker);

        speakerDetailsViewModel.getSpeaker(SPEAKER_ID, true).observeForever(speaker);

        inOrder.verify(speakerRepository).getSpeaker(SPEAKER_ID, true);
        inOrder.verify(speaker).onChanged(SPEAKER);
    }

    @Test
    public void shouldShowErrorMessageOnSwipeRefersh() {
        when(speakerRepository.getSpeaker(SPEAKER_ID, true))
                .thenReturn(Observable.error(Logger.TEST_ERROR));

        InOrder inOrder = Mockito.inOrder(speakerRepository, error);

        speakerDetailsViewModel.getSpeaker(SPEAKER_ID, true).observeForever(speaker);
        speakerDetailsViewModel.getError().observeForever(error);

        inOrder.verify(speakerRepository).getSpeaker(SPEAKER_ID, true);
        inOrder.verify(error).onChanged(Logger.TEST_ERROR.getMessage());
    }
}
