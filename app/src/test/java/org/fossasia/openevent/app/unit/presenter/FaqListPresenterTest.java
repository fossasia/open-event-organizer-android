package org.fossasia.openevent.app.unit.presenter;

import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.db.IDatabaseChangeListener;
import org.fossasia.openevent.app.data.models.Faq;
import org.fossasia.openevent.app.data.repository.IFaqRepository;
import org.fossasia.openevent.app.core.faq.list.FaqListPresenter;
import org.fossasia.openevent.app.core.faq.list.IFaqListView;
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
public class FaqListPresenterTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private IFaqListView faqListView;
    @Mock
    private IFaqRepository faqRepository;
    @Mock
    private IDatabaseChangeListener<Faq> databaseChangeListener;

    private FaqListPresenter faqListPresenter;

    private static final long ID = 10L;

    private static final List<Faq> FAQS = Arrays.asList(
        Faq.builder().id(2L).question("q").answer("a").build(),
        Faq.builder().id(3L).question("qu").answer("an").build(),
        Faq.builder().question("que").answer("ans").build()
    );

    @Before
    public void setUp() {
        faqListPresenter = new FaqListPresenter(faqRepository, databaseChangeListener);
        faqListPresenter.attach(ID, faqListView);

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
    public void shouldLoadFaqListAutomatically() {
        when(faqRepository.getFaqs(anyLong(), anyBoolean())).thenReturn(Observable.fromIterable(FAQS));
        when(databaseChangeListener.getNotifier()).thenReturn(PublishSubject.create());

        faqListPresenter.start();

        verify(faqRepository).getFaqs(ID, false);
    }

    @Test
    public void shouldShowFaqListAutomatically() {
        when(faqRepository.getFaqs(ID, false)).thenReturn(Observable.fromIterable(FAQS));
        when(databaseChangeListener.getNotifier()).thenReturn(PublishSubject.create());

        faqListPresenter.start();

        verify(faqListView).showResults(FAQS);
    }

    @Test
    public void shouldActivateChangeListenerOnStart() {
        when(faqRepository.getFaqs(anyLong(), anyBoolean())).thenReturn(Observable.fromIterable(FAQS));
        when(databaseChangeListener.getNotifier()).thenReturn(PublishSubject.create());

        faqListPresenter.start();

        verify(databaseChangeListener).startListening();
    }

    @Test
    public void shouldDisableChangeListenerOnDetach() {
        faqListPresenter.detach();

        verify(databaseChangeListener).stopListening();
    }

    @Test
    public void shouldShowEmptyViewOnNoFaqList() {
        when(faqRepository.getFaqs(anyLong(), anyBoolean())).thenReturn(Observable.fromIterable(new ArrayList<>()));

        faqListPresenter.loadFaqs(true);

        verify(faqListView).showEmptyView(true);
    }

    @Test
    public void shouldShowFaqListOnSwipeRefreshSuccess() {
        when(faqRepository.getFaqs(ID, true)).thenReturn(Observable.fromIterable(FAQS));

        faqListPresenter.loadFaqs(true);

        verify(faqListView).showResults(any());
    }

    @Test
    public void shouldShowErrorMessageOnSwipeRefreshError() {
        when(faqRepository.getFaqs(ID, true)).thenReturn(Observable.error(Logger.TEST_ERROR));

        faqListPresenter.loadFaqs(true);

        verify(faqListView).showError(Logger.TEST_ERROR.getMessage());
    }

    @Test
    public void testProgressbarOnSwipeRefreshSuccess() {
        when(faqRepository.getFaqs(ID, true)).thenReturn(Observable.fromIterable(FAQS));

        faqListPresenter.loadFaqs(true);

        InOrder inOrder = Mockito.inOrder(faqListView);

        inOrder.verify(faqListView).showProgress(true);
        inOrder.verify(faqListView).onRefreshComplete(true);
        inOrder.verify(faqListView).showProgress(false);
    }

    @Test
    public void testProgressbarOnSwipeRefreshError() {
        when(faqRepository.getFaqs(ID, true)).thenReturn(Observable.error(Logger.TEST_ERROR));

        faqListPresenter.loadFaqs(true);

        InOrder inOrder = Mockito.inOrder(faqListView);

        inOrder.verify(faqListView).showProgress(true);
        inOrder.verify(faqListView).onRefreshComplete(false);
        inOrder.verify(faqListView).showProgress(false);
    }

    @Test
    public void testProgressbarOnSwipeRefreshNoItem() {
        List<Faq> emptyList = new ArrayList<>();
        when(faqRepository.getFaqs(ID, true)).thenReturn(Observable.fromIterable(emptyList));

        faqListPresenter.loadFaqs(true);

        InOrder inOrder = Mockito.inOrder(faqListView);

        inOrder.verify(faqListView).showProgress(true);
        inOrder.verify(faqListView).onRefreshComplete(true);
        inOrder.verify(faqListView).showProgress(false);
    }
}
