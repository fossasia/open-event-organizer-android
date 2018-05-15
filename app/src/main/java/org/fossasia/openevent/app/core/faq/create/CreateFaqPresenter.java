package org.fossasia.openevent.app.core.faq.create;

import org.fossasia.openevent.app.common.ContextManager;
import org.fossasia.openevent.app.common.mvp.presenter.AbstractBasePresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.data.faq.Faq;
import org.fossasia.openevent.app.data.faq.FaqRepository;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneous;

public class CreateFaqPresenter extends AbstractBasePresenter<CreateFaqView> {

    private final FaqRepository faqRepository;
    private final Faq faq = new Faq();

    @Inject
    public CreateFaqPresenter(FaqRepository faqRepository) {
        this.faqRepository = faqRepository;
    }

    @Override
    public void start() {
        // Nothing to do
    }

    public Faq getFaq() {
        return faq;
    }

    public void createFaq() {
        long eventId = ContextManager.getSelectedEvent().getId();
        Event event = new Event();
        event.setId(eventId);
        faq.setEvent(event);

        faqRepository
            .createFaq(faq)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneous(getView()))
            .subscribe(createdFaq -> {
                getView().onSuccess("Faq Created");
                getView().dismiss();
            }, Logger::logError);
    }
}
