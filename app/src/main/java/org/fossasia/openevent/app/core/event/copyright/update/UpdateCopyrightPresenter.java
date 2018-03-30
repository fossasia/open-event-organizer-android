package org.fossasia.openevent.app.core.event.copyright.update;

import org.fossasia.openevent.app.common.ContextManager;
import org.fossasia.openevent.app.common.mvp.presenter.BasePresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.models.Copyright;
import org.fossasia.openevent.app.data.repository.ICopyrightRepository;
import org.fossasia.openevent.app.utils.StringUtils;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneous;

public class UpdateCopyrightPresenter extends BasePresenter<IUpdateCopyrightView> {

    private final ICopyrightRepository copyrightRepository;
    private Copyright copyright;

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

    public void loadCopyright(long eventId) {
        copyrightRepository
            .getCopyright(eventId, false)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneous(getView()))
            .doFinally(this::showCopyright)
            .subscribe(loadedCopyright -> this.copyright = loadedCopyright, Logger::logError);
    }

    private void showCopyright() {
        getView().setCopyright(copyright);
    }

    public void updateCopyright() {
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
