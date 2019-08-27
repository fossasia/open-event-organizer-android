package com.eventyay.organizer.core.speakerscall.create;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import com.eventyay.organizer.data.speakerscall.SpeakersCall;
import com.eventyay.organizer.data.speakerscall.SpeakersCallRepository;
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
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.threeten.bp.LocalDateTime;

public class CreateSpeakersCallViewModelTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule public TestRule rule = new InstantTaskExecutorRule();

    private static final SpeakersCall SPEAKERS_CALL = new SpeakersCall();
    private static final long EVENT_ID = 5L;
    private static final String ERROR = "Error";

    private CreateSpeakersCallViewModel createSpeakersCallViewModel;

    @Mock private SpeakersCallRepository speakersCallRepository;
    @Mock Observer<String> error;
    @Mock Observer<String> success;

    @Before
    public void setUp() {
        createSpeakersCallViewModel = new CreateSpeakersCallViewModel(speakersCallRepository);
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(
                schedulerCallable -> Schedulers.trampoline());
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldRejectEndAfterStartDates() {
        String isoDate = DateUtils.formatDateToIso(LocalDateTime.now());
        SPEAKERS_CALL.setStartsAt(isoDate);
        SPEAKERS_CALL.setEndsAt(isoDate);

        createSpeakersCallViewModel.setSpeakersCall(SPEAKERS_CALL);
        LiveData<SpeakersCall> speakersCallLiveData = createSpeakersCallViewModel.getSpeakersCall();
        when(speakersCallRepository.createSpeakersCall(speakersCallLiveData.getValue()))
                .thenReturn(Observable.error(new Throwable(ERROR)));

        createSpeakersCallViewModel.getError().observeForever(error);
        createSpeakersCallViewModel.createSpeakersCall(EVENT_ID);

        InOrder inOrder = Mockito.inOrder(speakersCallRepository, error);

        inOrder.verify(speakersCallRepository, never()).createSpeakersCall(any());
        inOrder.verify(error).onChanged("End time should be after start time");
    }

    @Test
    public void shouldRejectWrongFormatDates() {
        SPEAKERS_CALL.setStartsAt("2011/12/03");
        SPEAKERS_CALL.setEndsAt("2011/03/03");

        createSpeakersCallViewModel.setSpeakersCall(SPEAKERS_CALL);
        LiveData<SpeakersCall> speakersCallLiveData = createSpeakersCallViewModel.getSpeakersCall();
        when(speakersCallRepository.createSpeakersCall(speakersCallLiveData.getValue()))
                .thenReturn(Observable.error(new Throwable(ERROR)));

        createSpeakersCallViewModel.getError().observeForever(error);
        createSpeakersCallViewModel.createSpeakersCall(EVENT_ID);

        InOrder inOrder = Mockito.inOrder(speakersCallRepository, error);

        inOrder.verify(speakersCallRepository, never()).createSpeakersCall(any());
        inOrder.verify(error).onChanged("Please enter date in correct format");
    }

    @Test
    public void shouldShowSuccessOnCreated() {
        String isoDateNow = DateUtils.formatDateToIso(LocalDateTime.now());
        String isoDateMax = DateUtils.formatDateToIso(LocalDateTime.MAX);
        SPEAKERS_CALL.setStartsAt(isoDateNow);
        SPEAKERS_CALL.setEndsAt(isoDateMax);

        createSpeakersCallViewModel.setSpeakersCall(SPEAKERS_CALL);
        LiveData<SpeakersCall> speakersCallLiveData = createSpeakersCallViewModel.getSpeakersCall();
        when(speakersCallRepository.createSpeakersCall(speakersCallLiveData.getValue()))
                .thenReturn(Observable.just(SPEAKERS_CALL));

        createSpeakersCallViewModel.getSuccess().observeForever(success);
        createSpeakersCallViewModel.createSpeakersCall(EVENT_ID);

        InOrder inOrder = Mockito.inOrder(speakersCallRepository, success);

        inOrder.verify(speakersCallRepository).createSpeakersCall(SPEAKERS_CALL);
        inOrder.verify(success).onChanged("Speakers Call Created Successfully");
    }

    @Test
    public void shouldShowErrorOnFailure() {
        String isoDateNow = DateUtils.formatDateToIso(LocalDateTime.now());
        String isoDateMax = DateUtils.formatDateToIso(LocalDateTime.MAX);
        SPEAKERS_CALL.setStartsAt(isoDateNow);
        SPEAKERS_CALL.setEndsAt(isoDateMax);

        createSpeakersCallViewModel.setSpeakersCall(SPEAKERS_CALL);
        LiveData<SpeakersCall> speakersCallLiveData = createSpeakersCallViewModel.getSpeakersCall();
        when(speakersCallRepository.createSpeakersCall(speakersCallLiveData.getValue()))
                .thenReturn(Observable.error(new Throwable(ERROR)));

        createSpeakersCallViewModel.getError().observeForever(error);
        createSpeakersCallViewModel.createSpeakersCall(EVENT_ID);

        InOrder inOrder = Mockito.inOrder(speakersCallRepository, error);

        inOrder.verify(speakersCallRepository).createSpeakersCall(SPEAKERS_CALL);
        inOrder.verify(error).onChanged(ERROR);
    }

    @Test
    public void shouldShowSuccessOnUpdated() {
        String isoDateNow = DateUtils.formatDateToIso(LocalDateTime.now());
        String isoDateMax = DateUtils.formatDateToIso(LocalDateTime.MAX);
        SPEAKERS_CALL.setStartsAt(isoDateNow);
        SPEAKERS_CALL.setEndsAt(isoDateMax);

        createSpeakersCallViewModel.setSpeakersCall(SPEAKERS_CALL);
        LiveData<SpeakersCall> speakersCallLiveData = createSpeakersCallViewModel.getSpeakersCall();
        when(speakersCallRepository.updateSpeakersCall(speakersCallLiveData.getValue()))
                .thenReturn(Observable.just(SPEAKERS_CALL));

        createSpeakersCallViewModel.getSuccess().observeForever(success);
        createSpeakersCallViewModel.updateSpeakersCall(EVENT_ID);

        InOrder inOrder = Mockito.inOrder(speakersCallRepository, success);

        inOrder.verify(speakersCallRepository).updateSpeakersCall(SPEAKERS_CALL);
        inOrder.verify(success).onChanged("Speakers Call Updated Successfully");
    }

    @Test
    public void shouldShowErrorOnUpdationFailure() {
        String isoDateNow = DateUtils.formatDateToIso(LocalDateTime.now());
        String isoDateMax = DateUtils.formatDateToIso(LocalDateTime.MAX);
        SPEAKERS_CALL.setStartsAt(isoDateNow);
        SPEAKERS_CALL.setEndsAt(isoDateMax);

        createSpeakersCallViewModel.setSpeakersCall(SPEAKERS_CALL);
        LiveData<SpeakersCall> speakersCallLiveData = createSpeakersCallViewModel.getSpeakersCall();
        when(speakersCallRepository.updateSpeakersCall(speakersCallLiveData.getValue()))
                .thenReturn(Observable.error(new Throwable(ERROR)));

        createSpeakersCallViewModel.getError().observeForever(error);
        createSpeakersCallViewModel.updateSpeakersCall(EVENT_ID);

        InOrder inOrder = Mockito.inOrder(speakersCallRepository, error);

        inOrder.verify(speakersCallRepository).updateSpeakersCall(SPEAKERS_CALL);
        inOrder.verify(error).onChanged(ERROR);
    }
}
