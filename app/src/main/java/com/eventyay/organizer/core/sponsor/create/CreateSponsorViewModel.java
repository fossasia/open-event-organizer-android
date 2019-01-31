package com.eventyay.organizer.core.sponsor.create;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;

import com.eventyay.organizer.common.ContextManager;
import com.eventyay.organizer.common.livedata.SingleEventLiveData;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.sponsor.Sponsor;
import com.eventyay.organizer.data.sponsor.SponsorRepository;
import com.eventyay.organizer.utils.ErrorUtils;
import com.eventyay.organizer.utils.StringUtils;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class CreateSponsorViewModel extends ViewModel {

    private final SponsorRepository sponsorRepository;
    private Sponsor sponsor = new Sponsor();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final SingleEventLiveData<Boolean> progress = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> error = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> success = new SingleEventLiveData<>();
    private final SingleEventLiveData<Void> dismiss = new SingleEventLiveData<>();
    private final SingleEventLiveData<Sponsor> sponsorLiveData = new SingleEventLiveData<>();

    @Inject
    public CreateSponsorViewModel(SponsorRepository sponsorRepository) {
        this.sponsorRepository = sponsorRepository;
    }

    public Sponsor getSponsor() {
        return sponsor;
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

    @VisibleForTesting
    protected void nullifyEmptyFields(Sponsor sponsor) {
        sponsor.setDescription(StringUtils.emptyToNull(sponsor.getDescription()));
        sponsor.setLogoUrl(StringUtils.emptyToNull(sponsor.getLogoUrl()));
        sponsor.setUrl(StringUtils.emptyToNull(sponsor.getUrl()));
        sponsor.setType(StringUtils.emptyToNull(sponsor.getType()));
    }

    public void createSponsor() {
        long eventId = ContextManager.getSelectedEvent().getId();
        Event event = new Event();
        event.setId(eventId);
        sponsor.setEvent(event);

        nullifyEmptyFields(sponsor);


            compositeDisposable.add(
                sponsorRepository
                    .createSponsor(sponsor)
                    .doOnSubscribe(disposable -> progress.setValue(true))
                    .doFinally(() -> progress.setValue(false))
                    .subscribe(createdSponsor -> {
                        success.setValue("Sponsor Created");
                        dismiss.call();
                    }, throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
        }

    public void loadSponsor(long sponsorId) {
        compositeDisposable.add(
            sponsorRepository
                .getSponsor(sponsorId, false)
                .doOnSubscribe(disposable -> progress.setValue(true))
                .doFinally(() -> {
                    progress.setValue(false);
                    sponsorLiveData.setValue(sponsor);
                }).subscribe(loadedSponsor -> this.sponsor = loadedSponsor, Logger::logError));
    }

    public LiveData<Sponsor> getSponsorLiveData() {
        return sponsorLiveData;
    }

    public void updateSponsor() {
        nullifyEmptyFields(sponsor);

        long eventId = ContextManager.getSelectedEvent().getId();
        Event event = new Event();
        event.setId(eventId);
        sponsor.setEvent(event);

        compositeDisposable.add(
            sponsorRepository
                .updateSponsor(sponsor)
                .doOnSubscribe(disposable -> progress.setValue(true))
                .doFinally(() -> progress.setValue(false))
                .subscribe(updatedSponsor -> {
                    success.setValue("Sponsor Updated");
                    dismiss.call();
                }, throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
    }
}
