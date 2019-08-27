package com.eventyay.organizer.core.track.create;

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
import io.reactivex.disposables.CompositeDisposable;
import java.util.Random;
import javax.inject.Inject;

public class CreateTrackViewModel extends ViewModel {
    private final TrackRepository trackRepository;
    private final Track track = new Track();
    private int colorRed, colorGreen, colorBlue, colorRGB;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final SingleEventLiveData<Boolean> progress = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> error = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> success = new SingleEventLiveData<>();
    private final SingleEventLiveData<Void> dismiss = new SingleEventLiveData<>();
    private final SingleEventLiveData<Track> trackLiveData = new SingleEventLiveData<>();

    @Inject
    public CreateTrackViewModel(TrackRepository trackRepository) {
        this.trackRepository = trackRepository;

        track.setColor(getRandomColor());
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

    private void nullifyEmptyFields(Track track) {
        track.setDescription(StringUtils.emptyToNull(track.getDescription()));
    }

    public void createTrack() {
        nullifyEmptyFields(track);

        long eventId = ContextManager.getSelectedEvent().getId();
        Event event = new Event();
        event.setId(eventId);
        track.setEvent(event);

        compositeDisposable.add(
                trackRepository
                        .createTrack(track)
                        .doOnSubscribe(disposable -> progress.setValue(true))
                        .doFinally(() -> progress.setValue(false))
                        .subscribe(
                                createdTrack -> {
                                    success.setValue("Track Created");
                                    dismiss.call();
                                },
                                throwable ->
                                        error.setValue(
                                                ErrorUtils.getMessage(throwable).toString())));
    }

    public LiveData<Track> getTrackLiveData() {
        return trackLiveData;
    }

    public String getRandomColor() {
        Random random = new Random();
        colorRed = random.nextInt(255);
        colorGreen = random.nextInt(255);
        colorBlue = random.nextInt(255);
        colorRGB = Color.rgb(colorRed, colorGreen, colorBlue);
        return String.format("#%06X", 0xFFFFFF & colorRGB);
    }

    public int getRed() {
        return colorRed;
    }

    public int getGreen() {
        return colorGreen;
    }

    public int getBlue() {
        return colorBlue;
    }

    public int getColorRGB() {
        return colorRGB;
    }
}
