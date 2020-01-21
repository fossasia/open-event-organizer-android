package com.eventyay.organizer.core.sponsor.list;

import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.db.DatabaseChangeListener;
import com.eventyay.organizer.data.sponsor.Sponsor;
import com.eventyay.organizer.data.sponsor.SponsorRepository;

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

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
@SuppressWarnings({"PMD.CommentSize", "PMD.LineTooLong"})
public class SponsorsPresenterTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private SponsorsView sponsorsView;
    @Mock
    private SponsorRepository sponsorRepository;
    @Mock
    private DatabaseChangeListener<Sponsor> databaseChangeListener;

    private static final long ID = 42;

    private static final List<Sponsor> SPONSORS = Arrays.asList(
        Sponsor.builder().id(2L).name("xyz").build(),
        Sponsor.builder().id(3L).name("abc").build(),
        Sponsor.builder().id(4L).name("pqr").build()
    );

    private static final String SPONSORS_DELETED_SUCCESSFULLY = "Sponsors Deleted";

    private SponsorsPresenter sponsorsPresenter;

    @Before
    public void setUp() {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());

        sponsorsPresenter = new SponsorsPresenter(sponsorRepository, databaseChangeListener);
        sponsorsPresenter.attach(ID, sponsorsView);
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldLoadSponsorsAutomatically() {
        when(sponsorRepository.getSponsors(anyLong(), anyBoolean())).thenReturn(Observable.fromIterable(SPONSORS));
        when(databaseChangeListener.getNotifier()).thenReturn(PublishSubject.create());

        sponsorsPresenter.start();

        verify(sponsorRepository).getSponsors(ID, false);
    }

    @Test
    public void shouldShowSponsorsAutomatically() {
        when(sponsorRepository.getSponsors(ID, false)).thenReturn(Observable.fromIterable(SPONSORS));
        when(databaseChangeListener.getNotifier()).thenReturn(PublishSubject.create());

        sponsorsPresenter.start();

        verify(sponsorsView).showResults(SPONSORS);
    }

    @Test
    public void shouldActivateChangeListenerOnStart() {
        when(sponsorRepository.getSponsors(anyLong(), anyBoolean())).thenReturn(Observable.fromIterable(SPONSORS));
        when(databaseChangeListener.getNotifier()).thenReturn(PublishSubject.create());

        sponsorsPresenter.start();

        verify(databaseChangeListener).startListening();
    }

    @Test
    public void shouldDisableChangeListenerOnDetach() {
        sponsorsPresenter.detach();

        verify(databaseChangeListener).stopListening();
    }

    @Test
    public void shouldShowEmptyViewOnNoSponsors() {
        when(sponsorRepository.getSponsors(anyLong(), anyBoolean())).thenReturn(Observable.fromIterable(new ArrayList<>()));

        sponsorsPresenter.loadSponsors(true);

        verify(sponsorsView
        ).showEmptyView(true);
    }

    @Test
    public void shouldShowSponsorsOnSwipeRefreshSuccess() {
        when(sponsorRepository.getSponsors(ID, true)).thenReturn(Observable.fromIterable(SPONSORS));

        sponsorsPresenter.loadSponsors(true);

        verify(sponsorsView).showResults(any());
    }

    @Test
    public void shouldShowErrorMessageOnSwipeRefreshError() {
        when(sponsorRepository.getSponsors(ID, true)).thenReturn(Observable.error(Logger.TEST_ERROR));

        sponsorsPresenter.loadSponsors(true);

        verify(sponsorsView).showError(Logger.TEST_ERROR.getMessage());
    }

    @Test
    public void testProgressbarOnSwipeRefreshSuccess() {
        when(sponsorRepository.getSponsors(ID, true)).thenReturn(Observable.fromIterable(SPONSORS));

        sponsorsPresenter.loadSponsors(true);

        InOrder inOrder = Mockito.inOrder(sponsorsView);

        inOrder.verify(sponsorsView).showProgress(true);
        inOrder.verify(sponsorsView).onRefreshComplete(true);
        inOrder.verify(sponsorsView).showProgress(false);
    }

    @Test
    public void testProgressbarOnSwipeRefreshError() {
        when(sponsorRepository.getSponsors(ID, true)).thenReturn(Observable.error(Logger.TEST_ERROR));

        sponsorsPresenter.loadSponsors(true);

        InOrder inOrder = Mockito.inOrder(sponsorsView);

        inOrder.verify(sponsorsView).showProgress(true);
        inOrder.verify(sponsorsView).onRefreshComplete(false);
        inOrder.verify(sponsorsView).showProgress(false);
    }

    @Test
    public void testProgressbarOnSwipeRefreshNoItem() {
        List<Sponsor> emptyList = new ArrayList<>();
        when(sponsorRepository.getSponsors(ID, true)).thenReturn(Observable.fromIterable(emptyList));

        sponsorsPresenter.loadSponsors(true);

        InOrder inOrder = Mockito.inOrder(sponsorsView);

        inOrder.verify(sponsorsView).showProgress(true);
        inOrder.verify(sponsorsView).onRefreshComplete(true);
        inOrder.verify(sponsorsView).showProgress(false);
    }

    @Test
    public void shouldDeleteSponsorSuccessfully() {
        when(sponsorRepository.deleteSponsor(ID)).thenReturn(Completable.complete());

        sponsorsPresenter.isSponsorSelected(ID);
        sponsorsPresenter.getSelectedSponsors().get(ID).set(true);
        sponsorsPresenter.deleteSponsor(ID);

        assertFalse(sponsorsPresenter.isSponsorSelected(ID).get());
    }

    @Test
    public void shouldDeleteSponsorsSuccessfully() {
        for (Sponsor sponsor : sponsorsPresenter.getSponsors()) {
            sponsorsPresenter.getSelectedSponsors().get(sponsor.getId()).set(true);
        }

        for (Long sponsorId : sponsorsPresenter.getSelectedSponsors().keySet()) {
            when(sponsorRepository.deleteSponsor(sponsorId)).thenReturn(Completable.complete());
        }

        sponsorsPresenter.deleteSelectedSponsors();

        InOrder inOrder = Mockito.inOrder(sponsorsView);

        inOrder.verify(sponsorsView).showProgress(true);
        inOrder.verify(sponsorsView).showMessage(SPONSORS_DELETED_SUCCESSFULLY);
        inOrder.verify(sponsorsView).showProgress(false);

        assertEquals(sponsorsPresenter.getSelectedSponsors().size(), 0);
    }

    @Test
    public void shouldNotDeleteUnselectedSponsors() {
        for (Long sponsorId : sponsorsPresenter.getSelectedSponsors().keySet()) {
            when(sponsorRepository.deleteSponsor(sponsorId)).thenReturn(Completable.error(Logger.TEST_ERROR));
        }

        sponsorsPresenter.deleteSelectedSponsors();

        verify(sponsorRepository, Mockito.never()).deleteSponsor(anyLong());
    }

    @Test
    public void shouldResetToolbarToDefaultState() {
        sponsorsPresenter.resetToolbarToDefaultState();

        verify(sponsorsView).exitContextualMenuMode();
        verify(sponsorsView).changeToolbarMode(false, false);
    }
}
