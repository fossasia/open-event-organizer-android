package org.fossasia.openevent.app.model;

import org.fossasia.openevent.app.data.LoginModel;
import org.fossasia.openevent.app.data.contract.ISharedPreferenceModel;
import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.data.db.contract.IDatabaseRepository;
import org.fossasia.openevent.app.data.models.Login;
import org.fossasia.openevent.app.data.models.LoginResponse;
import org.fossasia.openevent.app.data.models.User;
import org.fossasia.openevent.app.data.network.EventService;
import org.fossasia.openevent.app.utils.Constants;
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
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class LoginModelTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private LoginModel loginModel;

    @Mock
    IUtilModel utilModel;

    @Mock
    ISharedPreferenceModel sharedPreferenceModel;

    @Mock
    EventService eventService;

    @Mock
    IDatabaseRepository databaseRepository;

    private String token = "TestToken";
    private String email = "test";
    private String password = "test";

    private static final String EXPIRED_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYmYiOjE0OTU3NDU0MDAsImlhdCI6MTQ5NTc0NTQwMCwiZXhwIjoxNDk1NzQ1ODAwLCJpZGVudGl0eSI6MzQ0fQ.NlZ9mrmEPyGpzQ-aIqauhwliYLh9GMiz11sG-EUaQ6I";
    private static final String UNEXPIRABLE_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYmYiOjE0OTU3NDU0MDAsImlhdCI6MTQ5NTc0NTQwMCwiZXhwIjoyNDk1ODMxODAwLCJpZGVudGl0eSI6MzQ0fQ.A_aC4hwK8sixZk4k9gzmzidO1wj2hjy_EH573uorK-E";

    @Before
    public void setUp() {
        loginModel = new LoginModel(utilModel, sharedPreferenceModel, eventService, databaseRepository);
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
        LoginModel spied = Mockito.spy(loginModel);

        doReturn(true).when(spied).isLoggedIn();

        spied.login(email, password).test();

        verifyNoMoreInteractions(eventService);
    }

    @Test
    public void shouldCallServiceOnCacheMiss() {
        when(utilModel.isConnected()).thenReturn(true);
        when(utilModel.getToken()).thenReturn(null);
        when(databaseRepository.getAllItems(User.class)).thenReturn(Observable.empty());
        when(eventService.login(Mockito.any(Login.class)))
            .thenReturn(Observable.just(new LoginResponse(token)));

        loginModel.login(email, password).test();

        verify(eventService).login(Mockito.any(Login.class));
    }

    @Test
    public void shouldSaveTokenOnLoginResponse() {
        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.login(Mockito.any(Login.class)))
            .thenReturn(Observable.just(new LoginResponse(token)));

        loginModel.login(email, password).test();

        verify(eventService).login(Mockito.any(Login.class));
        // Should save token on object return
        verify(utilModel).saveToken(token);
    }

    @Test
    public void shouldNotSaveTokenOnErrorResponse() {
        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.login(Mockito.any(Login.class)))
            .thenReturn(Observable.error(new Throwable("Error")));

        loginModel.login(email, password).test().assertErrorMessage("Error");

        verify(eventService).login(Mockito.any(Login.class));
        // Should not save token on object return
        verify(utilModel, Mockito.never()).saveToken(anyString());
    }

    @Test
    public void shouldSendErrorOnNetworkDown() {
        when(utilModel.isConnected()).thenReturn(false);

        loginModel.login(email, password).test().assertErrorMessage(Constants.NO_NETWORK);

        verifyNoMoreInteractions(eventService);
    }

    @Test
    public void shouldSayLoggedOutOnNull() {
        when(utilModel.getToken()).thenReturn(null);

        assertFalse(loginModel.isLoggedIn());
        verify(utilModel).getToken();
    }

    @Test
    public void shouldResetExpiredToken() {
        when(utilModel.getToken()).thenReturn(EXPIRED_TOKEN);

        when(utilModel.isConnected()).thenReturn(true);
        when(databaseRepository.getAllItems(User.class)).thenReturn(Observable.empty());
        when(eventService.login(Mockito.any(Login.class)))
            .thenReturn(Observable.just(new LoginResponse(token)));

        loginModel.login(email, password).test();

        verify(utilModel).saveToken(token);
    }

    @Test
    public void shouldSayLoggedInOnUnexpired() {
        when(utilModel.getToken()).thenReturn(UNEXPIRABLE_TOKEN);

        assertTrue(loginModel.isLoggedIn());

        verify(utilModel).getToken();
    }

    @Test
    public void shouldClearTokenOnLogout() {
        loginModel.logout().subscribe();

        verify(utilModel).saveToken(null);
    }

    @Test
    public void shouldLoginOnExistingSameUser() {
        when(utilModel.isConnected()).thenReturn(true);
        when(eventService.login(Mockito.any(Login.class)))
            .thenReturn(Observable.empty());

        loginModel.login(email, password).test();

        verify(eventService).login(Mockito.any(Login.class));
        verify(utilModel, Mockito.never()).deleteDatabase();
    }

    @Test
    public void shouldDeleteDatabaseOnDifferentUserLogin() {
        when(utilModel.isConnected()).thenReturn(true);
        when(utilModel.getToken()).thenReturn(null);
        when(databaseRepository.getAllItems(User.class))
            .thenReturn(Observable.just(User.builder().email(email).id(354).build()));
        when(eventService.login(Mockito.any(Login.class)))
            .thenReturn(Observable.just(new LoginResponse("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYmYiOjE0OTU3NDU0MDAsImlhdCI6MTQ5NTc0NTQwMCwiZXhwIjoxNDk1NzQ1ODAwLCJpZGVudGl0eSI6MzQ0fQ.NlZ9mrmEPyGpzQ-aIqauhwliYLh9GMiz11sG-EUaQ6I")));

        loginModel.login(email, password).test();

        verify(eventService).login(Mockito.any(Login.class));
        verify(utilModel).deleteDatabase();
    }

    @Test
    public void shouldSaveEmailOnLoginSuccessFully() {
        when(utilModel.isConnected()).thenReturn(true);
        when(utilModel.getToken()).thenReturn(EXPIRED_TOKEN);
        when(databaseRepository.getAllItems(User.class))
            .thenReturn(Observable.just(User.builder().id(344).email(email).build()));
        when(eventService.login(Mockito.any(Login.class)))
            .thenReturn(Observable.just(new LoginResponse("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYmYiOjE0OTU3NDU0MDAsImlhdCI6MTQ5NTc0NTQwMCwiZXhwIjoxNDk1NzQ1ODAwLCJpZGVudGl0eSI6MzQ0fQ.NlZ9mrmEPyGpzQ-aIqauhwliYLh9GMiz11sG-EUaQ6I")));

        loginModel.login(email + "new", password).test();

        verify(sharedPreferenceModel).addStringSetElement(Constants.SHARED_PREFS_SAVED_EMAIL, email + "new");
    }
}
