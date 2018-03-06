package org.fossasia.openevent.app.module.faq.create;

import org.fossasia.openevent.app.common.app.ContextManager;
import org.fossasia.openevent.app.common.app.lifecycle.presenter.BasePresenter;
import org.fossasia.openevent.app.common.app.rx.Logger;
import org.fossasia.openevent.app.common.data.models.Faq;
import org.fossasia.openevent.app.common.data.repository.contract.IFaqRepository;
import org.fossasia.openevent.app.module.faq.create.contract.ICreateFaqPresenter;
import org.fossasia.openevent.app.module.faq.create.contract.ICreateFaqView;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.progressiveErroneous;

public class CreateFaqPresenter extends BasePresenter<ICreateFaqView> implements ICreateFaqPresenter {

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

    @Override
    public Faq getFaq() {
        return faq;
    }

    @Override
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
