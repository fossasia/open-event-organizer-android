package org.fossasia.openevent.app.core.speakerscall.create;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.data.speakerscall.SpeakersCall;
import org.fossasia.openevent.app.data.speakerscall.SpeakersCallRepository;
import org.fossasia.openevent.app.utils.DateUtils;
import org.fossasia.openevent.app.utils.ErrorUtils;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeParseException;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class CreateSpeakersCallViewModel extends ViewModel {

    private final SpeakersCallRepository speakersCallRepository;
    private final SpeakersCall speakersCall = new SpeakersCall();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final MutableLiveData<Boolean> progress = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<String> success = new MutableLiveData<>();

    @Inject
    public CreateSpeakersCallViewModel(SpeakersCallRepository speakersCallRepository) {
        this.speakersCallRepository = speakersCallRepository;
    }

    public void createSpeakersCall(long eventId) {
        if (!verify())
            return;

        Event event = new Event();

        event.setId(eventId);
        speakersCall.setEvent(event);

        compositeDisposable.add(speakersCallRepository.createSpeakersCall(speakersCall)
            .doOnSubscribe(disposable -> progress.setValue(true))
            .doFinally(() -> progress.setValue(false))
            .subscribe(var -> success.setValue("Speakers Call Created Successfully"),
                throwable -> error.setValue(ErrorUtils.getMessage(throwable))));
    }

    public void initialize() {
        LocalDateTime current = LocalDateTime.now();

        String isoDate = DateUtils.formatDateToIso(current);
        speakersCall.setStartsAt(isoDate);
        speakersCall.setEndsAt(isoDate);
    }

    private boolean verify() {
        try {
            ZonedDateTime start = DateUtils.getDate(speakersCall.getStartsAt());
            ZonedDateTime end = DateUtils.getDate(speakersCall.getEndsAt());

            if (!end.isAfter(start)) {
                error.setValue("End time should be after start time");
                return false;
            }
            return true;
        } catch (DateTimeParseException pe) {
            error.setValue("Please enter date in correct format");
            return false;
        }
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<String> getSuccess() {
        return success;
    }

    public LiveData<Boolean> getProgress() {
        return progress;
    }

    public SpeakersCall getSpeakersCall() {
        return speakersCall;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
