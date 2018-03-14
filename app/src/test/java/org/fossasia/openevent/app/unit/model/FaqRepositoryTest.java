package org.fossasia.openevent.app.unit.model;

import com.raizlabs.android.dbflow.sql.language.SQLOperator;

import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.common.data.contract.IUtilModel;
import org.fossasia.openevent.app.common.data.db.contract.IDatabaseRepository;
import org.fossasia.openevent.app.common.data.models.Event;
import org.fossasia.openevent.app.common.data.models.Faq;
import org.fossasia.openevent.app.common.data.network.EventService;
import org.fossasia.openevent.app.common.data.repository.FaqRepository;
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

    private FaqRepository faqRepository;
    private static final Faq FAQ = new Faq();
    private static final Event EVENT = new Event();
    private static final long ID = 10L;

    @Mock
    private EventService eventService;
    @Mock
    private IUtilModel utilModel;
    @Mock
    private IDatabaseRepository databaseRepository;

    static {
        FAQ.setEvent(EVENT);
    }

    @Before
    public void setUp() {
        faqRepository = new FaqRepository(utilModel, databaseRepository, eventService);
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
        when(utilModel.isConnected()).thenReturn(false);

        Observable<Faq> faqObservable = faqRepository.getFaqs(ID, true);

        faqObservable
            .test()
            .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    @Test
    public void shouldReturnConnectionErrorOnGetFaqsWithNoneSaved() {
        when(utilModel.isConnected()).thenReturn(false);
        when(databaseRepository.getItems(eq(Faq.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        Observable<Faq> faqObservable = faqRepository.getFaqs(ID, false);

        faqObservable
            .test()
            .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    @Test
    public void shouldCallGetFaqsServiceOnReload() {
        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.getFaqs(ID)).thenReturn(Observable.empty());
        when(databaseRepository.getItems(eq(Faq.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        faqRepository.getFaqs(ID, true).subscribe();

        verify(eventService).getFaqs(ID);
    }

    @Test
    public void shouldCallGetFaqsServiceWithNoneSaved() {
        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.getFaqs(ID)).thenReturn(Observable.empty());
        when(databaseRepository.getItems(eq(Faq.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        faqRepository.getFaqs(ID, false).subscribe();

        verify(eventService).getFaqs(ID);
    }

    @Test
    public void shouldDeleteFaqsOnGet() {
        List<Faq> faqs = new ArrayList<>();
        faqs.add(FAQ);

        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.getFaqs(ID)).thenReturn(Observable.just(faqs));
        when(databaseRepository.deleteAll(Faq.class)).thenReturn(Completable.complete());
        when(databaseRepository.saveList(Faq.class, faqs)).thenReturn(Completable.complete());
        when(databaseRepository.getItems(eq(Faq.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        faqRepository.getFaqs(ID, true).subscribe();

        verify(databaseRepository).deleteAll(Faq.class);
    }

    @Test
    public void shouldSaveFaqsOnGet() {
        List<Faq> faqs = new ArrayList<>();
        faqs.add(FAQ);

        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.getFaqs(ID)).thenReturn(Observable.just(faqs));
        when(databaseRepository.deleteAll(Faq.class)).thenReturn(Completable.complete());
        when(databaseRepository.saveList(Faq.class, faqs)).thenReturn(Completable.complete());
        when(databaseRepository.getItems(eq(Faq.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        faqRepository.getFaqs(ID, true).subscribe();

        verify(databaseRepository).saveList(Faq.class, faqs);
    }
}
