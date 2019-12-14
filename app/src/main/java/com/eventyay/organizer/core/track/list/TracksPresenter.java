package com.eventyay.organizer.core.track.list;


import android.graphics.Color;

import androidx.databinding.ObservableBoolean;

import com.eventyay.organizer.common.mvp.presenter.AbstractDetailPresenter;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.db.DatabaseChangeListener;
import com.eventyay.organizer.data.db.DbFlowDatabaseChangeListener;
import com.eventyay.organizer.data.tracks.Track;
import com.eventyay.organizer.data.tracks.TrackRepository;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import static com.eventyay.organizer.common.rx.ViewTransformers.dispose;
import static com.eventyay.organizer.common.rx.ViewTransformers.disposeCompletable;
import static com.eventyay.organizer.common.rx.ViewTransformers.emptiable;
import static com.eventyay.organizer.common.rx.ViewTransformers.progressiveErroneous;
import static com.eventyay.organizer.common.rx.ViewTransformers.progressiveErroneousRefresh;

public class TracksPresenter extends AbstractDetailPresenter<Long, TracksView> {

    private final List<Track> tracks = new ArrayList<>();
    private final TrackRepository trackRepository;
    private final DatabaseChangeListener<Track> trackChangeListener;
    private final Map<Long, ObservableBoolean> selectedTracks = new ConcurrentHashMap<>();
    private boolean isContextualModeActive;

    private static final int EDITABLE_AT_ONCE = 1;

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
            .filter(action -> action.equals(BaseModel.Action.INSERT) || action.equals(BaseModel.Action.UPDATE) ||
                action.equals(BaseModel.Action.DELETE))
            .subscribeOn(Schedulers.io())
            .subscribe(trackModelChange -> loadTracks(false), Logger::logError);
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public int getTrackColor(Track track) {
        try {
            return Color.parseColor(track.color);
        } catch (IllegalArgumentException illegalArgumentException) {
            return Color.GRAY;
        }
    }

    public int getTrackFontColor(Track track) {
        try {
            return Color.parseColor(track.fontColor);
        } catch (IllegalArgumentException illegalArgumentException) {
            return Color.BLACK;
        }
    }

    public void updateTrack() {
        for (Long id : selectedTracks.keySet()) {
            if (selectedTracks.get(id).get()) {
                getView().openUpdateTrackFragment(id);
                selectedTracks.get(id).set(false);
                resetToolbarToDefaultState();
                return;
            }
        }
    }

    private void deleteTrack(Long trackId) {
        trackRepository
            .deleteTrack(trackId)
            .compose(disposeCompletable(getDisposable()))
            .subscribe(() -> {
                selectedTracks.remove(trackId);
                Logger.logSuccess(trackId);
            }, Logger::logError);
    }

    public void deleteSelectedTracks() {
        Observable.fromIterable(selectedTracks.entrySet())
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneous(getView()))
            .doFinally(() -> {
                getView().showMessage("Tracks Deleted");
                resetToolbarToDefaultState();
            })
            .subscribe(entry -> {
                if (entry.getValue().get()) {
                    deleteTrack(entry.getKey());
                }
            }, Logger::logError);
    }

    public void longClick(Track clickedTrack) {
        if (isContextualModeActive)
            click(clickedTrack.getId());
        else {
            selectedTracks.get(clickedTrack.getId()).set(true);
            isContextualModeActive = true;
            getView().enterContextualMenuMode();
            getView().changeToolbarMode(true, true);
        }
    }

    public void click(Long clickedTrackId) {
        if (isContextualModeActive) {
            if (countSelected() == 1 && isTrackSelected(clickedTrackId).get()) {
                selectedTracks.get(clickedTrackId).set(false);
                resetToolbarToDefaultState();
            } else if (countSelected() == 2 && isTrackSelected(clickedTrackId).get()) {
                selectedTracks.get(clickedTrackId).set(false);
                getView().changeToolbarMode(true, true);
            } else if (isTrackSelected(clickedTrackId).get())
                selectedTracks.get(clickedTrackId).set(false);
            else
                selectedTracks.get(clickedTrackId).set(true);

            if (countSelected() > EDITABLE_AT_ONCE)
                getView().changeToolbarMode(false, true);

        } else
            getView().openSessionsFragment(clickedTrackId);
    }

    public void resetToolbarToDefaultState() {
        isContextualModeActive = false;
        getView().changeToolbarMode(false, false);
        getView().exitContextualMenuMode();
    }

    public ObservableBoolean isTrackSelected(Long trackId) {
        if (!selectedTracks.containsKey(trackId))
            selectedTracks.put(trackId, new ObservableBoolean(false));

        return selectedTracks.get(trackId);
    }

    public void unselectTracksList() {
        for (Long tracksId : selectedTracks.keySet()) {
            if (tracksId != null && selectedTracks.containsKey(tracksId))
                selectedTracks.get(tracksId).set(false);
        }
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis") // Inevitable DD anomaly
    private int countSelected() {
        int count = 0;
        for (Long id : selectedTracks.keySet()) {
            if (selectedTracks.get(id).get())
                count++;
        }
        return count;
    }
}
