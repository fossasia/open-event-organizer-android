package org.fossasia.openevent.app.core.faq.create;

import org.fossasia.openevent.app.common.ContextManager;
import org.fossasia.openevent.app.common.mvp.presenter.BasePresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.models.Faq;
import org.fossasia.openevent.app.data.repository.IFaqRepository;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneous;

public class CreateFaqPresenter extends BasePresenter<ICreateFaqView> {

    private final IFaqRepository faqRepository;
    private final Faq faq = new Faq();

    @Inject
    public CreateFaqPresenter(IFaqRepository faqRepository) {
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
        faq.setEvent(ContextManager.getSelectedEvent());

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
