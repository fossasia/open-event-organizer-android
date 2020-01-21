package com.eventyay.organizer.core.track.update;

import android.graphics.Color;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.eventyay.organizer.common.ContextManager;
import com.eventyay.organizer.common.livedata.SingleEventLiveData;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.tracks.Track;
import com.eventyay.organizer.data.tracks.TrackRepository;
import com.eventyay.organizer.utils.ErrorUtils;
import com.eventyay.organizer.utils.StringUtils;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class UpdateTrackViewModel extends ViewModel {
    private final TrackRepository trackRepository;
    private Track track = new Track();

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final SingleEventLiveData<Boolean> progress = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> error = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> success = new SingleEventLiveData<>();
    private final SingleEventLiveData<Void> dismiss = new SingleEventLiveData<>();
    private final SingleEventLiveData<Track> trackLiveData = new SingleEventLiveData<>();

    @Inject
    public UpdateTrackViewModel(TrackRepository trackRepository) {
        this.trackRepository = trackRepository;
    }

    public Track getTrack() {
        return track;
    }

    public LiveData<Boolean> getProgress() {
        return progress;
    }

    public LiveData<String> getSuccess() {
        return success;
    }

    public LiveData<Void> getDismiss() {
        return dismiss;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Track> getTrackLiveData() {
        return trackLiveData;
    }

    private void nullifyEmptyFields(Track track) {
        track.setDescription(StringUtils.emptyToNull(track.getDescription()));
    }

    public void loadTrack(long trackId) {
        compositeDisposable.add(
            trackRepository
                .getTrack(trackId, false)
                .doOnSubscribe(disposable -> progress.setValue(true))
                .doFinally(() -> {
                    progress.setValue(false);
                    showTrack();
                })
                .subscribe(loadedTrack -> this.track = loadedTrack,
                    throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
    }

    public void updateTrack() {
        nullifyEmptyFields(track);

        long eventId = ContextManager.getSelectedEvent().getId();
        Event event = new Event();
        event.setId(eventId);
        track.setEvent(event);

        compositeDisposable.add(
            trackRepository
                .updateTrack(track)
                .doOnSubscribe(disposable -> progress.setValue(true))
                .doFinally(() -> progress.setValue(false))
                .subscribe(updatedTrack -> {
                    success.setValue("Track Updated");
                    dismiss.call();
                }, throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
    }

    private void showTrack() {
        trackLiveData.setValue(track);
    }


    public int getRed() {
        String colorRed = track.getColor();
        return Integer.valueOf(colorRed.substring(1, 3), 16);
    }

    public int getGreen() {
        String colorGreen = track.getColor();
        return Integer.valueOf(colorGreen.substring(3, 5), 16);
    }

    public int getBlue() {
        String colorBlue = track.getColor();
        return Integer.valueOf(colorBlue.substring(5, 7), 16);
    }

    public int getColorRGB() {
        return Color.rgb(getRed(), getGreen(), getBlue());
    }
}
