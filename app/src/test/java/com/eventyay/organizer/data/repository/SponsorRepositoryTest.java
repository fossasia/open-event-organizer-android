package com.eventyay.organizer.data.repository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.eventyay.organizer.common.Constants;
import com.eventyay.organizer.data.AbstractObservable;
import com.eventyay.organizer.data.Repository;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.sponsor.Sponsor;
import com.eventyay.organizer.data.sponsor.SponsorApi;
import com.eventyay.organizer.data.sponsor.SponsorRepositoryImpl;
import com.raizlabs.android.dbflow.sql.language.SQLOperator;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

@SuppressWarnings("PMD.TooManyMethods")
public class SponsorRepositoryTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    private SponsorRepositoryImpl sponsorRepository;
    private static final Sponsor SPONSOR = new Sponsor();
    private static final Event EVENT = new Event();
    private static final long ID = 10L;

    @Mock private SponsorApi sponsorApi;
    @Mock private Repository repository;

    static {
        SPONSOR.setEvent(EVENT);
        SPONSOR.setId(ID);
    }

    @Before
    public void setUp() {
        when(repository.observableOf(Sponsor.class))
                .thenReturn(new AbstractObservable.AbstractObservableBuilder<>(repository));
        sponsorRepository = new SponsorRepositoryImpl(repository, sponsorApi);
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
    public void shouldReturnConnectionErrorOnCreateSponsor() {
        when(repository.isConnected()).thenReturn(false);

        Observable<Sponsor> sponsorObservable = sponsorRepository.createSponsor(SPONSOR);

        sponsorObservable
                .test()
                .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    @Test
    public void shouldReturnConnectionErrorDeleteSponsor() {
        when(repository.isConnected()).thenReturn(false);

        Completable sponsorObservable = sponsorRepository.deleteSponsor(ID);

        sponsorObservable
                .test()
                .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    @Test
    public void shouldReturnConnectionErrorOnGetSponsorWithReload() {
        when(repository.isConnected()).thenReturn(false);

        Observable<Sponsor> sponsorObservable = sponsorRepository.getSponsor(ID, true);

        sponsorObservable
                .test()
                .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    @Test
    public void shouldReturnConnectionErrorOnGetSponsorWithNoneSaved() {
        when(repository.isConnected()).thenReturn(false);
        when(repository.getItems(eq(Sponsor.class), any(SQLOperator.class)))
                .thenReturn(Observable.empty());

        Observable<Sponsor> sponsorObservable = sponsorRepository.getSponsor(ID, false);

        sponsorObservable
                .test()
                .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    @Test
    public void shouldReturnConnectionErrorOnGetSponsorsWithReload() {
        when(repository.isConnected()).thenReturn(false);

        Observable<Sponsor> sponsorObservable = sponsorRepository.getSponsors(ID, true);

        sponsorObservable
                .test()
                .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    @Test
    public void shouldReturnConnectionErrorOnGetSponsorsWithNoneSaved() {
        when(repository.isConnected()).thenReturn(false);
        when(repository.getItems(eq(Sponsor.class), any(SQLOperator.class)))
                .thenReturn(Observable.empty());

        Observable<Sponsor> sponsorObservable = sponsorRepository.getSponsors(ID, false);

        sponsorObservable
                .test()
                .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    // Network up tests

    // Sponsor Create Tests

    @Test
    public void shouldCallCreateSponsorService() {
        when(repository.isConnected()).thenReturn(true);
        when(sponsorApi.postSponsor(SPONSOR)).thenReturn(Observable.empty());

        sponsorRepository.createSponsor(SPONSOR).subscribe();

        verify(sponsorApi).postSponsor(SPONSOR);
    }

    @Test
    public void shouldSetEventOnCreatedSponsor() {
        Sponsor created = mock(Sponsor.class);

        when(repository.isConnected()).thenReturn(true);
        when(sponsorApi.postSponsor(SPONSOR)).thenReturn(Observable.just(created));
        when(repository.save(eq(Sponsor.class), eq(created))).thenReturn(Completable.complete());

        sponsorRepository.createSponsor(SPONSOR).subscribe();

        verify(created).setEvent(EVENT);
    }

    @Test
    public void shouldSaveCreatedSponsor() {
        Sponsor created = mock(Sponsor.class);

        when(repository.isConnected()).thenReturn(true);
        when(sponsorApi.postSponsor(SPONSOR)).thenReturn(Observable.just(created));
        when(repository.save(eq(Sponsor.class), eq(created))).thenReturn(Completable.complete());

        sponsorRepository.createSponsor(SPONSOR).subscribe();

        verify(repository).save(Sponsor.class, created);
    }

    // Sponsor Get Tests

    @Test
    public void shouldCallGetSponsorServiceOnReload() {
        when(repository.isConnected()).thenReturn(true);
        when(sponsorApi.getSponsor(ID)).thenReturn(Observable.empty());
        when(repository.getItems(eq(Sponsor.class), any(SQLOperator.class)))
                .thenReturn(Observable.empty());

        sponsorRepository.getSponsor(ID, true).subscribe();

        verify(sponsorApi).getSponsor(ID);
    }

    @Test
    public void shouldCallGetSponsorServiceWithNoneSaved() {
        when(repository.isConnected()).thenReturn(true);
        when(sponsorApi.getSponsor(ID)).thenReturn(Observable.empty());
        when(repository.getItems(eq(Sponsor.class), any(SQLOperator.class)))
                .thenReturn(Observable.empty());

        sponsorRepository.getSponsor(ID, false).subscribe();

        verify(sponsorApi).getSponsor(ID);
    }

    @Test
    public void shouldSaveSponsorOnGet() {
        when(repository.isConnected()).thenReturn(true);
        when(sponsorApi.getSponsor(ID)).thenReturn(Observable.just(SPONSOR));
        when(repository.save(eq(Sponsor.class), eq(SPONSOR))).thenReturn(Completable.complete());
        when(repository.getItems(eq(Sponsor.class), any(SQLOperator.class)))
                .thenReturn(Observable.empty());

        sponsorRepository.getSponsor(ID, true).subscribe();

        verify(repository).save(Sponsor.class, SPONSOR);
    }

    // Sponsors Get Tests

    @Test
    public void shouldCallGetSponsorsServiceOnReload() {
        when(repository.isConnected()).thenReturn(true);
        when(sponsorApi.getSponsors(ID)).thenReturn(Observable.empty());
        when(repository.getItems(eq(Sponsor.class), any(SQLOperator.class)))
                .thenReturn(Observable.empty());

        sponsorRepository.getSponsors(ID, true).subscribe();

        verify(sponsorApi).getSponsors(ID);
    }

    @Test
    public void shouldCallGetSponsorsServiceWithNoneSaved() {
        when(repository.isConnected()).thenReturn(true);
        when(sponsorApi.getSponsors(ID)).thenReturn(Observable.empty());
        when(repository.getItems(eq(Sponsor.class), any(SQLOperator.class)))
                .thenReturn(Observable.empty());

        sponsorRepository.getSponsors(ID, false).subscribe();

        verify(sponsorApi).getSponsors(ID);
    }

    @Test
    public void shouldSaveSponsorsOnGet() {
        List<Sponsor> sponsors = new ArrayList<>();
        sponsors.add(SPONSOR);

        when(repository.isConnected()).thenReturn(true);
        when(sponsorApi.getSponsors(ID)).thenReturn(Observable.just(sponsors));
        when(repository.syncSave(eq(Sponsor.class), eq(sponsors), any(), any()))
                .thenReturn(Completable.complete());
        when(repository.getItems(eq(Sponsor.class), any(SQLOperator.class)))
                .thenReturn(Observable.empty());

        sponsorRepository.getSponsors(ID, true).subscribe();

        verify(repository).syncSave(eq(Sponsor.class), eq(sponsors), any(), any());
    }

    // Sponsor update tests

    @Test
    public void shouldCallUpdateSponsorService() {
        when(repository.isConnected()).thenReturn(true);
        when(sponsorApi.updateSponsor(ID, SPONSOR)).thenReturn(Observable.empty());

        sponsorRepository.updateSponsor(SPONSOR).subscribe();

        verify(sponsorApi).updateSponsor(ID, SPONSOR);
    }

    @Test
    public void shouldUpdateUpdatedSponsor() {
        Sponsor updated = mock(Sponsor.class);

        when(repository.isConnected()).thenReturn(true);
        when(sponsorApi.updateSponsor(ID, SPONSOR)).thenReturn(Observable.just(updated));
        when(repository.update(eq(Sponsor.class), eq(updated))).thenReturn(Completable.complete());

        sponsorRepository.updateSponsor(SPONSOR).subscribe();

        verify(repository).update(Sponsor.class, updated);
    }

    // Sponsor delete tests

    @Test
    public void shouldCallDeleteSponsorService() {
        when(repository.isConnected()).thenReturn(true);
        when(sponsorApi.deleteSponsor(ID)).thenReturn(Completable.complete());

        sponsorRepository.deleteSponsor(ID).subscribe();

        verify(sponsorApi).deleteSponsor(ID);
    }
}
