package com.eventyay.organizer.core.event.copyright.update;

import com.eventyay.organizer.common.ContextManager;
import com.eventyay.organizer.common.mvp.presenter.AbstractBasePresenter;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.copyright.Copyright;
import com.eventyay.organizer.data.copyright.CopyrightRepository;
import com.eventyay.organizer.utils.StringUtils;

import javax.inject.Inject;

import static com.eventyay.organizer.common.rx.ViewTransformers.dispose;
import static com.eventyay.organizer.common.rx.ViewTransformers.progressiveErroneous;

public class UpdateCopyrightPresenter extends AbstractBasePresenter<UpdateCopyrightView> {

    private final CopyrightRepository copyrightRepository;
    private Copyright copyright;
    private static final int YEAR_LENGTH = 4;

    @Inject
    public UpdateCopyrightPresenter(CopyrightRepository copyrightRepository) {
        this.copyrightRepository = copyrightRepository;
    }

    @Override
    public void start() {
        // Nothing to do
    }

    public Long getParentEventId() {
        return ContextManager.getSelectedEvent().id;
    }

    private void nullifyEmptyFields(Copyright copyright) {
        copyright.setHolderUrl(StringUtils.emptyToNull(copyright.getHolderUrl()));
        copyright.setLicence(StringUtils.emptyToNull(copyright.getLicence()));
        copyright.setLicenceUrl(StringUtils.emptyToNull(copyright.getLicenceUrl()));
        copyright.setYear(StringUtils.emptyToNull(copyright.getYear()));
        copyright.setLogoUrl(StringUtils.emptyToNull(copyright.getLogoUrl()));
    }

    protected boolean verifyYear(Copyright copyright) {
        if (copyright.getYear() == null)
            return true;
        else if (copyright.getYear().length() == YEAR_LENGTH)
            return true;
        else {
            getView().showError("Please Enter a Valid Year");
            return false;
        }
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

        if (!verifyYear(copyright))
            return;

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
