package com.eventyay.organizer.data.repository;

import com.raizlabs.android.dbflow.sql.language.SQLOperator;

import com.eventyay.organizer.common.Constants;
import com.eventyay.organizer.data.AbstractObservable;
import com.eventyay.organizer.data.Repository;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.faq.Faq;
import com.eventyay.organizer.data.faq.FaqApi;
import com.eventyay.organizer.data.faq.FaqRepositoryImpl;
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

public class FaqRepositoryTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private FaqRepositoryImpl faqRepository;
    private static final Faq FAQ = new Faq();
    private static final Event EVENT = new Event();
    private static final long ID = 10L;

    @Mock private FaqApi faqApi;
    @Mock private Repository repository;

    static {
        FAQ.setEvent(EVENT);
    }

    @Before
    public void setUp() {
        when(repository.observableOf(Faq.class)).thenReturn(new AbstractObservable.AbstractObservableBuilder<>(repository));
        faqRepository = new FaqRepositoryImpl(faqApi, repository);
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldReturnConnectionErrorOnGetFaqsWithReload() {
        when(repository.isConnected()).thenReturn(false);

        Observable<Faq> faqObservable = faqRepository.getFaqs(ID, true);

        faqObservable
            .test()
            .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    @Test
    public void shouldReturnConnectionErrorOnGetFaqsWithNoneSaved() {
        when(repository.isConnected()).thenReturn(false);
        when(repository.getItems(eq(Faq.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        Observable<Faq> faqObservable = faqRepository.getFaqs(ID, false);

        faqObservable
            .test()
            .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    @Test
    public void shouldCallGetFaqsServiceOnReload() {
        when(repository.isConnected()).thenReturn(true);
        when(faqApi.getFaqs(ID)).thenReturn(Observable.empty());
        when(repository.getItems(eq(Faq.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        faqRepository.getFaqs(ID, true).subscribe();

        verify(faqApi).getFaqs(ID);
    }

    @Test
    public void shouldCallGetFaqsServiceWithNoneSaved() {
        when(repository.isConnected()).thenReturn(true);
        when(faqApi.getFaqs(ID)).thenReturn(Observable.empty());
        when(repository.getItems(eq(Faq.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        faqRepository.getFaqs(ID, false).subscribe();

        verify(faqApi).getFaqs(ID);
    }

    @Test
    public void shouldSaveFaqsOnGet() {
        List<Faq> faqs = new ArrayList<>();
        faqs.add(FAQ);

        when(repository.isConnected()).thenReturn(true);
        when(faqApi.getFaqs(ID)).thenReturn(Observable.just(faqs));
        when(repository.syncSave(eq(Faq.class), eq(faqs), any(), any())).thenReturn(Completable.complete());
        when(repository.getItems(eq(Faq.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        faqRepository.getFaqs(ID, true).subscribe();

        verify(repository).syncSave(eq(Faq.class), eq(faqs), any(), any());
    }
}
