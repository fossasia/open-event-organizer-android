package org.fossasia.openevent.app.core.faq.list;

import com.raizlabs.android.dbflow.structure.BaseModel;

import org.fossasia.openevent.app.common.mvp.presenter.AbstractDetailPresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.db.DbFlowDatabaseChangeListener;
import org.fossasia.openevent.app.data.db.DatabaseChangeListener;
import org.fossasia.openevent.app.data.faq.Faq;
import org.fossasia.openevent.app.data.faq.FaqRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.emptiable;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneousRefresh;

public class FaqListPresenter extends AbstractDetailPresenter<Long, FaqListView> {

    private final List<Faq> faqs = new ArrayList<>();
    private final FaqRepository faqRepository;
    private final DatabaseChangeListener<Faq> faqChangeListener;

    @Inject
    public FaqListPresenter(FaqRepository faqRepository, DatabaseChangeListener<Faq> faqChangeListener) {
        this.faqRepository = faqRepository;
        this.faqChangeListener = faqChangeListener;
    }

    @Override
    public void start() {
        loadFaqs(false);
        listenChanges();
    }

    @Override
    public void detach() {
        super.detach();
        faqChangeListener.stopListening();
    }

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

    private void listenChanges() {
        faqChangeListener.startListening();
        faqChangeListener.getNotifier()
            .compose(dispose(getDisposable()))
            .map(DbFlowDatabaseChangeListener.ModelChange::getAction)
            .filter(action -> action.equals(BaseModel.Action.INSERT))
            .subscribeOn(Schedulers.io())
            .subscribe(faqModelChange -> loadFaqs(false), Logger::logError);
    }

    public List<Faq> getFaqs() {
        return faqs;
    }
}
