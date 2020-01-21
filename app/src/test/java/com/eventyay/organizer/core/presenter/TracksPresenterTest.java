package com.eventyay.organizer.core.presenter;

import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.core.track.list.TracksPresenter;
import com.eventyay.organizer.core.track.list.TracksView;
import com.eventyay.organizer.data.db.DatabaseChangeListener;
import com.eventyay.organizer.data.tracks.Track;
import com.eventyay.organizer.data.tracks.TrackRepository;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
@SuppressWarnings({"PMD.CommentSize", "PMD.LineTooLong"})
public class TracksPresenterTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private TracksView tracksView;
    @Mock
    private TrackRepository trackRepository;
    @Mock
    private DatabaseChangeListener<Track> databaseChangeListener;

    private static final long ID = 42;

    private static final List<Track> TRACKS = Arrays.asList(
        Track.builder().id(2L).name("xyz").build(),
        Track.builder().id(3L).name("abc").build(),
        Track.builder().id(4L).name("pqr").build()
    );

    private TracksPresenter tracksPresenter;

    @Before
    public void setUp() {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());


        tracksPresenter = new TracksPresenter(trackRepository, databaseChangeListener);
        tracksPresenter.attach(ID, tracksView);
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldLoadTracksAutomatically() {
        when(trackRepository.getTracks(anyLong(), anyBoolean())).thenReturn(Observable.fromIterable(TRACKS));
        when(databaseChangeListener.getNotifier()).thenReturn(PublishSubject.create());

        tracksPresenter.start();

        verify(trackRepository).getTracks(ID, false);
    }

    @Test
    public void shouldShowTracksAutomatically() {
        when(trackRepository.getTracks(ID, false)).thenReturn(Observable.fromIterable(TRACKS));
        when(databaseChangeListener.getNotifier()).thenReturn(PublishSubject.create());

        tracksPresenter.start();

        verify(tracksView).showResults(TRACKS);
    }

    @Test
    public void shouldActivateChangeListenerOnStart() {
        when(trackRepository.getTracks(anyLong(), anyBoolean())).thenReturn(Observable.fromIterable(TRACKS));
        when(databaseChangeListener.getNotifier()).thenReturn(PublishSubject.create());

        tracksPresenter.start();

        verify(databaseChangeListener).startListening();
    }

    @Test
    public void shouldDisableChangeListenerOnDetach() {
        tracksPresenter.detach();

        verify(databaseChangeListener).stopListening();
    }

    @Test
    public void shouldShowEmptyViewOnNoTracks() {
        when(trackRepository.getTracks(anyLong(), anyBoolean())).thenReturn(Observable.fromIterable(new ArrayList<>()));

        tracksPresenter.loadTracks(true);

        verify(tracksView
        ).showEmptyView(true);
    }

    @Test
    public void shouldShowTracksOnSwipeRefreshSuccess() {
        when(trackRepository.getTracks(ID, true)).thenReturn(Observable.fromIterable(TRACKS));

        tracksPresenter.loadTracks(true);

        verify(tracksView).showResults(any());
    }

    @Test
    public void shouldShowErrorMessageOnSwipeRefreshError() {
        when(trackRepository.getTracks(ID, true)).thenReturn(Observable.error(Logger.TEST_ERROR));

        tracksPresenter.loadTracks(true);

        verify(tracksView).showError(Logger.TEST_ERROR.getMessage());
    }

    @Test
    public void testProgressbarOnSwipeRefreshSuccess() {
        when(trackRepository.getTracks(ID, true)).thenReturn(Observable.fromIterable(TRACKS));

        tracksPresenter.loadTracks(true);

        InOrder inOrder = Mockito.inOrder(tracksView);

        inOrder.verify(tracksView).showProgress(true);
        inOrder.verify(tracksView).onRefreshComplete(true);
        inOrder.verify(tracksView).showProgress(false);
    }

    @Test
    public void testProgressbarOnSwipeRefreshError() {
        when(trackRepository.getTracks(ID, true)).thenReturn(Observable.error(Logger.TEST_ERROR));

        tracksPresenter.loadTracks(true);

        InOrder inOrder = Mockito.inOrder(tracksView);

        inOrder.verify(tracksView).showProgress(true);
        inOrder.verify(tracksView).onRefreshComplete(false);
        inOrder.verify(tracksView).showProgress(false);
    }

    @Test
    public void testProgressbarOnSwipeRefreshNoItem() {
        List<Track> emptyList = new ArrayList<>();
        when(trackRepository.getTracks(ID, true)).thenReturn(Observable.fromIterable(emptyList));

        tracksPresenter.loadTracks(true);

        InOrder inOrder = Mockito.inOrder(tracksView);

        inOrder.verify(tracksView).showProgress(true);
        inOrder.verify(tracksView).onRefreshComplete(true);
        inOrder.verify(tracksView).showProgress(false);
    }
}
