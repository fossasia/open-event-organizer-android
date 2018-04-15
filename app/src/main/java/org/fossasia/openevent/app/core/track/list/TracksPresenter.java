package org.fossasia.openevent.app.core.track.list;


import android.graphics.Color;

import com.raizlabs.android.dbflow.structure.BaseModel;

import org.fossasia.openevent.app.common.mvp.presenter.AbstractDetailPresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.db.DatabaseChangeListener;
import org.fossasia.openevent.app.data.db.DbFlowDatabaseChangeListener;
import org.fossasia.openevent.app.data.tracks.Track;
import org.fossasia.openevent.app.data.tracks.TrackRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.emptiable;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneousRefresh;

public class TracksPresenter extends AbstractDetailPresenter<Long, TracksView> {

    private final List<Track> tracks = new ArrayList<>();
    private final TrackRepository trackRepository;
    private final DatabaseChangeListener<Track> trackChangeListener;

    @Inject
    public TracksPresenter(TrackRepository trackRepository, DatabaseChangeListener<Track> trackChangeListener) {
        this.trackRepository = trackRepository;
        this.trackChangeListener = trackChangeListener;
    }

    @Override
    public void start() {
        loadTracks(false);
        listenChanges();
    }

    @Override
    public void detach() {
        super.detach();
        trackChangeListener.stopListening();
    }

    public void loadTracks(boolean forceReload) {
        getTrackSource(forceReload)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneousRefresh(getView(), forceReload))
            .toList()
            .compose(emptiable(getView(), tracks))
            .subscribe(Logger::logSuccess, Logger::logError);
    }

    private Observable<Track> getTrackSource(boolean forceReload) {
        if (!forceReload && !tracks.isEmpty() && isRotated())
            return Observable.fromIterable(tracks);
        else {
            return trackRepository.getTracks(getId(), forceReload);
        }
    }

    private void listenChanges() {
        trackChangeListener.startListening();
        trackChangeListener.getNotifier()
            .compose(dispose(getDisposable()))
            .map(DbFlowDatabaseChangeListener.ModelChange::getAction)
            .filter(action -> action.equals(BaseModel.Action.INSERT))
            .subscribeOn(Schedulers.io())
            .subscribe(trackModelChange -> loadTracks(false), Logger::logError);
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public static int getTrackColor(Track track) {
        try {
            return Color.parseColor(track.color);
        } catch (IllegalArgumentException illegalArgumentException) {
            return Color.GRAY;
        }
    }

    public static int getTrackFontColor(Track track) {
        try {
            return Color.parseColor(track.fontColor);
        } catch (IllegalArgumentException illegalArgumentException) {
            return Color.BLACK;
        }
    }

    public void openSessions(Long trackId) {
        getView().openSessionsFragment(trackId);
    }
}
