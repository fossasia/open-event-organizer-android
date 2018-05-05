package org.fossasia.openevent.app.data.repository;

import com.raizlabs.android.dbflow.sql.language.SQLOperator;

import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.data.AbstractObservable;
import org.fossasia.openevent.app.data.Repository;
import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.data.tracks.Track;
import org.fossasia.openevent.app.data.tracks.TrackApi;
import org.fossasia.openevent.app.data.tracks.TrackRepositoryImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TrackRepositoryTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private TrackRepositoryImpl trackRepository;
    private static final Track TRACK = new Track();
    private static final Event EVENT = new Event();
    private static final long ID = 10L;

    @Mock private TrackApi trackApi;
    @Mock private Repository repository;

    static {
        TRACK.setEvent(EVENT);
        TRACK.setId(ID);
    }

    @Before
    public void setUp() {
        when(repository.observableOf(Track.class)).thenReturn(new AbstractObservable.AbstractObservableBuilder<>(repository));
        trackRepository = new TrackRepositoryImpl(trackApi, repository);
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    // Network down tests

    @Test
    public void shouldReturnConnectionErrorOnCreateTrack() {
        when(repository.isConnected()).thenReturn(false);

        Observable<Track> trackObservable = trackRepository.createTrack(TRACK);

        trackObservable
            .test()
            .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    @Test
    public void shouldReturnConnectionErrorDeleteTrack() {
        when(repository.isConnected()).thenReturn(false);

        Completable trackObservable = trackRepository.deleteTrack(ID);

        trackObservable
            .test()
            .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    @Test
    public void shouldReturnConnectionErrorOnGetTrackWithReload() {
        when(repository.isConnected()).thenReturn(false);

        Observable<Track> trackObservable = trackRepository.getTrack(ID, true);

        trackObservable
            .test()
            .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    @Test
    public void shouldReturnConnectionErrorOnGetTrackWithNoneSaved() {
        when(repository.isConnected()).thenReturn(false);
        when(repository.getItems(eq(Track.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        Observable<Track> trackObservable = trackRepository.getTrack(ID, false);

        trackObservable
            .test()
            .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    @Test
    public void shouldReturnConnectionErrorOnGetTracksWithReload() {
        when(repository.isConnected()).thenReturn(false);

        Observable<Track> trackObservable = trackRepository.getTracks(ID, true);

        trackObservable
            .test()
            .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    @Test
    public void shouldReturnConnectionErrorOnGetTracksWithNoneSaved() {
        when(repository.isConnected()).thenReturn(false);
        when(repository.getItems(eq(Track.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        Observable<Track> trackObservable = trackRepository.getTracks(ID, false);

        trackObservable
            .test()
            .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    // Network up tests

    // Track Create Tests

    @Test
    public void shouldCallCreateTrackService() {
        when(repository.isConnected()).thenReturn(true);
        when(trackApi.postTrack(TRACK)).thenReturn(Observable.empty());

        trackRepository.createTrack(TRACK).subscribe();

        verify(trackApi).postTrack(TRACK);
    }

    @Test
    public void shouldSetEventOnCreatedTrack() {
        Track created = mock(Track.class);

        when(repository.isConnected()).thenReturn(true);
        when(trackApi.postTrack(TRACK)).thenReturn(Observable.just(created));
        when(repository.save(eq(Track.class), eq(created))).thenReturn(Completable.complete());

        trackRepository.createTrack(TRACK).subscribe();

        verify(created).setEvent(EVENT);
    }

    @Test
    public void shouldSaveCreatedTrack() {
        Track created = mock(Track.class);

        when(repository.isConnected()).thenReturn(true);
        when(trackApi.postTrack(TRACK)).thenReturn(Observable.just(created));
        when(repository.save(eq(Track.class), eq(created))).thenReturn(Completable.complete());

        trackRepository.createTrack(TRACK).subscribe();

        verify(repository).save(Track.class, created);
    }

    // Track Get Tests

    @Test
    public void shouldCallGetTrackServiceOnReload() {
        when(repository.isConnected()).thenReturn(true);
        when(trackApi.getTrack(ID)).thenReturn(Observable.empty());
        when(repository.getItems(eq(Track.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        trackRepository.getTrack(ID, true).subscribe();

        verify(trackApi).getTrack(ID);
    }

    @Test
    public void shouldCallGetTrackServiceWithNoneSaved() {
        when(repository.isConnected()).thenReturn(true);
        when(trackApi.getTrack(ID)).thenReturn(Observable.empty());
        when(repository.getItems(eq(Track.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        trackRepository.getTrack(ID, false).subscribe();

        verify(trackApi).getTrack(ID);
    }

    @Test
    public void shouldSaveTrackOnGet() {
        when(repository.isConnected()).thenReturn(true);
        when(trackApi.getTrack(ID)).thenReturn(Observable.just(TRACK));
        when(repository.save(eq(Track.class), eq(TRACK))).thenReturn(Completable.complete());
        when(repository.getItems(eq(Track.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        trackRepository.getTrack(ID, true).subscribe();

        verify(repository).save(Track.class, TRACK);
    }

    // Tracks Get Tests

    @Test
    public void shouldCallGetTracksServiceOnReload() {
        when(repository.isConnected()).thenReturn(true);
        when(trackApi.getTracks(ID)).thenReturn(Observable.empty());
        when(repository.getItems(eq(Track.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        trackRepository.getTracks(ID, true).subscribe();

        verify(trackApi).getTracks(ID);
    }

    @Test
    public void shouldCallGetTracksServiceWithNoneSaved() {
        when(repository.isConnected()).thenReturn(true);
        when(trackApi.getTracks(ID)).thenReturn(Observable.empty());
        when(repository.getItems(eq(Track.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        trackRepository.getTracks(ID, false).subscribe();

        verify(trackApi).getTracks(ID);
    }

    @Test
    public void shouldSaveTracksOnGet() {
        List<Track> tracks = new ArrayList<>();
        tracks.add(TRACK);

        when(repository.isConnected()).thenReturn(true);
        when(trackApi.getTracks(ID)).thenReturn(Observable.just(tracks));
        when(repository.syncSave(eq(Track.class), eq(tracks), any(), any())).thenReturn(Completable.complete());
        when(repository.getItems(eq(Track.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        trackRepository.getTracks(ID, true).subscribe();

        verify(repository).syncSave(eq(Track.class), eq(tracks), any(), any());
    }

    // Track update tests

    @Test
    public void shouldCallUpdateTrackService() {
        when(repository.isConnected()).thenReturn(true);
        when(trackApi.updateTrack(ID, TRACK)).thenReturn(Observable.empty());

        trackRepository.updateTrack(TRACK).subscribe();

        verify(trackApi).updateTrack(ID, TRACK);
    }

    @Test
    public void shouldUpdateUpdatedTrack() {
        Track updated = mock(Track.class);

        when(repository.isConnected()).thenReturn(true);
        when(trackApi.updateTrack(ID, TRACK)).thenReturn(Observable.just(updated));
        when(repository.update(eq(Track.class), eq(updated))).thenReturn(Completable.complete());

        trackRepository.updateTrack(TRACK).subscribe();

        verify(repository).update(Track.class, updated);
    }

    // Track delete tests

    @Test
    public void shouldCallDeleteTrackService() {
        when(repository.isConnected()).thenReturn(true);
        when(trackApi.deleteTrack(ID)).thenReturn(Completable.complete());

        trackRepository.deleteTrack(ID).subscribe();

        verify(trackApi).deleteTrack(ID);
    }
}
