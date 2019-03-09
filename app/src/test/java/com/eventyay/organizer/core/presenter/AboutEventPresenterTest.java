package com.eventyay.organizer.core.presenter;

import com.eventyay.organizer.core.event.about.AboutEventPresenter;
import com.eventyay.organizer.core.event.about.AboutEventView;
import com.eventyay.organizer.data.db.DatabaseChangeListener;
import com.eventyay.organizer.data.copyright.Copyright;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.copyright.CopyrightRepository;
import com.eventyay.organizer.data.event.EventRepository;
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

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class AboutEventPresenterTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock private AboutEventView aboutEventVew;
    @Mock private EventRepository eventRepository;
    @Mock private CopyrightRepository copyrightRepository;
    @Mock private DatabaseChangeListener<Copyright> copyrightChangeListener;

    private AboutEventPresenter aboutEventPresenter;
    private static final Event EVENT = new Event();
    private static final Copyright COPYRIGHT = new Copyright();
    private static final long ID = 10L;

    @Before
    public void setUp() {
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());

        aboutEventPresenter = new AboutEventPresenter(eventRepository, copyrightRepository, copyrightChangeListener);
        aboutEventPresenter.attach(ID, aboutEventVew);
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldLoadEventAndCopyrightAutomatically() {
        when(eventRepository.getEvent(ID, false)).thenReturn(Observable.just(EVENT));
        when(copyrightRepository.getCopyright(ID, false)).thenReturn(Observable.just(COPYRIGHT));
        when(copyrightChangeListener.getNotifier()).thenReturn(PublishSubject.create());

        aboutEventPresenter.start();

        verify(eventRepository).getEvent(ID, false);
        verify(copyrightRepository).getCopyright(ID, false);
    }

    @Test
    public void shouldDisposeOnDetach() {
        aboutEventPresenter.detach();
        assertTrue(aboutEventPresenter.getDisposable().isDisposed());
    }

    @Test
    public void shouldLoadEventSuccessfully() {
        when(eventRepository.getEvent(ID, false)).thenReturn(Observable.just(EVENT));

        InOrder inOrder = Mockito.inOrder(eventRepository, aboutEventVew);

        aboutEventPresenter.loadEvent(false);

        inOrder.verify(eventRepository).getEvent(ID, false);
        inOrder.verify(aboutEventVew).showProgress(true);
        inOrder.verify(aboutEventVew).showResult(EVENT);
        inOrder.verify(aboutEventVew).showProgress(false);
    }

    @Test
    public void shouldLoadCopyrightSuccessfully() {
        when(copyrightRepository.getCopyright(ID, false)).thenReturn(Observable.just(COPYRIGHT));

        InOrder inOrder = Mockito.inOrder(copyrightRepository, aboutEventVew);

        aboutEventPresenter.loadCopyright(false);

        inOrder.verify(copyrightRepository).getCopyright(ID, false);
        inOrder.verify(aboutEventVew).showProgress(true);
        inOrder.verify(aboutEventVew).showCopyright(COPYRIGHT);
        inOrder.verify(aboutEventVew).showProgress(false);
    }

    @Test
    public void shouldChangeCopyrightMenuTextOnSuccessfulLoad() {
        when(copyrightRepository.getCopyright(ID, false)).thenReturn(Observable.just(COPYRIGHT));

        aboutEventPresenter.loadCopyright(false);

        verify(aboutEventVew).changeCopyrightMenuItem(false);
    }

    @Test
    public void shouldShowErrorOnEventLoadFailure() {
        when(eventRepository.getEvent(ID, false)).thenReturn(Observable.error(new Throwable("Error")));

        aboutEventPresenter.loadEvent(false);

        verify(aboutEventVew).showError("Error");
    }

    @Test
    public void shouldRefreshEventSuccessfully() {
        when(eventRepository.getEvent(ID, true)).thenReturn(Observable.just(EVENT));

        InOrder inOrder = Mockito.inOrder(eventRepository, aboutEventVew);

        aboutEventPresenter.loadEvent(true);

        inOrder.verify(eventRepository).getEvent(ID, true);
        inOrder.verify(aboutEventVew).showProgress(true);
        inOrder.verify(aboutEventVew).showResult(EVENT);
        inOrder.verify(aboutEventVew).onRefreshComplete(true);
        inOrder.verify(aboutEventVew).showProgress(false);
    }

    @Test
    public void shouldRefreshCopyrightSuccessfully() {
        when(copyrightRepository.getCopyright(ID, true)).thenReturn(Observable.just(COPYRIGHT));

        InOrder inOrder = Mockito.inOrder(copyrightRepository, aboutEventVew);

        aboutEventPresenter.loadCopyright(true);

        inOrder.verify(copyrightRepository).getCopyright(ID, true);
        inOrder.verify(aboutEventVew).showProgress(true);
        inOrder.verify(aboutEventVew).onRefreshComplete(true);
        inOrder.verify(aboutEventVew).showCopyright(COPYRIGHT);
        inOrder.verify(aboutEventVew).showProgress(false);
    }

    @Test
    public void shouldActivateCopyrightChangeListenerOnStart() {
        when(eventRepository.getEvent(ID, false)).thenReturn(Observable.just(EVENT));
        when(copyrightRepository.getCopyright(ID, false)).thenReturn(Observable.just(COPYRIGHT));
        when(copyrightChangeListener.getNotifier()).thenReturn(PublishSubject.create());

        aboutEventPresenter.start();

        verify(copyrightChangeListener).startListening();
    }

    @Test
    public void shouldDisableCopyrightChangeListenerOnDetach() {
        aboutEventPresenter.detach();

        verify(copyrightChangeListener).stopListening();
    }

    @Test
    public void shouldDeleteCopyrightSuccessfully() {
        when(copyrightRepository.deleteCopyright(ID)).thenReturn(Completable.complete());
        when(copyrightRepository.getCopyright(ID, true)).thenReturn(Observable.just(COPYRIGHT));

        InOrder inOrder = Mockito.inOrder(copyrightRepository, aboutEventVew);

        aboutEventPresenter.deleteCopyright(ID);

        inOrder.verify(copyrightRepository).deleteCopyright(ID);
        inOrder.verify(aboutEventVew).showProgress(true);
        inOrder.verify(aboutEventVew).showCopyrightDeleted("Copyright Deleted");
        inOrder.verify(aboutEventVew, times(2)).showProgress(false); // delete copyright operation and load copyright operation
    }
}
