package org.fossasia.openevent.app.model;

import org.fossasia.openevent.app.data.LoginModel;
import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.data.models.Login;
import org.fossasia.openevent.app.data.models.LoginResponse;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class LoginModelTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private LoginModel loginModel;

    @Mock
    IUtilModel utilModel;

    @Mock
    EventService eventService;

    private String token = "TestToken";
    private String email = "test";
    private String password = "test";

    @Before
    public void setUp() {
        loginModel = new LoginModel(utilModel, eventService);
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

        Mockito.doReturn(true).when(spied).isLoggedIn();
        Mockito.when(utilModel.getToken()).thenReturn(token);

        Observable<LoginResponse> responseObservable = spied.login(email, password);

        responseObservable
            .map(LoginResponse::getAccessToken)
            .test()
            .assertValue(token);

        Mockito.verifyNoMoreInteractions(eventService);
    }

    @Test
    public void shouldCallServiceOnCacheMiss() {
        Mockito.when(utilModel.isConnected()).thenReturn(true);
        Mockito.when(eventService.login(Mockito.any(Login.class)))
            .thenReturn(Observable.just(new LoginResponse(token)));

        Observable<LoginResponse> responseObservable = loginModel.login(email, password);


        assertEquals(responseObservable.blockingFirst().getAccessToken(), token);

        Mockito.verify(eventService).login(Mockito.any(Login.class));
        // Should save token on object return
        Mockito.verify(utilModel).saveToken(token);
    }

    @Test
    public void shouldNotSaveTokenOnErrorResponse() {
        Mockito.when(utilModel.isConnected()).thenReturn(true);
        Mockito.when(eventService.login(Mockito.any(Login.class)))
            .thenReturn(Observable.error(new Throwable("Error")));

        Observable<LoginResponse> responseObservable = loginModel.login(email, password);
        responseObservable.test().assertErrorMessage("Error");

        Mockito.verify(eventService).login(Mockito.any(Login.class));
        // Should not save token on object return
        Mockito.verify(utilModel, Mockito.never()).saveString(Constants.SHARED_PREFS_TOKEN, token);
    }

    @Test
    public void shouldSendErrorOnNetworkDown() {
        Mockito.when(utilModel.isConnected()).thenReturn(false);

        Observable<LoginResponse> responseObservable = loginModel.login(email, password);

        responseObservable.test().assertErrorMessage(Constants.NO_NETWORK);

        Mockito.verifyNoMoreInteractions(eventService);
    }

    @Test
    public void shouldSayLoggedOutOnNull() {
        Mockito.when(utilModel.getToken()).thenReturn(null);

        assertFalse(loginModel.isLoggedIn());

        Mockito.verify(utilModel).getToken();
    }

    @Test
    public void shouldResetExpiredToken() {
        String expiredToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYmYiOjE0OTU3NDU0MDAsImlhdCI6MTQ5NTc0NTQwMCwiZXhwIjoxNDk1NzQ1ODAwLCJpZGVudGl0eSI6MzQ0fQ.NlZ9mrmEPyGpzQ-aIqauhwliYLh9GMiz11sG-EUaQ6I";

        Mockito.when(utilModel.getToken()).thenReturn(expiredToken);

        assertFalse(loginModel.isLoggedIn());

        Mockito.verify(utilModel).getToken();
    }

    @Test
    public void shouldSayLoggedInOnUnexpired() {
        String unexpirableToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYmYiOjE0OTU3NDU0MDAsImlhdCI6MTQ5NTc0NTQwMCwiZXhwIjoyNDk1ODMxODAwLCJpZGVudGl0eSI6MzQ0fQ.A_aC4hwK8sixZk4k9gzmzidO1wj2hjy_EH573uorK-E";

        Mockito.when(utilModel.getToken()).thenReturn(unexpirableToken);

        assertTrue(loginModel.isLoggedIn());

        Mockito.verify(utilModel).getToken();
    }

}
