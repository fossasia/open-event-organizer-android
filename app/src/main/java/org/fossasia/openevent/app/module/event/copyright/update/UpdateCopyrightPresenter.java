package org.fossasia.openevent.app.module.event.copyright.update;

import org.fossasia.openevent.app.common.app.ContextManager;
import org.fossasia.openevent.app.common.app.lifecycle.presenter.BasePresenter;
import org.fossasia.openevent.app.common.app.rx.Logger;
import org.fossasia.openevent.app.common.data.models.Copyright;
import org.fossasia.openevent.app.common.data.repository.contract.ICopyrightRepository;
import org.fossasia.openevent.app.common.utils.core.StringUtils;
import org.fossasia.openevent.app.module.event.copyright.update.contract.IUpdateCopyrightPresenter;
import org.fossasia.openevent.app.module.event.copyright.update.contract.IUpdateCopyrightView;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.progressiveErroneous;

public class UpdateCopyrightPresenter extends BasePresenter<IUpdateCopyrightView> implements IUpdateCopyrightPresenter {

    private final ICopyrightRepository copyrightRepository;

    @Inject
    public UpdateCopyrightPresenter(ICopyrightRepository copyrightRepository) {
        this.copyrightRepository = copyrightRepository;
    }

    @Override
    public void start() {
        // Nothing to do
    }

    private void nullifyEmptyFields(Copyright copyright) {
        copyright.setHolderUrl(StringUtils.emptyToNull(copyright.getHolderUrl()));
        copyright.setLicence(StringUtils.emptyToNull(copyright.getLicence()));
        copyright.setLicenceUrl(StringUtils.emptyToNull(copyright.getLicenceUrl()));
        copyright.setYear(StringUtils.emptyToNull(copyright.getYear()));
        copyright.setLogoUrl(StringUtils.emptyToNull(copyright.getLogoUrl()));
    }

    @Override
    public void updateCopyright(Copyright copyright) {
        nullifyEmptyFields(copyright);

        copyright.setEvent(ContextManager.getSelectedEvent());

        copyrightRepository.updateCopyright(copyright)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneous(getView()))
            .subscribe(updatedTicket -> {
                getView().onSuccess("Copyright Updated");
                getView().dismiss();
            }, Logger::logError);
    }
}
