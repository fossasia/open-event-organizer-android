package org.fossasia.openevent.app.data.auth;

import org.fossasia.openevent.app.data.auth.model.Login;
import org.fossasia.openevent.app.data.auth.model.LoginResponse;
import org.fossasia.openevent.app.data.user.User;
import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.data.Repository;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class AuthServiceTest {
    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    private AuthService authService;
    @Mock private AuthHolder authHolder;
    @Mock private AuthApi authApi;
    @Mock private Repository repository;

    private static final String TOKEN = "TestToken";
    private static final String EMAIL = "test";
    private static final String PASSWORD = "test";
    private static final Login LOGIN = new Login(EMAIL, PASSWORD);

    private static final String EXPIRED_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
        ".eyJuYmYiOjE0OTU3NDU0MDAsImlhdCI6MTQ5NTc0NTQwMCwiZXhwIjoxNDk1NzQ1ODAwLCJpZGVudGl0eSI6MzQ0fQ" +
        ".NlZ9mrmEPyGpzQ-aIqauhwliYLh9GMiz11sG-EUaQ6I";

    @Before
    public void setUp() {
        authService = new AuthServiceImpl(authHolder, repository, authApi);
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
        AuthService spied = Mockito.spy(authService);

        doReturn(true).when(spied).isLoggedIn();

        spied.login(LOGIN).test();

        verifyNoMoreInteractions(authApi);
    }

    @Test
    public void shouldCallServiceOnCacheMiss() {
        when(repository.isConnected()).thenReturn(true);
        when(authHolder.getToken()).thenReturn(null);
        when(repository.getAllItems(User.class)).thenReturn(Observable.empty());
        when(authApi.login(Mockito.any(Login.class)))
            .thenReturn(Observable.just(new LoginResponse(TOKEN)));

        authService.login(LOGIN).test();

        verify(authApi).login(Mockito.any(Login.class));
    }

    @Test
    public void shouldSaveTokenOnLoginResponse() {
        when(repository.isConnected()).thenReturn(true);
        when(authApi.login(Mockito.any(Login.class)))
            .thenReturn(Observable.just(new LoginResponse(TOKEN)));

        authService.login(LOGIN).test();

        verify(authApi).login(Mockito.any(Login.class));
        // Should save TOKEN on object return
        verify(authHolder).login(TOKEN);
    }

    @Test
    public void shouldNotSaveTokenOnErrorResponse() {
        when(repository.isConnected()).thenReturn(true);
        when(authApi.login(Mockito.any(Login.class)))
            .thenReturn(Observable.error(new Throwable("Error")));

        authService.login(LOGIN).test().assertErrorMessage("Error");

        verify(authApi).login(Mockito.any(Login.class));
        // Should not save TOKEN on object return
        verify(authHolder, Mockito.never()).login(anyString());
    }

    @Test
    public void shouldSendErrorOnNetworkDown() {
        when(repository.isConnected()).thenReturn(false);

        authService.login(LOGIN).test().assertErrorMessage(Constants.NO_NETWORK);

        verifyNoMoreInteractions(authApi);
    }

    @Test
    public void shouldResetExpiredToken() {
        when(authHolder.getToken()).thenReturn(EXPIRED_TOKEN);

        when(repository.isConnected()).thenReturn(true);
        when(repository.getAllItems(User.class)).thenReturn(Observable.empty());
        when(authApi.login(Mockito.any(Login.class)))
            .thenReturn(Observable.just(new LoginResponse(TOKEN)));

        authService.login(LOGIN).test();

        verify(authHolder).login(TOKEN);
    }

    @Test
    public void shouldClearTokenOnLogout() {
        authService.logout().subscribe();

        verify(authHolder).logout();
    }

    @Test
    public void shouldLoginOnExistingSameUser() {
        when(repository.isConnected()).thenReturn(true);
        when(authApi.login(Mockito.any(Login.class)))
            .thenReturn(Observable.empty());

        authService.login(LOGIN).test();

        verify(authApi).login(Mockito.any(Login.class));
        verify(repository, Mockito.never()).deleteDatabase();
    }

    @Test
    public void shouldDeleteDatabaseOnDifferentUserLogin() {
        when(repository.isConnected()).thenReturn(true);
        when(authHolder.getToken()).thenReturn(null);
        when(repository.getAllItems(User.class))
            .thenReturn(Observable.just(User.builder().email(EMAIL).id(354).build()));
        when(authApi.login(Mockito.any(Login.class)))
            .thenReturn(Observable.just(new LoginResponse(
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
                    ".eyJuYmYiOjE0OTU3NDU0MDAsImlhdCI6MTQ5NTc0NTQwMCwiZXhwIjoxNDk1NzQ1ODAwLCJpZGVudGl0eSI6MzQ0fQ" +
                    ".NlZ9mrmEPyGpzQ-aIqauhwliYLh9GMiz11sG-EUaQ6I"
            )));

        authService.login(LOGIN).test();

        verify(authApi).login(Mockito.any(Login.class));
        verify(repository).deleteDatabase();
    }

    @Test
    public void shouldSaveEmailOnLoginSuccessFully() {
        when(repository.isConnected()).thenReturn(true);
        when(authHolder.getToken()).thenReturn(EXPIRED_TOKEN);
        when(repository.getAllItems(User.class))
            .thenReturn(Observable.just(User.builder().id(344).email(EMAIL).build()));
        when(authApi.login(Mockito.any(Login.class)))
            .thenReturn(Observable.just(new LoginResponse(
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
                    ".eyJuYmYiOjE0OTU3NDU0MDAsImlhdCI6MTQ5NTc0NTQwMCwiZXhwIjoxNDk1NzQ1ODAwLCJpZGVudGl0eSI6MzQ0fQ" +
                    ".NlZ9mrmEPyGpzQ-aIqauhwliYLh9GMiz11sG-EUaQ6I"
            )));

        authService.login(new Login(EMAIL + "new", PASSWORD)).test();

        verify(authHolder).saveEmail(EMAIL + "new");
    }


}
