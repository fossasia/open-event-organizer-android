package org.fossasia.openevent.app.module.faq.list;

import org.fossasia.openevent.app.common.app.lifecycle.presenter.BaseDetailPresenter;
import org.fossasia.openevent.app.common.app.rx.Logger;
import org.fossasia.openevent.app.common.data.models.Faq;
import org.fossasia.openevent.app.common.data.repository.contract.IFaqRepository;
import org.fossasia.openevent.app.module.faq.list.contract.IFaqListPresenter;
import org.fossasia.openevent.app.module.faq.list.contract.IFaqListView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.emptiable;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.progressiveErroneousRefresh;

public class FaqListPresenter extends BaseDetailPresenter<Long, IFaqListView> implements IFaqListPresenter {

    private final List<Faq> faqs = new ArrayList<>();
    private final IFaqRepository faqRepository;

    @Inject
    public FaqListPresenter(IFaqRepository faqRepository) {
        this.faqRepository = faqRepository;
    }

    @Override
    public void start() {
        loadFaqs(false);
    }

    @Override
    public void loadFaqs(boolean forceReload) {
        getFaqSource(forceReload)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneousRefresh(getView(), forceReload))
            .toList()
            .compose(emptiable(getView(), faqs))
            .subscribe(Logger::logSuccess, Logger::logError);
    }

    private Observable<Faq> getFaqSource(boolean forceReload) {
        if (!forceReload && !faqs.isEmpty() && isRotated())
            return Observable.fromIterable(faqs);
        else {
            return faqRepository.getFaqs(getId(), forceReload);
        }
    }

    @Override
    public List<Faq> getFaqs() {
        return faqs;
    }
}
