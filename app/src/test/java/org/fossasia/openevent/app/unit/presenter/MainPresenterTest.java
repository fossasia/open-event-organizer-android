package org.fossasia.openevent.app.unit.presenter;

import com.f2prateek.rx.preferences2.Preference;
import com.f2prateek.rx.preferences2.RxSharedPreferences;

import org.fossasia.openevent.app.common.app.ContextManager;
import org.fossasia.openevent.app.common.data.contract.IAuthModel;
import org.fossasia.openevent.app.common.data.contract.IBus;
import org.fossasia.openevent.app.common.data.contract.ISharedPreferenceModel;
import org.fossasia.openevent.app.common.data.models.Event;
import org.fossasia.openevent.app.common.data.models.User;
import org.fossasia.openevent.app.common.data.repository.contract.IEventRepository;
import org.fossasia.openevent.app.module.main.MainActivity;
import org.fossasia.openevent.app.module.main.MainPresenter;
import org.fossasia.openevent.app.module.main.contract.IMainView;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MainPresenterTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock private IAuthModel loginModel;
    @Mock private IMainView mainView;
    @Mock private ContextManager contextManager;
    @Mock private IBus bus;
    @Mock private Preference<Boolean> booleanPref;
    @Mock private ISharedPreferenceModel sharedPreferenceModel;
    @Mock private RxSharedPreferences rxSharedPreferences;
    @Mock private IEventRepository eventRepository;

    private static final PublishSubject<Event> PUBLISHER = PublishSubject.create();

    private MainPresenter mainPresenter;

    private static final long EVENT_ID = 2;
    private static final Event EVENT = Event.builder().id(2).paymentCurrency("INR").build();
    private static final User ORGANIZER = User.builder().id(3).build();

    @Before
    public void setUp() {
        mainPresenter = new MainPresenter(sharedPreferenceModel, loginModel, eventRepository, bus, rxSharedPreferences, contextManager);
        mainPresenter.attach(mainView);
    }

    private void mockCommons() {
        when(booleanPref.asObservable()).thenReturn(Observable.just(true));
        when(rxSharedPreferences.getBoolean(anyString())).thenReturn(booleanPref);

        when(bus.getSelectedEvent()).thenReturn(PUBLISHER);

        when(eventRepository.getOrganiser(anyBoolean())).thenReturn(Observable.just(ORGANIZER));
        when(eventRepository.getEvent(anyLong(), anyBoolean())).thenReturn(Observable.just(EVENT));
    }

    @Test
    public void shouldClearSentryContext() {
        when(loginModel.logout()).thenReturn(Completable.complete());

        mainPresenter.logout();

        verify(contextManager).clearOrganiser();
    }

    @Test
    public void shouldInvalidateViewsOnTimezonePrefChange() {
        mockCommons();

        when(sharedPreferenceModel.getLong(anyString(), anyLong())).thenReturn(EVENT_ID);

        mainPresenter.start();

        verify(mainView).invalidateDateViews();
    }

    @Test
    public void shouldSaveEventOnPush() {
        mockCommons();

        mainPresenter.start();
        PUBLISHER.onNext(EVENT);

        verify(sharedPreferenceModel).setLong(MainActivity.EVENT_KEY, EVENT_ID);
    }

    @Test
    public void shouldShowDashboardOnEventIdStored() {
        mockCommons();

        when(sharedPreferenceModel.getLong(anyString(), anyLong())).thenReturn(EVENT_ID);
        ContextManager.setSelectedEvent(EVENT);

        mainPresenter.start();

        verify(mainView).showDashboard();
    }

    @Test
    public void shouldLoadEventFromRepoIfNotStored() {
        mockCommons();

        when(sharedPreferenceModel.getLong(anyString(), anyLong())).thenReturn(EVENT_ID);
        ContextManager.setSelectedEvent(null);

        mainPresenter.start();

        verify(eventRepository).getEvent(EVENT_ID, false);
    }

    @Test
    public void shouldShowEventsIfNoneStored() {
        mockCommons();

        when(sharedPreferenceModel.getLong(anyString(), anyLong())).thenReturn(-1L);
        ContextManager.setSelectedEvent(null);

        mainPresenter.start();

        verify(mainView).showEventList();
    }

    @Test
    public void shouldShowResultInViewOnEventPush() {
        mockCommons();

        mainPresenter.start();
        PUBLISHER.onNext(EVENT);

        verify(mainView).showResult(EVENT);
    }

}
