package org.fossasia.openevent.app.unit.presenter;

import org.fossasia.openevent.app.common.app.ContextManager;
import org.fossasia.openevent.app.common.data.contract.IAuthModel;
import org.fossasia.openevent.app.common.data.contract.ISharedPreferenceModel;
import org.fossasia.openevent.app.module.main.MainPresenter;
import org.fossasia.openevent.app.module.main.contract.IMainView;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.reactivex.Completable;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MainPresenterTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    IAuthModel loginModel;

    @Mock
    ISharedPreferenceModel sharedPreferenceModel;

    @Mock
    IMainView mainView;

    @Mock
    ContextManager contextManager;

    private MainPresenter mainPresenter;

    @Before
    public void setUp() {
        mainPresenter = new MainPresenter(sharedPreferenceModel, loginModel, null, null, contextManager);
        mainPresenter.attach(mainView);
    }

    @Test
    public void shouldClearSentryContext() {
        when(loginModel.logout()).thenReturn(Completable.complete());

        mainPresenter.logout();

        verify(contextManager).clearOrganiser();
    }

}
