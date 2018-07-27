package com.eventyay.organizer.core.event.copyright;

import com.eventyay.organizer.common.ContextManager;
import com.eventyay.organizer.common.mvp.presenter.AbstractBasePresenter;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.copyright.Copyright;
import com.eventyay.organizer.data.copyright.CopyrightRepository;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.utils.StringUtils;

import javax.inject.Inject;

import static com.eventyay.organizer.common.rx.ViewTransformers.dispose;
import static com.eventyay.organizer.common.rx.ViewTransformers.progressiveErroneous;

public class CreateCopyrightPresenter extends AbstractBasePresenter<CreateCopyrightView> {

    private final CopyrightRepository copyrightRepository;
    private final Copyright copyright = new Copyright();
    private static final int YEAR_LENGTH = 4;

    @Inject
    public CreateCopyrightPresenter(CopyrightRepository copyrightRepository) {
        this.copyrightRepository = copyrightRepository;
    }

    @Override
    public void start() {
        // Nothing to do
    }

    public Copyright getCopyright() {
        return copyright;
    }

    private void nullifyEmptyFields(Copyright copyright) {
        copyright.setHolderUrl(StringUtils.emptyToNull(copyright.getHolderUrl()));
        copyright.setLicence(StringUtils.emptyToNull(copyright.getLicence()));
        copyright.setLicenceUrl(StringUtils.emptyToNull(copyright.getLicenceUrl()));
        copyright.setYear(StringUtils.emptyToNull(copyright.getYear()));
        copyright.setLogoUrl(StringUtils.emptyToNull(copyright.getLogoUrl()));
    }

    private boolean verifyYear(Copyright copyright) {
       if (copyright.getYear() == null)
            return true;
       else if (copyright.getYear().length() == YEAR_LENGTH)
           return true;
       else {
           getView().showError("Please Enter a Valid Year");
           return false;
       }
    }

    public void createCopyright() {
        nullifyEmptyFields(copyright);

        if (!verifyYear(copyright))
            return;

        long eventId = ContextManager.getSelectedEvent().getId();
        Event event = new Event();
        event.setId(eventId);
        copyright.setEvent(event);

        copyrightRepository.createCopyright(copyright)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneous(getView()))
            .subscribe(createdTicket -> {
                getView().onSuccess("Copyright Created");
                getView().dismiss();
            }, Logger::logError);
    }
}
