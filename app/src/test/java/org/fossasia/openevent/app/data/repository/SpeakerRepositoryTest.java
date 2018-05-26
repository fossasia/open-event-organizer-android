package org.fossasia.openevent.app.data.repository;

import com.raizlabs.android.dbflow.sql.language.SQLOperator;

import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.data.AbstractObservable;
import org.fossasia.openevent.app.data.Repository;
import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.data.speaker.Speaker;
import org.fossasia.openevent.app.data.speaker.SpeakerApi;
import org.fossasia.openevent.app.data.speaker.SpeakerRepositoryImpl;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SpeakerRepositoryTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private SpeakerRepositoryImpl speakerRepository;
    private static final Speaker SPEAKER = new Speaker();
    private static final Event EVENT = new Event();
    private static final long ID = 10L;

    @Mock
    private SpeakerApi speakerApi;
    @Mock private Repository repository;

    static {
        SPEAKER.setEvent(EVENT);
        SPEAKER.setId(ID);
    }

    @Before
    public void setUp() {
        when(repository.observableOf(Speaker.class)).thenReturn(new AbstractObservable.AbstractObservableBuilder<>(repository));
        speakerRepository = new SpeakerRepositoryImpl(speakerApi, repository);
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
    public void shouldReturnConnectionErrorOnGetSpeakersWithReload() {
        when(repository.isConnected()).thenReturn(false);

        Observable<Speaker> speakerObservable = speakerRepository.getSpeakers(ID, true);

        speakerObservable
            .test()
            .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    @Test
    public void shouldReturnConnectionErrorOnGetSpeakersWithNoneSaved() {
        when(repository.isConnected()).thenReturn(false);
        when(repository.getItems(eq(Speaker.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        Observable<Speaker> speakerObservable = speakerRepository.getSpeakers(ID, true);

        speakerObservable
            .test()
            .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    // Network up tests

    @Test
    public void shouldCallGetSpeakersServiceOnReload() {
        when(repository.isConnected()).thenReturn(true);
        when(speakerApi.getSpeakers(ID)).thenReturn(Observable.empty());
        when(repository.getItems(eq(Speaker.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        speakerRepository.getSpeakers(ID, true).subscribe();

        verify(speakerApi).getSpeakers(ID);
    }

    @Test
    public void shouldCallGetSpeakersServiceWithNoneSaved() {
        when(repository.isConnected()).thenReturn(true);
        when(speakerApi.getSpeakers(ID)).thenReturn(Observable.empty());
        when(repository.getItems(eq(Speaker.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        speakerRepository.getSpeakers(ID, false).subscribe();

        verify(speakerApi).getSpeakers(ID);
    }

    @Test
    public void shouldSaveSpeakersOnGet() {
        List<Speaker> speakers = new ArrayList<>();
        speakers.add(SPEAKER);

        when(repository.isConnected()).thenReturn(true);
        when(speakerApi.getSpeakers(ID)).thenReturn(Observable.just(speakers));
        when(repository.syncSave(eq(Speaker.class), eq(speakers), any(), any())).thenReturn(Completable.complete());
        when(repository.getItems(eq(Speaker.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        speakerRepository.getSpeakers(ID, true).subscribe();

        verify(repository).syncSave(eq(Speaker.class), eq(speakers), any(), any());
    }
}
