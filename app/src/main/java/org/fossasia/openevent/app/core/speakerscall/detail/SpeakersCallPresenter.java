package org.fossasia.openevent.app.core.speakerscall.detail;

import org.fossasia.openevent.app.common.mvp.presenter.AbstractDetailPresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.speakerscall.SpeakersCall;
import org.fossasia.openevent.app.data.speakerscall.SpeakersCallRepository;

import javax.inject.Inject;

import io.reactivex.Observable;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneousRefresh;

public class SpeakersCallPresenter extends AbstractDetailPresenter<Long, SpeakersCallView> {

    private SpeakersCall speakersCall;
    private final SpeakersCallRepository speakersCallRepository;

    @Inject
    public SpeakersCallPresenter(SpeakersCallRepository speakersCallRepository) {
        this.speakersCallRepository = speakersCallRepository;
    }

    @Override
    public void start() {
        loadSpeakersCall(false);
    }

    public void loadSpeakersCall(boolean forceReload) {
        getSpeakersCallSource(forceReload)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneousRefresh(getView(), forceReload))
            .doFinally(() -> getView().showResult(speakersCall))
            .subscribe(speakersCall -> this.speakersCall = speakersCall, Logger::logError);
    }

    private Observable<SpeakersCall> getSpeakersCallSource(boolean forceReload) {
        if (!forceReload && isRotated() && speakersCall != null)
            return Observable.just(speakersCall);
        else {
            return speakersCallRepository.getSpeakersCall(getId(), forceReload);
        }
    }
}
