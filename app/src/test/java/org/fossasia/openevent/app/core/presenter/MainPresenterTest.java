package org.fossasia.openevent.app.core.presenter;

import com.f2prateek.rx.preferences2.Preference;
import com.f2prateek.rx.preferences2.RxSharedPreferences;

import org.fossasia.openevent.app.common.ContextManager;
import org.fossasia.openevent.app.core.main.MainPresenter;
import org.fossasia.openevent.app.core.main.MainView;
import org.fossasia.openevent.app.data.auth.AuthService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.reactivex.Completable;
import io.reactivex.Observable;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MainPresenterTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock private AuthService loginModel;
    @Mock private MainView mainView;
    @Mock private ContextManager contextManager;
    @Mock private Preference<Boolean> booleanPref;
    @Mock private RxSharedPreferences rxSharedPreferences;

    private MainPresenter mainPresenter;

    @Before
    public void setUp() {
        mainPresenter = new MainPresenter(loginModel, rxSharedPreferences, contextManager);
        mainPresenter.attach(mainView);
    }

    private void mockCommons() {
        when(booleanPref.asObservable()).thenReturn(Observable.just(true));
        when(rxSharedPreferences.getBoolean(anyString())).thenReturn(booleanPref);
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

        mainPresenter.start();

        verify(mainView).invalidateDateViews();
    }

}
