package com.eventyay.organizer.core.sessions.create;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.eventyay.organizer.common.ContextManager;
import com.eventyay.organizer.core.session.create.CreateSessionViewModel;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.session.Session;
import com.eventyay.organizer.data.session.SessionRepository;
import com.eventyay.organizer.utils.DateUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class CreateSessionViewModelTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();
    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private Event event;

    @Mock
    Observer<String> error;
    @Mock
    Observer<Boolean> progress;
    @Mock
    Observer<String> success;
    @Mock
    Observer<Void> dismiss;

    private CreateSessionViewModel createSessionViewModel;
    private static final Session SESSION = Session.builder().id(2L).title("dd").build();
    private static final String ERROR = "Error";
    private static final long EVENT_ID = 5L;
    private static final long TRACK_ID = 5L;

    @Before
    public void setUp() {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());

        createSessionViewModel = new CreateSessionViewModel(sessionRepository);
        ContextManager.setSelectedEvent(event);
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldRejectWrongDates() {
        Session session = createSessionViewModel.getSession();

        String isoDate = DateUtils.formatDateToIso(LocalDateTime.now());
        session.setStartsAt(isoDate);
        session.setEndsAt(isoDate);

        createSessionViewModel.createSession(TRACK_ID, EVENT_ID);

        verify(sessionRepository, never()).createSession(any());
    }

    @Test
    public void shouldAcceptCorrectDates() {
        Session session = createSessionViewModel.getSession();

        when(sessionRepository.createSession(session)).thenReturn(Observable.empty());

        String isoDateNow = DateUtils.formatDateToIso(LocalDateTime.now());
        String isoDateThen = DateUtils.formatDateToIso(LocalDateTime.MAX);
        session.setStartsAt(isoDateNow);
        session.setEndsAt(isoDateThen);

        createSessionViewModel.createSession(TRACK_ID, EVENT_ID);

        verify(sessionRepository).createSession(session);
    }

    @Test
    public void shouldNullifyEmptyFields() {
        Session session = createSessionViewModel.getSession();
        when(sessionRepository.createSession(session)).thenReturn(Observable.just(session));

        session.setSlidesUrl("");
        session.setAudioUrl("");
        session.setVideoUrl("");
        session.setSignupUrl("");

        String isoDateNow = DateUtils.formatDateToIso(LocalDateTime.now());
        String isoDateMax = DateUtils.formatDateToIso(LocalDateTime.MAX);
        session.setStartsAt(isoDateNow);
        session.setEndsAt(isoDateMax);

        createSessionViewModel.createSession(TRACK_ID, EVENT_ID);
        assertNull(session.getSlidesUrl());
        assertNull(session.getAudioUrl());
        assertNull(session.getVideoUrl());
        assertNull(session.getSignupUrl());
    }

    @Test
    public void shouldShowSuccessOnCreated() {
        String successString = "Session Created";
        Session session = createSessionViewModel.getSession();

        String isoDateNow = DateUtils.formatDateToIso(LocalDateTime.now());
        String isoDateThen = DateUtils.formatDateToIso(LocalDateTime.MAX);
        session.setStartsAt(isoDateNow);
        session.setEndsAt(isoDateThen);

        when(sessionRepository.createSession(createSessionViewModel.getSession())).thenReturn(Observable.just(SESSION));

        createSessionViewModel.createSession(TRACK_ID, EVENT_ID);
        ContextManager.setSelectedEvent(event);

        InOrder inOrder = Mockito.inOrder(progress, dismiss, success);

        createSessionViewModel.getProgress().observeForever(progress);
        createSessionViewModel.getDismiss().observeForever(dismiss);
        createSessionViewModel.getSuccess().observeForever(success);
        createSessionViewModel.createSession(TRACK_ID, EVENT_ID);

        inOrder.verify(progress).onChanged(true);
        inOrder.verify(success).onChanged(successString);
        inOrder.verify(dismiss).onChanged(null);
        inOrder.verify(progress).onChanged(false);

        ContextManager.setSelectedEvent(null);
    }

    @Test
    public void shouldShowErrorOnFailure() {
        Session session = createSessionViewModel.getSession();
        when(sessionRepository.createSession(createSessionViewModel.getSession())).thenReturn(Observable.error(new Throwable(ERROR)));
        ContextManager.setSelectedEvent(event);

        String isoDateNow = DateUtils.formatDateToIso(LocalDateTime.now());
        String isoDateThen = DateUtils.formatDateToIso(LocalDateTime.MAX);
        session.setStartsAt(isoDateNow);
        session.setEndsAt(isoDateThen);

        createSessionViewModel.createSession(TRACK_ID, EVENT_ID);

        InOrder inOrder = Mockito.inOrder(progress, error);
        createSessionViewModel.getProgress().observeForever(progress);
        createSessionViewModel.getError().observeForever(error);

        inOrder.verify(progress).onChanged(true);
        inOrder.verify(error).onChanged(ERROR);
        inOrder.verify(progress).onChanged(false);

        ContextManager.setSelectedEvent(null);
    }

    @Test
    public void shouldShowSuccessOnUpdated() {
        String successString = "Session Updated";
        Session session = createSessionViewModel.getSession();
        when(sessionRepository.updateSession(createSessionViewModel.getSession())).thenReturn(Observable.just(SESSION));
        ContextManager.setSelectedEvent(event);

        String isoDateNow = DateUtils.formatDateToIso(LocalDateTime.now());
        String isoDateThen = DateUtils.formatDateToIso(LocalDateTime.MAX);
        session.setStartsAt(isoDateNow);
        session.setEndsAt(isoDateThen);

        InOrder inOrder = Mockito.inOrder(progress, dismiss, success);

        createSessionViewModel.getProgress().observeForever(progress);
        createSessionViewModel.getDismiss().observeForever(dismiss);
        createSessionViewModel.getSuccess().observeForever(success);

        createSessionViewModel.updateSession(TRACK_ID, EVENT_ID);

        inOrder.verify(progress).onChanged(true);
        inOrder.verify(success).onChanged(successString);
        inOrder.verify(dismiss).onChanged(null);
        inOrder.verify(progress).onChanged(false);

        ContextManager.setSelectedEvent(null);
    }

    @Test
    public void shouldShowErrorOnUpdateFailure() {
        Session session = createSessionViewModel.getSession();
        when(sessionRepository.updateSession(createSessionViewModel.getSession())).thenReturn(Observable.error(new Throwable(ERROR)));
        ContextManager.setSelectedEvent(event);

        String isoDateNow = DateUtils.formatDateToIso(LocalDateTime.now());
        String isoDateThen = DateUtils.formatDateToIso(LocalDateTime.MAX);
        session.setStartsAt(isoDateNow);
        session.setEndsAt(isoDateThen);

        InOrder inOrder = Mockito.inOrder(progress, error);

        createSessionViewModel.getProgress().observeForever(progress);
        createSessionViewModel.getError().observeForever(error);

        createSessionViewModel.updateSession(TRACK_ID, EVENT_ID);

        inOrder.verify(progress).onChanged(true);
        inOrder.verify(error).onChanged("Error");
        inOrder.verify(progress).onChanged(false);

        ContextManager.setSelectedEvent(null);
    }
}
