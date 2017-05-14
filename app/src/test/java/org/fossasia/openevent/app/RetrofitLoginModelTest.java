package org.fossasia.openevent.app;

import org.fossasia.openevent.app.contract.model.UtilModel;
import org.fossasia.openevent.app.data.models.Login;
import org.fossasia.openevent.app.data.models.LoginResponse;
import org.fossasia.openevent.app.data.network.api.LoginService;
import org.fossasia.openevent.app.data.network.api.RetrofitLoginModel;
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

@RunWith(JUnit4.class)
public class RetrofitLoginModelTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private RetrofitLoginModel retrofitLoginModel;

    @Mock
    UtilModel utilModel;

    @Mock
    LoginService loginService;

    private String token = "TestToken";
    private String email = "test";
    private String password = "test";

    @Before
    public void setUp() {
        retrofitLoginModel = new RetrofitLoginModel(utilModel);
        retrofitLoginModel.setLoginService(loginService);
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
        Mockito.when(utilModel.getString(Constants.SHARED_PREFS_TOKEN, null)).thenReturn(token);

        Observable<LoginResponse> responseObservable = retrofitLoginModel.login(email, password);

        assertEquals(responseObservable.blockingFirst().getAccessToken(), token);

        Mockito.verifyNoMoreInteractions(loginService);
    }

    @Test
    public void shouldCallServiceOnCacheMiss() {
        Mockito.when(utilModel.getString(Constants.SHARED_PREFS_TOKEN, null)).thenReturn(null);
        Mockito.when(utilModel.isConnected()).thenReturn(true);
        Mockito.when(loginService.login(Mockito.any(Login.class)))
            .thenReturn(Observable.just(new LoginResponse(token)));

        Observable<LoginResponse> responseObservable = retrofitLoginModel.login(email, password);

        assertEquals(responseObservable.blockingFirst().getAccessToken(), token);

        Mockito.verify(loginService).login(Mockito.any(Login.class));
        // Should save token on object return
        Mockito.verify(utilModel).saveString(Constants.SHARED_PREFS_TOKEN, token);
    }

    @Test
    public void shouldNotSaveTokenOnErrorResponse() {
        Mockito.when(utilModel.getString(Constants.SHARED_PREFS_TOKEN, null)).thenReturn(null);
        Mockito.when(utilModel.isConnected()).thenReturn(true);
        Mockito.when(loginService.login(Mockito.any(Login.class)))
            .thenReturn(Observable.error(new Throwable("Error")));

        Observable<LoginResponse> responseObservable = retrofitLoginModel.login(email, password);
        responseObservable.test().assertErrorMessage("Error");

        Mockito.verify(loginService).login(Mockito.any(Login.class));
        // Should not save token on object return
        Mockito.verify(utilModel, Mockito.never()).saveString(Constants.SHARED_PREFS_TOKEN, token);
    }

    @Test
    public void shouldSendErrorOnNetworkDown() {
        Mockito.when(utilModel.isConnected()).thenReturn(false);

        Observable<LoginResponse> responseObservable = retrofitLoginModel.login(email, password);

        responseObservable.test().assertErrorMessage(Constants.NO_NETWORK);

        Mockito.verifyNoMoreInteractions(loginService);
    }


}
