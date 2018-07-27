package com.eventyay.organizer.core.speakerscall.detail;

import com.raizlabs.android.dbflow.structure.BaseModel;

import com.eventyay.organizer.common.mvp.presenter.AbstractDetailPresenter;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.db.DatabaseChangeListener;
import com.eventyay.organizer.data.db.DbFlowDatabaseChangeListener;
import com.eventyay.organizer.data.speakerscall.SpeakersCall;
import com.eventyay.organizer.data.speakerscall.SpeakersCallRepository;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import static com.eventyay.organizer.common.rx.ViewTransformers.dispose;
import static com.eventyay.organizer.common.rx.ViewTransformers.progressiveErroneousRefresh;

public class SpeakersCallPresenter extends AbstractDetailPresenter<Long, SpeakersCallView> {

    private SpeakersCall speakersCall;
    private final SpeakersCallRepository speakersCallRepository;
    private final DatabaseChangeListener<SpeakersCall> speakersCallChangeListener;

    @Inject
    public SpeakersCallPresenter(SpeakersCallRepository speakersCallRepository, DatabaseChangeListener<SpeakersCall> speakersCallChangeListener) {
        this.speakersCallRepository = speakersCallRepository;
        this.speakersCallChangeListener = speakersCallChangeListener;
    }

    @Override
    public void start() {
        loadSpeakersCall(false);
        listenChanges();
    }

    private void listenChanges() {
        speakersCallChangeListener.startListening();
        speakersCallChangeListener.getNotifier()
            .compose(dispose(getDisposable()))
            .map(DbFlowDatabaseChangeListener.ModelChange::getAction)
            .filter(action -> action.equals(BaseModel.Action.INSERT) || action.equals(BaseModel.Action.UPDATE))
            .subscribeOn(Schedulers.io())
            .subscribe(speakersCallModelChange -> loadSpeakersCall(false), Logger::logError);
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

    public long getSpeakersCallId() {
        if (speakersCall == null)
            return -1;
        else
            return speakersCall.getId();
    }

    public SpeakersCall getSpeakersCall() {
        return speakersCall;
    }
}
