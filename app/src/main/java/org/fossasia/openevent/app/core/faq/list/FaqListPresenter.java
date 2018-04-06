package org.fossasia.openevent.app.core.faq.list;

import com.raizlabs.android.dbflow.structure.BaseModel;

import org.fossasia.openevent.app.common.mvp.presenter.BaseDetailPresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.db.DatabaseChangeListener;
import org.fossasia.openevent.app.data.db.IDatabaseChangeListener;
import org.fossasia.openevent.app.data.models.Faq;
import org.fossasia.openevent.app.data.repository.IFaqRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.disposeCompletable;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.emptiable;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneousCompletable;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneousRefresh;

public class FaqListPresenter extends BaseDetailPresenter<Long, IFaqListView> {

    private final List<Faq> faqs = new ArrayList<>();
    private final IFaqRepository faqRepository;
    private final IDatabaseChangeListener<Faq> faqChangeListener;
    private Faq previousFaq = new Faq();

    @Inject
    public FaqListPresenter(IFaqRepository faqRepository, IDatabaseChangeListener<Faq> faqChangeListener) {
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
            .map(DatabaseChangeListener.ModelChange::getAction)
            .filter(action -> action.equals(BaseModel.Action.INSERT) || action.equals(BaseModel.Action.DELETE))
            .subscribeOn(Schedulers.io())
            .subscribe(faqModelChange -> loadFaqs(false), Logger::logError);
    }

    public List<Faq> getFaqs() {
        return faqs;
    }

    public void deleteFaq(Faq faq) {
        faqRepository
            .deleteFaq(faq.getId())
            .compose(disposeCompletable(getDisposable()))
            .compose(progressiveErroneousCompletable(getView()))
            .subscribe(() -> {
                getView().showMessage("FAQ Deleted Successfully");
                unselectFaq(faq);
            }, Logger::logError);
    }

    public void deleteSelectedFaq() {
        deleteFaq(previousFaq);
    }

    public void unselectFaq(Faq faq) {
        if (faq != null)
            faq.getSelected().set(false);
    }

    public void toolbarDeleteMode(Faq currentFaq) {
        if (!previousFaq.equals(currentFaq))
            unselectFaq(previousFaq);

        previousFaq = currentFaq;
        getView().changeToDeletingMode();
    }

    public void resetToDefaultState() {
        unselectFaq(previousFaq);
        getView().resetToolbar();
    }
}
