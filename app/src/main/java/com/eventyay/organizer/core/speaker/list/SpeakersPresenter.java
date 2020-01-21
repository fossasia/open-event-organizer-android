package com.eventyay.organizer.core.speaker.list;

import com.eventyay.organizer.common.mvp.presenter.AbstractDetailPresenter;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.db.DatabaseChangeListener;
import com.eventyay.organizer.data.db.DbFlowDatabaseChangeListener;
import com.eventyay.organizer.data.speaker.Speaker;
import com.eventyay.organizer.data.speaker.SpeakerRepository;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import static com.eventyay.organizer.common.rx.ViewTransformers.dispose;
import static com.eventyay.organizer.common.rx.ViewTransformers.emptiable;
import static com.eventyay.organizer.common.rx.ViewTransformers.progressiveErroneousRefresh;

public class SpeakersPresenter extends AbstractDetailPresenter<Long, SpeakersView> {

    private final List<Speaker> speakers = new ArrayList<>();
    private final SpeakerRepository speakerRepository;
    private final DatabaseChangeListener<Speaker> speakersChangeListener;

    @Inject
    public SpeakersPresenter(SpeakerRepository speakerRepository, DatabaseChangeListener<Speaker> speakersChangeListener) {
        this.speakerRepository = speakerRepository;
        this.speakersChangeListener = speakersChangeListener;
    }

    @Override
    public void start() {
        loadSpeakers(false);
        listenChanges();
    }

    @Override
    public void detach() {
        super.detach();
        speakersChangeListener.stopListening();
    }

    public void loadSpeakers(boolean forceReload) {
        getSpeakerSource(forceReload)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneousRefresh(getView(), forceReload))
            .toList()
            .compose(emptiable(getView(), speakers))
            .subscribe(Logger::logSuccess, Logger::logError);
    }

    private Observable<Speaker> getSpeakerSource(boolean forceReload) {
        if (!forceReload && !speakers.isEmpty() && isRotated())
            return Observable.fromIterable(speakers);
        else {
            return speakerRepository.getSpeakers(getId(), forceReload);
        }
    }

    private void listenChanges() {
        speakersChangeListener.startListening();
        speakersChangeListener.getNotifier()
            .compose(dispose(getDisposable()))
            .map(DbFlowDatabaseChangeListener.ModelChange::getAction)
            .filter(action -> action.equals(BaseModel.Action.INSERT))
            .subscribeOn(Schedulers.io())
            .subscribe(speakerModelChange -> loadSpeakers(false), Logger::logError);
    }

    public List<Speaker> getSpeakers() {
        return speakers;
    }

    public void click(long speakerId) {
        getView().openSpeakersDetailFragment(speakerId);
    }
}
