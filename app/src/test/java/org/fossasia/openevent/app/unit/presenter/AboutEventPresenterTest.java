package org.fossasia.openevent.app.unit.presenter;

import org.fossasia.openevent.app.common.data.db.contract.IDatabaseChangeListener;
import org.fossasia.openevent.app.common.data.models.Copyright;
import org.fossasia.openevent.app.common.data.models.Event;
import org.fossasia.openevent.app.common.data.repository.contract.ICopyrightRepository;
import org.fossasia.openevent.app.common.data.repository.contract.IEventRepository;
import org.fossasia.openevent.app.module.event.about.AboutEventPresenter;
import org.fossasia.openevent.app.module.event.about.contract.IAboutEventVew;
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
import io.reactivex.subjects.PublishSubject;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class AboutEventPresenterTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock private IAboutEventVew aboutEventVew;
    @Mock private IEventRepository eventRepository;
    @Mock private ICopyrightRepository copyrightRepository;
    @Mock private IDatabaseChangeListener<Copyright> copyrightChangeListener;

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

        verify(aboutEventVew).changeCopyrightMenuItem();
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
}
