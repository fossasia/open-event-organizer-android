package com.eventyay.organizer.data.repository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.eventyay.organizer.common.Constants;
import com.eventyay.organizer.data.AbstractObservable;
import com.eventyay.organizer.data.Repository;
import com.eventyay.organizer.data.copyright.Copyright;
import com.eventyay.organizer.data.copyright.CopyrightApi;
import com.eventyay.organizer.data.copyright.CopyrightRepositoryImpl;
import com.eventyay.organizer.data.event.Event;
import com.raizlabs.android.dbflow.sql.language.SQLOperator;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

public class CopyrightRepositoryTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    private CopyrightRepositoryImpl copyrightRepository;
    private static final Copyright COPYRIGHT = new Copyright();
    private static final Event EVENT = new Event();
    private static final long ID = 10L;

    @Mock private CopyrightApi copyrightApi;
    @Mock private Repository repository;

    static {
        COPYRIGHT.setEvent(EVENT);
        COPYRIGHT.setId(ID);
    }

    @Before
    public void setUp() {
        when(repository.observableOf(Copyright.class))
                .thenReturn(new AbstractObservable.AbstractObservableBuilder<>(repository));
        copyrightRepository = new CopyrightRepositoryImpl(repository, copyrightApi);
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(
                schedulerCallable -> Schedulers.trampoline());
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    // Network down tests

    @Test
    public void shouldReturnConnectionErrorOnCreateCopyright() {
        when(repository.isConnected()).thenReturn(false);

        Observable<Copyright> copyrightObservable = copyrightRepository.createCopyright(COPYRIGHT);

        copyrightObservable
                .test()
                .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    @Test
    public void shouldReturnConnectionErrorOnGetCopyrightWithReload() {
        when(repository.isConnected()).thenReturn(false);

        Observable<Copyright> copyrightObservable = copyrightRepository.getCopyright(ID, true);

        copyrightObservable
                .test()
                .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    @Test
    public void shouldReturnConnectionErrorOnGetCopyrightWithNoneSaved() {
        when(repository.isConnected()).thenReturn(false);
        when(repository.getItems(eq(Copyright.class), any(SQLOperator.class)))
                .thenReturn(Observable.empty());

        Observable<Copyright> copyrightObservable = copyrightRepository.getCopyright(ID, false);

        copyrightObservable
                .test()
                .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    @Test
    public void shouldReturnConnectionErrorOnUpdateCopyright() {
        when(repository.isConnected()).thenReturn(false);

        Observable<Copyright> copyrightObservable = copyrightRepository.updateCopyright(COPYRIGHT);

        copyrightObservable
                .test()
                .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    @Test
    public void shouldReturnConnectionErrorOnDeleteCopyright() {
        when(repository.isConnected()).thenReturn(false);

        Completable copyrightCompletable = copyrightRepository.deleteCopyright(ID);

        copyrightCompletable
                .test()
                .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    // Network up tests

    // Create copyright tests

    @Test
    public void shouldCallCreateCopyrightService() {
        when(repository.isConnected()).thenReturn(true);
        when(copyrightApi.postCopyright(COPYRIGHT)).thenReturn(Observable.empty());

        copyrightRepository.createCopyright(COPYRIGHT).subscribe();

        verify(copyrightApi).postCopyright(COPYRIGHT);
    }

    @Test
    public void shouldSetEventOnCreatedCopyright() {
        Copyright created = mock(Copyright.class);

        when(repository.isConnected()).thenReturn(true);
        when(copyrightApi.postCopyright(COPYRIGHT)).thenReturn(Observable.just(created));
        when(repository.save(eq(Copyright.class), eq(created))).thenReturn(Completable.complete());

        copyrightRepository.createCopyright(COPYRIGHT).subscribe();

        verify(created).setEvent(EVENT);
    }

    @Test
    public void shouldSaveCreatedCopyright() {
        Copyright created = mock(Copyright.class);

        when(repository.isConnected()).thenReturn(true);
        when(copyrightApi.postCopyright(COPYRIGHT)).thenReturn(Observable.just(created));
        when(repository.save(eq(Copyright.class), eq(created))).thenReturn(Completable.complete());

        copyrightRepository.createCopyright(COPYRIGHT).subscribe();

        verify(repository).save(Copyright.class, created);
    }

    // Copyright get tests

    @Test
    public void shouldCallGetCopyrightsServiceOnReload() {
        when(repository.isConnected()).thenReturn(true);
        when(copyrightApi.getCopyright(ID)).thenReturn(Observable.empty());
        when(repository.getItems(eq(Copyright.class), any(SQLOperator.class)))
                .thenReturn(Observable.empty());

        copyrightRepository.getCopyright(ID, true).subscribe();

        verify(copyrightApi).getCopyright(ID);
    }

    @Test
    public void shouldCallGetCopyrightServiceWithNoneSaved() {
        when(repository.isConnected()).thenReturn(true);
        when(copyrightApi.getCopyright(ID)).thenReturn(Observable.empty());
        when(repository.getItems(eq(Copyright.class), any(SQLOperator.class)))
                .thenReturn(Observable.empty());

        copyrightRepository.getCopyright(ID, false).subscribe();

        verify(copyrightApi).getCopyright(ID);
    }

    @Test
    public void shouldSaveCopyrightOnGet() {
        when(repository.isConnected()).thenReturn(true);
        when(copyrightApi.getCopyright(ID)).thenReturn(Observable.just(COPYRIGHT));
        when(repository.getItems(eq(Copyright.class), any(SQLOperator.class)))
                .thenReturn(Observable.empty());
        when(repository.save(eq(Copyright.class), eq(COPYRIGHT)))
                .thenReturn(Completable.complete());

        copyrightRepository.getCopyright(ID, true).subscribe();

        verify(repository).save(Copyright.class, COPYRIGHT);
    }

    // Copyright update tests

    @Test
    public void shouldCallUpdateCopyrightService() {
        when(repository.isConnected()).thenReturn(true);
        when(copyrightApi.patchCopyright(ID, COPYRIGHT)).thenReturn(Observable.empty());

        copyrightRepository.updateCopyright(COPYRIGHT).subscribe();

        verify(copyrightApi).patchCopyright(ID, COPYRIGHT);
    }

    @Test
    public void shouldUpdateUpdatedCopyright() {
        Copyright updated = mock(Copyright.class);

        when(repository.isConnected()).thenReturn(true);
        when(copyrightApi.patchCopyright(ID, COPYRIGHT)).thenReturn(Observable.just(updated));
        when(repository.update(eq(Copyright.class), eq(updated)))
                .thenReturn(Completable.complete());

        copyrightRepository.updateCopyright(COPYRIGHT).subscribe();

        verify(repository).update(Copyright.class, updated);
    }

    // Copyright delete tests

    @Test
    public void shouldCallDeleteCopyrightService() {
        when(repository.isConnected()).thenReturn(true);
        when(copyrightApi.deleteCopyright(ID)).thenReturn(Completable.complete());

        copyrightRepository.deleteCopyright(ID).subscribe();

        verify(copyrightApi).deleteCopyright(ID);
    }
}
