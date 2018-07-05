package org.fossasia.openevent.app.data.repository;

import com.raizlabs.android.dbflow.sql.language.SQLOperator;

import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.data.AbstractObservable;
import org.fossasia.openevent.app.data.Repository;
import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.data.speakerscall.SpeakersCall;
import org.fossasia.openevent.app.data.speakerscall.SpeakersCallApi;
import org.fossasia.openevent.app.data.speakerscall.SpeakersCallRepositoryImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

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

public class SpeakersCallRepositoryTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private SpeakersCallRepositoryImpl speakersCallRepository;
    private static final SpeakersCall SPEAKERS_CALL = new SpeakersCall();
    private static final Event EVENT = new Event();
    private static final long ID = 10L;

    @Mock
    private SpeakersCallApi speakersCallApi;
    @Mock private Repository repository;

    static {
        SPEAKERS_CALL.setEvent(EVENT);
        SPEAKERS_CALL.setId(ID);
    }

    @Before
    public void setUp() {
        when(repository.observableOf(SpeakersCall.class)).thenReturn(new AbstractObservable.AbstractObservableBuilder<>(repository));
        speakersCallRepository = new SpeakersCallRepositoryImpl(speakersCallApi, repository);
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    // Network Down Tests

    @Test
    public void shouldReturnConnectionErrorOnCreateSpeakersCall() {
        when(repository.isConnected()).thenReturn(false);

        Observable<SpeakersCall> speakersCallObservable = speakersCallRepository.createSpeakersCall(SPEAKERS_CALL);

        speakersCallObservable
            .test()
            .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    @Test
    public void shouldReturnConnectionErrorOnGetSpeakersCallWithReload() {
        when(repository.isConnected()).thenReturn(false);

        Observable<SpeakersCall> speakersCallObservable = speakersCallRepository.getSpeakersCall(ID, true);

        speakersCallObservable
            .test()
            .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    @Test
    public void shouldReturnConnectionErrorOnGetSpeakersCallWithNoneSaved() {
        when(repository.isConnected()).thenReturn(false);
        when(repository.getItems(eq(SpeakersCall.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        Observable<SpeakersCall> speakersCallObservable = speakersCallRepository.getSpeakersCall(ID, false);

        speakersCallObservable
            .test()
            .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    // SpeakersCall Create Tests

    @Test
    public void shouldCallCreateSpeakersCallService() {
        when(repository.isConnected()).thenReturn(true);
        when(speakersCallApi.postSpeakersCall(SPEAKERS_CALL)).thenReturn(Observable.empty());

        speakersCallRepository.createSpeakersCall(SPEAKERS_CALL).subscribe();

        verify(speakersCallApi).postSpeakersCall(SPEAKERS_CALL);
    }

    @Test
    public void shouldSetEventOnCreatedSpeakersCall() {
        SpeakersCall created = mock(SpeakersCall.class);

        when(repository.isConnected()).thenReturn(true);
        when(speakersCallApi.postSpeakersCall(SPEAKERS_CALL)).thenReturn(Observable.just(created));
        when(repository.save(eq(SpeakersCall.class), eq(created))).thenReturn(Completable.complete());

        speakersCallRepository.createSpeakersCall(SPEAKERS_CALL).subscribe();

        verify(created).setEvent(EVENT);
    }

    @Test
    public void shouldSaveCreatedSpeakersCall() {
        SpeakersCall created = mock(SpeakersCall.class);

        when(repository.isConnected()).thenReturn(true);
        when(speakersCallApi.postSpeakersCall(SPEAKERS_CALL)).thenReturn(Observable.just(created));
        when(repository.save(eq(SpeakersCall.class), eq(created))).thenReturn(Completable.complete());

        speakersCallRepository.createSpeakersCall(SPEAKERS_CALL).subscribe();

        verify(repository).save(SpeakersCall.class, created);
    }

    // SpeakersCall Get Tests

    @Test
    public void shouldCallGetSpeakersCallServiceOnReload() {
        when(repository.isConnected()).thenReturn(true);
        when(speakersCallApi.getSpeakersCall(ID)).thenReturn(Observable.empty());
        when(repository.getItems(eq(SpeakersCall.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        speakersCallRepository.getSpeakersCall(ID, true).subscribe();

        verify(speakersCallApi).getSpeakersCall(ID);
    }

    @Test
    public void shouldCallGetSpeakersCallServiceWithNoneSaved() {
        when(repository.isConnected()).thenReturn(true);
        when(speakersCallApi.getSpeakersCall(ID)).thenReturn(Observable.empty());
        when(repository.getItems(eq(SpeakersCall.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        speakersCallRepository.getSpeakersCall(ID, false).subscribe();

        verify(speakersCallApi).getSpeakersCall(ID);
    }

    @Test
    public void shouldSaveSpeakersCallOnGet() {
        when(repository.isConnected()).thenReturn(true);
        when(speakersCallApi.getSpeakersCall(ID)).thenReturn(Observable.just(SPEAKERS_CALL));
        when(repository.save(eq(SpeakersCall.class), eq(SPEAKERS_CALL))).thenReturn(Completable.complete());
        when(repository.getItems(eq(SpeakersCall.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        speakersCallRepository.getSpeakersCall(ID, true).subscribe();

        verify(repository).save(SpeakersCall.class, SPEAKERS_CALL);
    }

    // SpeakersCall update tests

    @Test
    public void shouldCallUpdateSpeakersCallService() {
        when(repository.isConnected()).thenReturn(true);
        when(speakersCallApi.updateSpeakersCall(ID, SPEAKERS_CALL)).thenReturn(Observable.empty());

        speakersCallRepository.updateSpeakersCall(SPEAKERS_CALL).subscribe();

        verify(speakersCallApi).updateSpeakersCall(ID, SPEAKERS_CALL);
    }

    @Test
    public void shouldUpdateUpdatedSpeakersCall() {
        SpeakersCall updated = mock(SpeakersCall.class);

        when(repository.isConnected()).thenReturn(true);
        when(speakersCallApi.updateSpeakersCall(ID, SPEAKERS_CALL)).thenReturn(Observable.just(updated));
        when(repository.update(eq(SpeakersCall.class), eq(updated))).thenReturn(Completable.complete());

        speakersCallRepository.updateSpeakersCall(SPEAKERS_CALL).subscribe();

        verify(repository).update(SpeakersCall.class, updated);
    }
}
