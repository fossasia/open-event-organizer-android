package org.fossasia.openevent.app.unit.model;

import com.raizlabs.android.dbflow.sql.language.SQLOperator;

import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.data.IUtilModel;
import org.fossasia.openevent.app.data.db.IDatabaseRepository;
import org.fossasia.openevent.app.data.models.Copyright;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.network.EventService;
import org.fossasia.openevent.app.data.repository.CopyrightRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CopyrightRepositoryTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private CopyrightRepository copyrightRepository;
    private static final Copyright COPYRIGHT = new Copyright();
    private static final Event EVENT = new Event();
    private static final long ID = 10L;

    @Mock
    private EventService eventService;
    @Mock
    private IUtilModel utilModel;
    @Mock
    private IDatabaseRepository databaseRepository;

    static {
        COPYRIGHT.setEvent(EVENT);
        COPYRIGHT.setId(ID);
    }

    @Before
    public void setUp() {
        copyrightRepository = new CopyrightRepository(utilModel, databaseRepository, eventService);
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    // Network down tests

    @Test
    public void shouldReturnConnectionErrorOnCreateCopyright() {
        when(utilModel.isConnected()).thenReturn(false);

        Observable<Copyright> copyrightObservable = copyrightRepository.createCopyright(COPYRIGHT);

        copyrightObservable
            .test()
            .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    @Test
    public void shouldReturnConnectionErrorOnGetCopyrightWithReload() {
        when(utilModel.isConnected()).thenReturn(false);

        Observable<Copyright> copyrightObservable = copyrightRepository.getCopyright(ID, true);

        copyrightObservable
            .test()
            .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    @Test
    public void shouldReturnConnectionErrorOnGetCopyrightWithNoneSaved() {
        when(utilModel.isConnected()).thenReturn(false);
        when(databaseRepository.getItems(eq(Copyright.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        Observable<Copyright> copyrightObservable = copyrightRepository.getCopyright(ID, false);

        copyrightObservable
            .test()
            .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    @Test
    public void shouldReturnConnectionErrorOnUpdateCopyright() {
        when(utilModel.isConnected()).thenReturn(false);

        Observable<Copyright> copyrightObservable = copyrightRepository.updateCopyright(COPYRIGHT);

        copyrightObservable
            .test()
            .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    @Test
    public void shouldReturnConnectionErrorOnDeleteCopyright() {
        when(utilModel.isConnected()).thenReturn(false);

        Completable copyrightCompletable = copyrightRepository.deleteCopyright(ID);

        copyrightCompletable
            .test()
            .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    // Network up tests

    // Create copyright tests

    @Test
    public void shouldCallCreateCopyrightService() {
        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.postCopyright(COPYRIGHT)).thenReturn(Observable.empty());

        copyrightRepository.createCopyright(COPYRIGHT).subscribe();

        verify(eventService).postCopyright(COPYRIGHT);
    }

    @Test
    public void shouldSetEventOnCreatedCopyright() {
        Copyright created = mock(Copyright.class);

        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.postCopyright(COPYRIGHT)).thenReturn(Observable.just(created));
        when(databaseRepository.save(eq(Copyright.class), eq(created))).thenReturn(Completable.complete());

        copyrightRepository.createCopyright(COPYRIGHT).subscribe();

        verify(created).setEvent(EVENT);
    }

    @Test
    public void shouldSaveCreatedCopyright() {
        Copyright created = mock(Copyright.class);

        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.postCopyright(COPYRIGHT)).thenReturn(Observable.just(created));
        when(databaseRepository.save(eq(Copyright.class), eq(created))).thenReturn(Completable.complete());

        copyrightRepository.createCopyright(COPYRIGHT).subscribe();

        verify(databaseRepository).save(Copyright.class, created);
    }

    // Copyright get tests

    @Test
    public void shouldCallGetCopyrightsServiceOnReload() {
        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.getCopyright(ID)).thenReturn(Observable.empty());
        when(databaseRepository.getItems(eq(Copyright.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        copyrightRepository.getCopyright(ID, true).subscribe();

        verify(eventService).getCopyright(ID);
    }

    @Test
    public void shouldCallGetCopyrightServiceWithNoneSaved() {
        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.getCopyright(ID)).thenReturn(Observable.empty());
        when(databaseRepository.getItems(eq(Copyright.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        copyrightRepository.getCopyright(ID, false).subscribe();

        verify(eventService).getCopyright(ID);
    }

    @Test
    public void shouldSaveCopyrightOnGet() {
        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.getCopyright(ID)).thenReturn(Observable.just(COPYRIGHT));
        when(databaseRepository.getItems(eq(Copyright.class), any(SQLOperator.class))).thenReturn(Observable.empty());
        when(databaseRepository.save(eq(Copyright.class), eq(COPYRIGHT))).thenReturn(Completable.complete());

        copyrightRepository.getCopyright(ID, true).subscribe();

        verify(databaseRepository).save(Copyright.class, COPYRIGHT);
    }

    // Copyright update tests

    @Test
    public void shouldCallUpdateCopyrightService() {
        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.patchCopyright(ID, COPYRIGHT)).thenReturn(Observable.empty());

        copyrightRepository.updateCopyright(COPYRIGHT).subscribe();

        verify(eventService).patchCopyright(ID, COPYRIGHT);
    }

    @Test
    public void shouldUpdateUpdatedCopyright() {
        Copyright updated = mock(Copyright.class);

        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.patchCopyright(ID, COPYRIGHT)).thenReturn(Observable.just(updated));
        when(databaseRepository.update(eq(Copyright.class), eq(updated))).thenReturn(Completable.complete());

        copyrightRepository.updateCopyright(COPYRIGHT).subscribe();

        verify(databaseRepository).update(Copyright.class, updated);
    }

    // Copyright delete tests

    @Test
    public void shouldCallDeleteCopyrightService() {
        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.deleteCopyright(ID)).thenReturn(Completable.complete());

        copyrightRepository.deleteCopyright(ID).subscribe();

        verify(eventService).deleteCopyright(ID);
    }

}
