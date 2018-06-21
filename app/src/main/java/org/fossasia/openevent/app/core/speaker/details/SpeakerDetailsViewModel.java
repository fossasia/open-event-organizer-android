package org.fossasia.openevent.app.core.speaker.details;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.app.data.session.Session;
import org.fossasia.openevent.app.data.session.SessionRepository;
import org.fossasia.openevent.app.data.speaker.Speaker;
import org.fossasia.openevent.app.data.speaker.SpeakerRepository;
import org.fossasia.openevent.app.utils.ErrorUtils;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;

public class SpeakerDetailsViewModel extends ViewModel {
    private final SpeakerRepository speakerRepository;
    private final SessionRepository sessionRepository;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final MutableLiveData<Speaker> speakerLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Session>> sessionLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> progress = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    @Inject
    public SpeakerDetailsViewModel(SpeakerRepository speakerRepository, SessionRepository sessionRepository) {
        this.speakerRepository = speakerRepository;
        this.sessionRepository = sessionRepository;
        progress.setValue(false);
    }

    protected LiveData<Speaker> getSpeaker(long speakerId, boolean reload) {
        if (speakerLiveData.getValue() != null && !reload)
            return speakerLiveData;

        compositeDisposable.add(speakerRepository.getSpeaker(speakerId, reload)
            .compose(dispose(compositeDisposable))
            .doOnSubscribe(disposable -> progress.setValue(true))
            .doFinally(() -> progress.setValue(false))
            .doOnNext(speaker -> speakerLiveData.setValue(speaker))
            .flatMap(speaker -> sessionRepository.getSessionsUnderSpeaker(speakerId, reload))
            .toList()
            .subscribe(sessionList -> sessionLiveData.setValue(sessionList),
                throwable -> error.setValue(ErrorUtils.getMessage(throwable))));

        return speakerLiveData;
    }

    protected LiveData<List<Session>> getSessionsUnderSpeaker() {
        return sessionLiveData;
    }

    protected LiveData<Boolean> getProgress() {
        return progress;
    }

    protected LiveData<String> getError() {
        return error;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
