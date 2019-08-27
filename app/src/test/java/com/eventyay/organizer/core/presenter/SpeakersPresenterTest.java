package com.eventyay.organizer.core.presenter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.core.speaker.list.SpeakersPresenter;
import com.eventyay.organizer.core.speaker.list.SpeakersView;
import com.eventyay.organizer.data.db.DatabaseChangeListener;
import com.eventyay.organizer.data.speaker.Speaker;
import com.eventyay.organizer.data.speaker.SpeakerRepository;
import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

@RunWith(JUnit4.class)
public class SpeakersPresenterTest {
    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock private SpeakersView speakersView;
    @Mock private SpeakerRepository speakerRepository;
    @Mock private DatabaseChangeListener<Speaker> databaseChangeListener;

    private static final long ID = 42;

    private static final List<Speaker> SPEAKERS =
            Arrays.asList(
                    Speaker.builder().id(2L).name("xyz").build(),
                    Speaker.builder().id(3L).name("abc").build(),
                    Speaker.builder().id(4L).name("pqr").build());

    private SpeakersPresenter speakersPresenter;

    @Before
    public void setUp() {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(
                schedulerCallable -> Schedulers.trampoline());

        speakersPresenter = new SpeakersPresenter(speakerRepository, databaseChangeListener);
        speakersPresenter.attach(ID, speakersView);
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldLoadSpeakersAutomatically() {
        when(speakerRepository.getSpeakers(anyLong(), anyBoolean()))
                .thenReturn(Observable.fromIterable(SPEAKERS));
        when(databaseChangeListener.getNotifier()).thenReturn(PublishSubject.create());

        speakersPresenter.start();

        verify(speakerRepository).getSpeakers(ID, false);
    }

    @Test
    public void shouldShowSpeakersAutomatically() {
        when(speakerRepository.getSpeakers(anyLong(), anyBoolean()))
                .thenReturn(Observable.fromIterable(SPEAKERS));
        when(databaseChangeListener.getNotifier()).thenReturn(PublishSubject.create());

        speakersPresenter.start();

        verify(speakersView).showResults(SPEAKERS);
    }

    @Test
    public void shouldActivateChangeListenerOnStart() {
        when(speakerRepository.getSpeakers(anyLong(), anyBoolean()))
                .thenReturn(Observable.fromIterable(SPEAKERS));
        when(databaseChangeListener.getNotifier()).thenReturn(PublishSubject.create());

        speakersPresenter.start();

        verify(databaseChangeListener).startListening();
    }

    @Test
    public void shouldDisableChangeListenerOnDetach() {
        speakersPresenter.detach();

        verify(databaseChangeListener).stopListening();
    }

    @Test
    public void shouldShowEmptyViewOnNoSpeakers() {
        when(speakerRepository.getSpeakers(anyLong(), anyBoolean()))
                .thenReturn(Observable.fromIterable(new ArrayList<>()));

        speakersPresenter.loadSpeakers(true);

        verify(speakersView).showEmptyView(true);
    }

    @Test
    public void shouldShowSpeakersOnSwipeRefreshSuccess() {
        when(speakerRepository.getSpeakers(ID, true)).thenReturn(Observable.fromIterable(SPEAKERS));

        speakersPresenter.loadSpeakers(true);

        verify(speakersView).showResults(any());
    }

    @Test
    public void shouldShowErrorMessageOnSwipeRefreshError() {
        when(speakerRepository.getSpeakers(ID, true))
                .thenReturn(Observable.error(Logger.TEST_ERROR));

        speakersPresenter.loadSpeakers(true);

        verify(speakersView).showError(Logger.TEST_ERROR.getMessage());
    }

    @Test
    public void testProgressbarOnSwipeRefreshSuccess() {
        when(speakerRepository.getSpeakers(ID, true)).thenReturn(Observable.fromIterable(SPEAKERS));

        speakersPresenter.loadSpeakers(true);

        InOrder inOrder = Mockito.inOrder(speakersView);

        inOrder.verify(speakersView).showProgress(true);
        inOrder.verify(speakersView).onRefreshComplete(true);
        inOrder.verify(speakersView).showProgress(false);
    }

    @Test
    public void testProgressbarOnSwipeRefreshError() {
        when(speakerRepository.getSpeakers(ID, true))
                .thenReturn(Observable.error(Logger.TEST_ERROR));

        speakersPresenter.loadSpeakers(true);

        InOrder inOrder = Mockito.inOrder(speakersView);

        inOrder.verify(speakersView).showProgress(true);
        inOrder.verify(speakersView).onRefreshComplete(false);
        inOrder.verify(speakersView).showProgress(false);
    }

    @Test
    public void testProgressbarOnSwipeRefreshNoItem() {
        List<Speaker> emptyList = new ArrayList<>();
        when(speakerRepository.getSpeakers(ID, true))
                .thenReturn(Observable.fromIterable(emptyList));

        speakersPresenter.loadSpeakers(true);

        InOrder inOrder = Mockito.inOrder(speakersView);

        inOrder.verify(speakersView).showProgress(true);
        inOrder.verify(speakersView).onRefreshComplete(true);
        inOrder.verify(speakersView).showProgress(false);
    }
}
