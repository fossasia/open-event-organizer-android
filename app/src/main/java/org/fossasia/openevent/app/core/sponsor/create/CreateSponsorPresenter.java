package org.fossasia.openevent.app.core.sponsor.create;

import android.support.annotation.VisibleForTesting;

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

public class CreateSponsorPresenter extends AbstractBasePresenter<CreateSponsorView> {

    private final SponsorRepository sponsorRepository;
    private final Sponsor sponsor = new Sponsor();

    @Inject
    public CreateSponsorPresenter(SponsorRepository sponsorRepository) {
        this.sponsorRepository = sponsorRepository;
    }

    @Override
    public void start() {
        // nothing to do
    }

    public Sponsor getSponsor() {
        return sponsor;
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

        sponsorRepository
            .createSponsor(sponsor)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneous(getView()))
            .subscribe(createdSponsor -> {
                getView().onSuccess("Sponsor Created");
                getView().dismiss();
            }, Logger::logError);
    }
}
