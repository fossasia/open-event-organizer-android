package com.eventyay.organizer.core.track.update;

import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import com.eventyay.organizer.common.ContextManager;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.tracks.Track;
import com.eventyay.organizer.data.tracks.TrackRepository;
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
public class UpdateTrackViewModelTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule public TestRule rule = new InstantTaskExecutorRule();

    @Mock private TrackRepository trackRepository;
    @Mock private Event event;

    private UpdateTrackViewModel updateTrackViewModel;

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

        updateTrackViewModel = new UpdateTrackViewModel(trackRepository);
        ContextManager.setSelectedEvent(null);
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldShowSuccessOnUpdate() {
        String successString = "Track Updated";
        Track track = updateTrackViewModel.getTrack();
        when(trackRepository.updateTrack(track)).thenReturn(Observable.just(track));
        ContextManager.setSelectedEvent(event);

        InOrder inOrder = Mockito.inOrder(progress, dismiss, success);

        updateTrackViewModel.getProgress().observeForever(progress);
        updateTrackViewModel.getDismiss().observeForever(dismiss);
        updateTrackViewModel.getSuccess().observeForever(success);

        updateTrackViewModel.updateTrack();

        inOrder.verify(progress).onChanged(true);
        inOrder.verify(success).onChanged(successString);
        inOrder.verify(dismiss).onChanged(null);
        inOrder.verify(progress).onChanged(false);

        ContextManager.setSelectedEvent(null);
    }

    @Test
    public void shouldShowErrorOnUpdateFailure() {
        Track track = updateTrackViewModel.getTrack();
        when(trackRepository.updateTrack(track))
                .thenReturn(Observable.error(new Throwable("Error")));
        ContextManager.setSelectedEvent(event);

        InOrder inOrder = Mockito.inOrder(progress, error);

        updateTrackViewModel.getProgress().observeForever(progress);
        updateTrackViewModel.getError().observeForever(error);

        updateTrackViewModel.updateTrack();

        inOrder.verify(progress).onChanged(true);
        inOrder.verify(error).onChanged("Error");
        inOrder.verify(progress).onChanged(false);

        ContextManager.setSelectedEvent(null);
    }
}
