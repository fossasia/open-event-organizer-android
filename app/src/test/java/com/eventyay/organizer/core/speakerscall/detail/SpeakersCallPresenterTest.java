package com.eventyay.organizer.core.speakerscall.detail;

import com.eventyay.organizer.data.db.DatabaseChangeListener;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.speakerscall.SpeakersCall;
import com.eventyay.organizer.data.speakerscall.SpeakersCallRepositoryImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SpeakersCallPresenterTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private SpeakersCallView speakersCallView;
    @Mock
    private SpeakersCallRepositoryImpl speakersCallRepository;
    @Mock
    private DatabaseChangeListener<SpeakersCall> databaseChangeListener;

    private SpeakersCallPresenter speakersCallPresenter;

    private static final long ID = 5L;

    private static final SpeakersCall SPEAKERS_CALL = new SpeakersCall();

    static {
        SPEAKERS_CALL.setId(ID);
        SPEAKERS_CALL.setEvent(Event.builder().id(ID).build());
    }

    @Before
    public void setUp() {
        speakersCallPresenter = new SpeakersCallPresenter(speakersCallRepository, databaseChangeListener);
        speakersCallPresenter.attach(ID, speakersCallView);

        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldActivateChangeListenerOnStart() {
        when(speakersCallRepository.getSpeakersCall(anyLong(), anyBoolean())).thenReturn(Observable.just(SPEAKERS_CALL));
        when(databaseChangeListener.getNotifier()).thenReturn(PublishSubject.create());

        speakersCallPresenter.start();

        verify(databaseChangeListener).startListening();
    }

    @Test
    public void shouldLoadSpeakersCallSuccessfully() {
        when(speakersCallRepository.getSpeakersCall(ID, false)).thenReturn(Observable.just(SPEAKERS_CALL));
        when(databaseChangeListener.getNotifier()).thenReturn(PublishSubject.create());

        speakersCallPresenter.start();

        verify(speakersCallView).showResult(SPEAKERS_CALL);
    }

    @Test
    public void shouldShowErrorOnSpeakersCallLoadFailure() {
        when(speakersCallRepository.getSpeakersCall(ID, false)).thenReturn(Observable.error(new Throwable("Error")));
        when(databaseChangeListener.getNotifier()).thenReturn(PublishSubject.create());

        speakersCallPresenter.start();

        verify(speakersCallView).showError("Error");
    }
}
