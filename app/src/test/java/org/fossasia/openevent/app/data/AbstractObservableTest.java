package org.fossasia.openevent.app.data;

import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.data.network.ConnectionStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class AbstractObservableTest {

    private static final String DISK = "disk";

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private AbstractObservable abstractObservable;

    @Mock
    private ConnectionStatus connectionStatus;

    @Before
    public void setUp() {
        abstractObservable = new AbstractObservable(connectionStatus);
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void testDiskValue() {
        String networkString = "network";

        abstractObservable.of(String.class)
            .reload(false)
            .withDiskObservable(Observable.defer(() ->
                Observable.just(DISK)))
            .withNetworkObservable(Observable.defer(() ->
                Observable.just(networkString)))
            .build()
            .test()
            .assertValue(DISK);
    }

    @Test
    public void testNetwork() {
        String networkString = "network";

        when(connectionStatus.isConnected()).thenReturn(true);
        abstractObservable.of(String.class)
            .reload(false)
            .withDiskObservable(Observable.empty())
            .withNetworkObservable(Observable.defer(() ->
                Observable.just(networkString)))
            .build()
            .test()
            .assertValue(networkString);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void networkNotCalled() {
        Observable<String> networkObservable = mock(Observable.class);

        abstractObservable.of(String.class)
            .reload(false)
            .withDiskObservable(Observable.defer(() ->
                Observable.just(DISK)))
            .withNetworkObservable(networkObservable)
            .build()
            .test();

        verifyZeroInteractions(networkObservable);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void networkCalled() {
        Observable<String> networkObservable = spy(Observable.just("Test"));

        when(connectionStatus.isConnected()).thenReturn(true);
        abstractObservable.of(String.class)
            .reload(true)
            .withDiskObservable(Observable.defer(() ->
                Observable.just(DISK)))
            .withNetworkObservable(networkObservable)
            .build()
            .subscribe();

        verify(networkObservable).subscribe(any(Observer.class));
    }

    @Test
    public void throwError() {
        when(connectionStatus.isConnected()).thenReturn(false);
        abstractObservable.of(String.class)
            .reload(true)
            .withDiskObservable(Observable.defer(() ->
                Observable.just(DISK)))
            .withNetworkObservable(Observable.just("Test"))
            .build()
            .test()
            .assertErrorMessage(Constants.NO_NETWORK);
    }

    @Test
    public void throwErrorNoReload() {
        when(connectionStatus.isConnected()).thenReturn(false);
        abstractObservable.of(String.class)
            .reload(false)
            .withDiskObservable(Observable.empty())
            .withNetworkObservable(Observable.just("Test"))
            .build()
            .test()
            .assertErrorMessage(Constants.NO_NETWORK);
    }

}
