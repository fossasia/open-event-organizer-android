package org.fossasia.openevent.app.core.sponsor.update;

import org.fossasia.openevent.app.common.ContextManager;
import org.fossasia.openevent.app.common.mvp.presenter.AbstractBasePresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.data.sponsor.Sponsor;
import org.fossasia.openevent.app.data.sponsor.SponsorRepository;
import org.fossasia.openevent.app.utils.StringUtils;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneous;

public class UpdateSponsorPresenter extends AbstractBasePresenter<UpdateSponsorView> {
    private final SponsorRepository sponsorRepository;
    private Sponsor sponsor;

    @Inject
    public UpdateSponsorPresenter(SponsorRepository sponsorRepository) {
        this.sponsorRepository = sponsorRepository;
    }

    @Override
    public void start() {
        // Nothing to do
    }

    public void loadSponsor(long sponsorId) {
        sponsorRepository
            .getSponsor(sponsorId, false)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneous(getView()))
            .doFinally(this::showSponsor)
            .subscribe(loadedSponsor -> this.sponsor = loadedSponsor, Logger::logError);
    }

    private void showSponsor() {
        getView().setSponsor(sponsor);
    }

    private void nullifyEmptyFields(Sponsor sponsor) {
        sponsor.setDescription(StringUtils.emptyToNull(sponsor.getDescription()));
    }

    public void updateSponsor() {
        nullifyEmptyFields(sponsor);

        long eventId = ContextManager.getSelectedEvent().getId();
        Event event = new Event();
        event.setId(eventId);
        sponsor.setEvent(event);

        sponsorRepository
            .updateSponsor(sponsor)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneous(getView()))
            .subscribe(updatedSponsor -> {
                getView().onSuccess("Sponsor Updated");
                getView().dismiss();
            }, Logger::logError);
    }
}
