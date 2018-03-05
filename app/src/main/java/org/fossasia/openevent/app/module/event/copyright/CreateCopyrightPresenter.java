package org.fossasia.openevent.app.module.event.copyright;

import org.fossasia.openevent.app.common.app.ContextManager;
import org.fossasia.openevent.app.common.app.lifecycle.presenter.BasePresenter;
import org.fossasia.openevent.app.common.app.rx.Logger;
import org.fossasia.openevent.app.common.data.models.Copyright;
import org.fossasia.openevent.app.common.data.repository.contract.ICopyrightRepository;
import org.fossasia.openevent.app.common.utils.core.StringUtils;
import org.fossasia.openevent.app.module.event.copyright.contract.ICreateCopyrightPresenter;
import org.fossasia.openevent.app.module.event.copyright.contract.ICreateCopyrightView;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.progressiveErroneous;

public class CreateCopyrightPresenter extends BasePresenter<ICreateCopyrightView> implements ICreateCopyrightPresenter {

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

    @Override
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

    @Override
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
