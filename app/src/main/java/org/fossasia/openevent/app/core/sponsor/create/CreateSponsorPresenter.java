package org.fossasia.openevent.app.core.sponsor.create;

import org.fossasia.openevent.app.common.ContextManager;
import org.fossasia.openevent.app.common.mvp.presenter.AbstractBasePresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.sponsor.Sponsor;
import org.fossasia.openevent.app.data.sponsor.SponsorRepository;

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

    public void createSponsor() {
        sponsor.setEvent(ContextManager.getSelectedEvent());

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
