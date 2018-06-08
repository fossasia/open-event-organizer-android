package org.fossasia.openevent.app.core.presenter;

import org.fossasia.openevent.app.core.session.create.CreateSessionPresenter;
import org.fossasia.openevent.app.core.session.create.CreateSessionView;
import org.fossasia.openevent.app.data.session.Session;
import org.fossasia.openevent.app.data.session.SessionRepository;
import org.fossasia.openevent.app.utils.DateUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.threeten.bp.LocalDateTime;

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
    private static final String ERROR = "Error";
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
    public void shouldRejectWrongDates() {
        Session session = createSessionPresenter.getSession();

        String isoDate = DateUtils.formatDateToIso(LocalDateTime.now());
        session.setStartsAt(isoDate);
        session.setEndsAt(isoDate);

        createSessionPresenter.createSession(TRACK_ID, EVENT_ID);

        verify(createSessionView).showError(anyString());
        verify(sessionRepository, never()).createSession(any());
    }

    @Test
    public void shouldAcceptCorrectDates() {
        Session session = createSessionPresenter.getSession();

        when(sessionRepository.createSession(session)).thenReturn(Observable.empty());

        String isoDateNow = DateUtils.formatDateToIso(LocalDateTime.now());
        String isoDateThen = DateUtils.formatDateToIso(LocalDateTime.MAX);
        session.setStartsAt(isoDateNow);
        session.setEndsAt(isoDateThen);

        createSessionPresenter.createSession(TRACK_ID, EVENT_ID);

        verify(createSessionView, never()).showError(anyString());
        verify(sessionRepository).createSession(session);
    }

    @Test
    public void shouldNullifyEmptyFields() {
        Session session = createSessionPresenter.getSession();
        when(sessionRepository.createSession(session)).thenReturn(Observable.just(session));

        session.setSlidesUrl("");
        session.setAudioUrl("");
        session.setVideoUrl("");
        session.setSignupUrl("");

        String isoDateNow = DateUtils.formatDateToIso(LocalDateTime.now());
        String isoDateMax = DateUtils.formatDateToIso(LocalDateTime.MAX);
        session.setStartsAt(isoDateNow);
        session.setEndsAt(isoDateMax);

        createSessionPresenter.createSession(TRACK_ID, EVENT_ID);
        assertNull(session.getSlidesUrl());
        assertNull(session.getAudioUrl());
        assertNull(session.getVideoUrl());
        assertNull(session.getSignupUrl());
    }

    @Test
    public void shouldShowSuccessOnCreated() {
        Session session = createSessionPresenter.getSession();

        String isoDateNow = DateUtils.formatDateToIso(LocalDateTime.now());
        String isoDateThen = DateUtils.formatDateToIso(LocalDateTime.MAX);
        session.setStartsAt(isoDateNow);
        session.setEndsAt(isoDateThen);

        when(sessionRepository.createSession(createSessionPresenter.getSession())).thenReturn(Observable.just(SESSION));

        createSessionPresenter.createSession(TRACK_ID, EVENT_ID);

        InOrder inOrder = Mockito.inOrder(createSessionView);

        inOrder.verify(createSessionView).showProgress(true);
        inOrder.verify(createSessionView).onSuccess(anyString());
        inOrder.verify(createSessionView).showProgress(false);
    }

    @Test
    public void shouldShowErrorOnFailure() {
        Session session = createSessionPresenter.getSession();

        String isoDateNow = DateUtils.formatDateToIso(LocalDateTime.now());
        String isoDateThen = DateUtils.formatDateToIso(LocalDateTime.MAX);
        session.setStartsAt(isoDateNow);
        session.setEndsAt(isoDateThen);

        when(sessionRepository.createSession(createSessionPresenter.getSession())).thenReturn(Observable.error(new Throwable(ERROR)));

        createSessionPresenter.createSession(TRACK_ID, EVENT_ID);

        InOrder inOrder = Mockito.inOrder(createSessionView);

        inOrder.verify(createSessionView).showProgress(true);
        inOrder.verify(createSessionView).showError(ERROR);
        inOrder.verify(createSessionView).showProgress(false);
    }

    @Test
    public void shouldShowSuccessOnUpdated() {
        Session session = createSessionPresenter.getSession();

        String isoDateNow = DateUtils.formatDateToIso(LocalDateTime.now());
        String isoDateThen = DateUtils.formatDateToIso(LocalDateTime.MAX);
        session.setStartsAt(isoDateNow);
        session.setEndsAt(isoDateThen);

        when(sessionRepository.updateSession(createSessionPresenter.getSession())).thenReturn(Observable.just(SESSION));

        createSessionPresenter.updateSession(TRACK_ID, EVENT_ID);

        InOrder inOrder = Mockito.inOrder(createSessionView);

        inOrder.verify(createSessionView).showProgress(true);
        inOrder.verify(createSessionView).onSuccess(anyString());
        inOrder.verify(createSessionView).showProgress(false);
    }

    @Test
    public void shouldShowErrorOnUpdateFailure() {
        Session session = createSessionPresenter.getSession();

        String isoDateNow = DateUtils.formatDateToIso(LocalDateTime.now());
        String isoDateThen = DateUtils.formatDateToIso(LocalDateTime.MAX);
        session.setStartsAt(isoDateNow);
        session.setEndsAt(isoDateThen);

        when(sessionRepository.updateSession(createSessionPresenter.getSession())).thenReturn(Observable.error(new Throwable(ERROR)));

        createSessionPresenter.updateSession(TRACK_ID, EVENT_ID);

        InOrder inOrder = Mockito.inOrder(createSessionView);

        inOrder.verify(createSessionView).showProgress(true);
        inOrder.verify(createSessionView).showError(ERROR);
        inOrder.verify(createSessionView).showProgress(false);
    }
}
