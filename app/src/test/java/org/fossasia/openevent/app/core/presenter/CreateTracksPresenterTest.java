package org.fossasia.openevent.app.core.presenter;

import org.fossasia.openevent.app.common.ContextManager;
import org.fossasia.openevent.app.core.track.create.CreateTrackPresenter;
import org.fossasia.openevent.app.core.track.create.CreateTrackView;
import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.data.tracks.Track;
import org.fossasia.openevent.app.data.tracks.TrackRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
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
public class CreateTracksPresenterTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private TrackRepository trackRepository;
    @Mock
    private CreateTrackView createTrackView;

    private CreateTrackPresenter createTrackPresenter;

    @Before
    public void setUp() {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());

        createTrackPresenter = new CreateTrackPresenter(trackRepository);
        createTrackPresenter.attach(createTrackView);
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
    public void shouldShowSuccessOnCreated() {

        ContextManager.setSelectedEvent(getEvent());

        Track track = createTrackPresenter.getTrack();
        track.setDescription(null);
        when(trackRepository.createTrack(track)).thenReturn(Observable.just(track));
        createTrackPresenter.createTrack();

        InOrder inorder = Mockito.inOrder(createTrackView);

        inorder.verify(createTrackView).showProgress(true);
        inorder.verify(createTrackView).onSuccess(anyString());
        inorder.verify(createTrackView).dismiss();
        inorder.verify(createTrackView).showProgress(false);

        ContextManager.setSelectedEvent(null);
    }

    @Test
    public void shouldShowErrorOnFailure() {

        ContextManager.setSelectedEvent(getEvent());

        Track track = createTrackPresenter.getTrack();
        track.setDescription(null);
        when(trackRepository.createTrack(track)).thenReturn(Observable.error(new Throwable("Error")));

        createTrackPresenter.createTrack();

        InOrder inorder = Mockito.inOrder(createTrackView);

        inorder.verify(createTrackView).showProgress(true);
        inorder.verify(createTrackView).showError("Error");
        inorder.verify(createTrackView).showProgress(false);

        ContextManager.setSelectedEvent(null);
    }
}
