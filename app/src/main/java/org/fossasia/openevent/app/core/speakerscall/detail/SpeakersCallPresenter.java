package org.fossasia.openevent.app.core.speakerscall.detail;

import com.raizlabs.android.dbflow.structure.BaseModel;

import org.fossasia.openevent.app.common.mvp.presenter.AbstractDetailPresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.db.DatabaseChangeListener;
import org.fossasia.openevent.app.data.db.DbFlowDatabaseChangeListener;
import org.fossasia.openevent.app.data.speakerscall.SpeakersCall;
import org.fossasia.openevent.app.data.speakerscall.SpeakersCallRepository;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneousRefresh;

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
            .filter(action -> action.equals(BaseModel.Action.INSERT))
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
}
