package com.eventyay.organizer.core.session.create;

import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import com.eventyay.organizer.data.session.Session;
import com.eventyay.organizer.data.session.SessionRepository;
import com.eventyay.organizer.utils.DateUtils;
import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
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

@RunWith(JUnit4.class)
public class CreateSessionViewModelTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule public TestRule rule = new InstantTaskExecutorRule();

    @Mock private SessionRepository sessionRepository;

    @Mock Observer<String> error;
    @Mock Observer<Boolean> progress;
    @Mock Observer<String> success;
    @Mock Observer<Void> dismiss;

    private CreateSessionViewModel createSessionViewModel;
    private static final Session SESSION = Session.builder().id(2L).title("dd").build();
    private static final String ERROR = "Error";
    private static final long EVENT_ID = 5L;
    private static final long TRACK_ID = 5L;

    @Before
    public void setUp() {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(
                schedulerCallable -> Schedulers.trampoline());

        createSessionViewModel = new CreateSessionViewModel(sessionRepository);
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

        InOrder inOrder = Mockito.inOrder(sessionRepository, error);

        createSessionViewModel.getError().observeForever(error);

        createSessionViewModel.createSession(TRACK_ID, EVENT_ID);

        inOrder.verify(error).onChanged(anyString());
        inOrder.verify(sessionRepository, never()).createSession(any());
    }

    @Test
    public void shouldAcceptCorrectDates() {
        Session session = createSessionViewModel.getSession();

        when(sessionRepository.createSession(session)).thenReturn(Observable.empty());

        String isoDateNow = DateUtils.formatDateToIso(LocalDateTime.now());
        String isoDateThen = DateUtils.formatDateToIso(LocalDateTime.MAX);
        session.setStartsAt(isoDateNow);
        session.setEndsAt(isoDateThen);

        InOrder inOrder = Mockito.inOrder(sessionRepository, error);

        createSessionViewModel.getError().observeForever(error);

        createSessionViewModel.createSession(TRACK_ID, EVENT_ID);

        inOrder.verify(error, never()).onChanged(anyString());
        inOrder.verify(sessionRepository).createSession(session);
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
        Session session = createSessionViewModel.getSession();

        String isoDateNow = DateUtils.formatDateToIso(LocalDateTime.now());
        String isoDateThen = DateUtils.formatDateToIso(LocalDateTime.MAX);
        session.setStartsAt(isoDateNow);
        session.setEndsAt(isoDateThen);

        when(sessionRepository.createSession(createSessionViewModel.getSession()))
                .thenReturn(Observable.just(SESSION));

        InOrder inOrder = Mockito.inOrder(progress, success, dismiss);

        createSessionViewModel.getProgress().observeForever(progress);
        createSessionViewModel.getSuccess().observeForever(success);
        createSessionViewModel.getDismiss().observeForever(dismiss);

        createSessionViewModel.createSession(TRACK_ID, EVENT_ID);

        inOrder.verify(progress).onChanged(true);
        inOrder.verify(success).onChanged(anyString());
        inOrder.verify(dismiss).onChanged(null);
        inOrder.verify(progress).onChanged(false);
    }

    @Test
    public void shouldShowErrorOnFailure() {
        Session session = createSessionViewModel.getSession();

        String isoDateNow = DateUtils.formatDateToIso(LocalDateTime.now());
        String isoDateThen = DateUtils.formatDateToIso(LocalDateTime.MAX);
        session.setStartsAt(isoDateNow);
        session.setEndsAt(isoDateThen);

        when(sessionRepository.createSession(createSessionViewModel.getSession()))
                .thenReturn(Observable.error(new Throwable(ERROR)));

        InOrder inOrder = Mockito.inOrder(progress, error);

        createSessionViewModel.getProgress().observeForever(progress);
        createSessionViewModel.getError().observeForever(error);

        createSessionViewModel.createSession(TRACK_ID, EVENT_ID);

        inOrder.verify(progress).onChanged(true);
        inOrder.verify(error).onChanged(anyString());
        inOrder.verify(progress).onChanged(false);
    }

    @Test
    public void shouldShowSuccessOnUpdated() {
        Session session = createSessionViewModel.getSession();

        String isoDateNow = DateUtils.formatDateToIso(LocalDateTime.now());
        String isoDateThen = DateUtils.formatDateToIso(LocalDateTime.MAX);
        session.setStartsAt(isoDateNow);
        session.setEndsAt(isoDateThen);

        when(sessionRepository.updateSession(createSessionViewModel.getSession()))
                .thenReturn(Observable.just(SESSION));

        InOrder inOrder = Mockito.inOrder(progress, success, dismiss);

        createSessionViewModel.getProgress().observeForever(progress);
        createSessionViewModel.getSuccess().observeForever(success);
        createSessionViewModel.getDismiss().observeForever(dismiss);

        createSessionViewModel.updateSession(TRACK_ID, EVENT_ID);

        inOrder.verify(progress).onChanged(true);
        inOrder.verify(success).onChanged(anyString());
        inOrder.verify(dismiss).onChanged(null);
        inOrder.verify(progress).onChanged(false);
    }

    @Test
    public void shouldShowErrorOnUpdateFailure() {
        Session session = createSessionViewModel.getSession();

        String isoDateNow = DateUtils.formatDateToIso(LocalDateTime.now());
        String isoDateThen = DateUtils.formatDateToIso(LocalDateTime.MAX);
        session.setStartsAt(isoDateNow);
        session.setEndsAt(isoDateThen);

        when(sessionRepository.updateSession(createSessionViewModel.getSession()))
                .thenReturn(Observable.error(new Throwable(ERROR)));

        InOrder inOrder = Mockito.inOrder(progress, error);

        createSessionViewModel.getProgress().observeForever(progress);
        createSessionViewModel.getError().observeForever(error);

        createSessionViewModel.updateSession(TRACK_ID, EVENT_ID);

        inOrder.verify(progress).onChanged(true);
        inOrder.verify(error).onChanged(anyString());
        inOrder.verify(progress).onChanged(false);
    }
}
