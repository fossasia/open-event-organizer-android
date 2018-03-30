package org.fossasia.openevent.app.core.event.copyright;

import org.fossasia.openevent.app.common.ContextManager;
import org.fossasia.openevent.app.common.mvp.presenter.BasePresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.models.Copyright;
import org.fossasia.openevent.app.data.repository.ICopyrightRepository;
import org.fossasia.openevent.app.utils.StringUtils;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneous;

public class CreateCopyrightPresenter extends BasePresenter<ICreateCopyrightView> {

    private final ICopyrightRepository copyrightRepository;
    private final Copyright copyright = new Copyright();

    @Inject
    public CreateCopyrightPresenter(ICopyrightRepository copyrightRepository) {
        this.copyrightRepository = copyrightRepository;
    }

    @Override
    public void start() {
        // Nothing to do
    }

    public Copyright getCopyright() {
        return copyright;
    }

    protected void nullifyEmptyFields(Copyright copyright) {
        copyright.setHolderUrl(StringUtils.emptyToNull(copyright.getHolderUrl()));
        copyright.setLicence(StringUtils.emptyToNull(copyright.getLicence()));
        copyright.setLicenceUrl(StringUtils.emptyToNull(copyright.getLicenceUrl()));
        copyright.setYear(StringUtils.emptyToNull(copyright.getYear()));
        copyright.setLogoUrl(StringUtils.emptyToNull(copyright.getLogoUrl()));
    }

    public void createCopyright() {
        nullifyEmptyFields(copyright);

        copyright.setEvent(ContextManager.getSelectedEvent());

        copyrightRepository.createCopyright(copyright)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneous(getView()))
            .subscribe(createdTicket -> {
                getView().onSuccess("Copyright Created");
                getView().dismiss();
            }, Logger::logError);
    }
}
