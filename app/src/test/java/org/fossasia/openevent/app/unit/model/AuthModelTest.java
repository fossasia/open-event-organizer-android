package org.fossasia.openevent.app.unit.model;

import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.common.ContextManager;
import org.fossasia.openevent.app.data.AuthModel;
import org.fossasia.openevent.app.data.ISharedPreferenceModel;
import org.fossasia.openevent.app.data.IUtilModel;
import org.fossasia.openevent.app.data.db.IDatabaseRepository;
import org.fossasia.openevent.app.data.models.User;
import org.fossasia.openevent.app.data.models.dto.Login;
import org.fossasia.openevent.app.data.models.dto.LoginResponse;
import org.fossasia.openevent.app.data.network.EventService;
import org.fossasia.openevent.app.core.main.MainActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class AuthModelTest {
    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    private AuthModel authModel;
    @Mock private IUtilModel utilModel;
    @Mock private ISharedPreferenceModel sharedPreferenceModel;
    @Mock private EventService eventService;
    @Mock private IDatabaseRepository databaseRepository;

    private static final String TOKEN = "TestToken";
    private static final String EMAIL = "test";
    private static final String PASSWORD = "test";
    private static final Login LOGIN = new Login(EMAIL, PASSWORD);

    private static final String EXPIRED_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
        ".eyJuYmYiOjE0OTU3NDU0MDAsImlhdCI6MTQ5NTc0NTQwMCwiZXhwIjoxNDk1NzQ1ODAwLCJpZGVudGl0eSI6MzQ0fQ" +
        ".NlZ9mrmEPyGpzQ-aIqauhwliYLh9GMiz11sG-EUaQ6I";
    private static final String UNEXPIRABLE_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
        ".eyJuYmYiOjE0OTU3NDU0MDAsImlhdCI6MTQ5NTc0NTQwMCwiZXhwIjoyNDk1ODMxODAwLCJpZGVudGl0eSI6MzQ0fQ" +
        ".A_aC4hwK8sixZk4k9gzmzidO1wj2hjy_EH573uorK-E";

    @Before
    public void setUp() {
        authModel = new AuthModel(utilModel, sharedPreferenceModel, eventService, databaseRepository);
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldCacheLogin() {
        // Partial mocking
        AuthModel spied = Mockito.spy(authModel);

        doReturn(true).when(spied).isLoggedIn();

        spied.login(LOGIN).test();

        verifyNoMoreInteractions(eventService);
    }

    @Test
    public void shouldCallServiceOnCacheMiss() {
        when(utilModel.isConnected()).thenReturn(true);
        when(utilModel.getToken()).thenReturn(null);
        when(databaseRepository.getAllItems(User.class)).thenReturn(Observable.empty());
        when(eventService.login(Mockito.any(Login.class)))
            .thenReturn(Observable.just(new LoginResponse(TOKEN)));

        authModel.login(LOGIN).test();

        verify(eventService).login(Mockito.any(Login.class));
    }

    @Test
    public void shouldSaveTokenOnLoginResponse() {
        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.login(Mockito.any(Login.class)))
            .thenReturn(Observable.just(new LoginResponse(TOKEN)));

        authModel.login(LOGIN).test();

        verify(eventService).login(Mockito.any(Login.class));
        // Should save TOKEN on object return
        verify(utilModel).saveToken(TOKEN);
    }

    @Test
    public void shouldNotSaveTokenOnErrorResponse() {
        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.login(Mockito.any(Login.class)))
            .thenReturn(Observable.error(new Throwable("Error")));

        authModel.login(LOGIN).test().assertErrorMessage("Error");

        verify(eventService).login(Mockito.any(Login.class));
        // Should not save TOKEN on object return
        verify(utilModel, Mockito.never()).saveToken(anyString());
    }

    @Test
    public void shouldSendErrorOnNetworkDown() {
        when(utilModel.isConnected()).thenReturn(false);

        authModel.login(LOGIN).test().assertErrorMessage(Constants.NO_NETWORK);

        verifyNoMoreInteractions(eventService);
    }

    @Test
    public void shouldSayLoggedOutOnNull() {
        when(utilModel.getToken()).thenReturn(null);

        assertFalse(authModel.isLoggedIn());
        verify(utilModel).getToken();
    }

    @Test
    public void shouldResetExpiredToken() {
        when(utilModel.getToken()).thenReturn(EXPIRED_TOKEN);

        when(utilModel.isConnected()).thenReturn(true);
        when(databaseRepository.getAllItems(User.class)).thenReturn(Observable.empty());
        when(eventService.login(Mockito.any(Login.class)))
            .thenReturn(Observable.just(new LoginResponse(TOKEN)));

        authModel.login(LOGIN).test();

        verify(utilModel).saveToken(TOKEN);
    }

    @Test
    public void shouldSayLoggedInOnUnexpired() {
        when(utilModel.getToken()).thenReturn(UNEXPIRABLE_TOKEN);

        assertTrue(authModel.isLoggedIn());

        verify(utilModel).getToken();
    }

    @Test
    public void shouldClearTokenOnLogout() {
        authModel.logout().subscribe();

        verify(utilModel).saveToken(null);
    }

    @Test
    public void shouldLoginOnExistingSameUser() {
        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.login(Mockito.any(Login.class)))
            .thenReturn(Observable.empty());

        authModel.login(LOGIN).test();

        verify(eventService).login(Mockito.any(Login.class));
        verify(utilModel, Mockito.never()).deleteDatabase();
    }

    @Test
    public void shouldDeleteDatabaseOnDifferentUserLogin() {
        when(utilModel.isConnected()).thenReturn(true);
        when(utilModel.getToken()).thenReturn(null);
        when(databaseRepository.getAllItems(User.class))
            .thenReturn(Observable.just(User.builder().email(EMAIL).id(354).build()));
        when(eventService.login(Mockito.any(Login.class)))
            .thenReturn(Observable.just(new LoginResponse(
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
                    ".eyJuYmYiOjE0OTU3NDU0MDAsImlhdCI6MTQ5NTc0NTQwMCwiZXhwIjoxNDk1NzQ1ODAwLCJpZGVudGl0eSI6MzQ0fQ" +
                    ".NlZ9mrmEPyGpzQ-aIqauhwliYLh9GMiz11sG-EUaQ6I"
            )));

        authModel.login(LOGIN).test();

        verify(eventService).login(Mockito.any(Login.class));
        verify(utilModel).deleteDatabase();
    }

    @Test
    public void shouldSaveEmailOnLoginSuccessFully() {
        when(utilModel.isConnected()).thenReturn(true);
        when(utilModel.getToken()).thenReturn(EXPIRED_TOKEN);
        when(databaseRepository.getAllItems(User.class))
            .thenReturn(Observable.just(User.builder().id(344).email(EMAIL).build()));
        when(eventService.login(Mockito.any(Login.class)))
            .thenReturn(Observable.just(new LoginResponse(
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
                    ".eyJuYmYiOjE0OTU3NDU0MDAsImlhdCI6MTQ5NTc0NTQwMCwiZXhwIjoxNDk1NzQ1ODAwLCJpZGVudGl0eSI6MzQ0fQ" +
                    ".NlZ9mrmEPyGpzQ-aIqauhwliYLh9GMiz11sG-EUaQ6I"
            )));

        authModel.login(new Login(EMAIL + "new", PASSWORD)).test();

        verify(sharedPreferenceModel).addStringSetElement(Constants.SHARED_PREFS_SAVED_EMAIL, EMAIL + "new");
    }

    @Test
    public void shouldClearSelectedEventOnLogout() {
        authModel.logout().test();

        verify(utilModel).saveToken(null);
        verify(sharedPreferenceModel).setLong(MainActivity.EVENT_KEY, -1);
        assertNull(ContextManager.getSelectedEvent());
    }
}
