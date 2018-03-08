package org.fossasia.openevent.app.unit.presenter;

import org.fossasia.openevent.app.common.app.rx.Logger;
import org.fossasia.openevent.app.common.data.models.Faq;
import org.fossasia.openevent.app.common.data.repository.contract.IFaqRepository;
import org.fossasia.openevent.app.module.faq.list.FaqListPresenter;
import org.fossasia.openevent.app.module.faq.list.contract.IFaqListView;
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

    private FaqListPresenter faqListPresenter;

    private static final long ID = 10L;

    private static final List<Faq> FAQS = Arrays.asList(
        Faq.builder().id(2L).question("q").answer("a").build(),
        Faq.builder().id(3L).question("qu").answer("an").build(),
        Faq.builder().question("que").answer("ans").build()
    );

    @Before
    public void setUp() {
        faqListPresenter = new FaqListPresenter(faqRepository);
        faqListPresenter.attach(ID, faqListView);
    }

    @Test
    public void shouldLoadFaqListAutomatically() {
        when(faqRepository.getFaqs(anyLong(), anyBoolean())).thenReturn(Observable.fromIterable(FAQS));

        faqListPresenter.start();

        verify(faqRepository).getFaqs(ID, false);
    }

    @Test
    public void shouldShowFaqListAutomatically() {
        when(faqRepository.getFaqs(ID, false)).thenReturn(Observable.fromIterable(FAQS));

        faqListPresenter.start();

        verify(faqListView).showResults(FAQS);
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
