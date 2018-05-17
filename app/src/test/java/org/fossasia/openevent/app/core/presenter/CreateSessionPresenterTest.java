package org.fossasia.openevent.app.core.presenter;

import org.fossasia.openevent.app.core.session.create.CreateSessionPresenter;
import org.fossasia.openevent.app.core.session.create.CreateSessionView;
import org.fossasia.openevent.app.data.session.Session;
import org.fossasia.openevent.app.data.session.SessionRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class CreateSessionPresenterTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private CreateSessionView createSessionView;
    @Mock
    private SessionRepository sessionRepository;

    private CreateSessionPresenter createSessionPresenter;
    private static final Session SESSION = Session.builder().id(2L).title("dd").build();
    private static final long EVENT_ID = 5L;
    private static final long TRACK_ID = 5L;

    @Before
    public void setUp() {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());

        createSessionPresenter = new CreateSessionPresenter(sessionRepository);
        createSessionPresenter.attach(createSessionView);
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldShowSuccessOnCreated() {
        when(sessionRepository.createSession(createSessionPresenter.getSession())).thenReturn(Observable.just(SESSION));

        createSessionPresenter.createSession(TRACK_ID, EVENT_ID);

        InOrder inOrder = Mockito.inOrder(createSessionView);

        inOrder.verify(createSessionView).showProgress(true);
        inOrder.verify(createSessionView).onSuccess(anyString());
        inOrder.verify(createSessionView).showProgress(false);
    }

    @Test
    public void shouldShowErrorOnFailure() {
        when(sessionRepository.createSession(createSessionPresenter.getSession())).thenReturn(Observable.error(new Throwable("Error")));

        createSessionPresenter.createSession(TRACK_ID, EVENT_ID);

        InOrder inOrder = Mockito.inOrder(createSessionView);

        inOrder.verify(createSessionView).showProgress(true);
        inOrder.verify(createSessionView).showError("Error");
        inOrder.verify(createSessionView).showProgress(false);
    }
}
