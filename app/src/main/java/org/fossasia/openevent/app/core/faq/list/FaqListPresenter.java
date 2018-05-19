package org.fossasia.openevent.app.core.faq.list;

import android.databinding.ObservableBoolean;

import com.raizlabs.android.dbflow.structure.BaseModel;

import org.fossasia.openevent.app.common.mvp.presenter.AbstractDetailPresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.db.DatabaseChangeListener;
import org.fossasia.openevent.app.data.db.DbFlowDatabaseChangeListener;
import org.fossasia.openevent.app.data.faq.Faq;
import org.fossasia.openevent.app.data.faq.FaqRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.disposeCompletable;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.emptiable;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneousCompletable;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneousRefresh;

public class FaqListPresenter extends AbstractDetailPresenter<Long, FaqListView> {

    private final List<Faq> faqs = new ArrayList<>();
    private Faq previousFaq = new Faq();
    private final FaqRepository faqRepository;
    private final DatabaseChangeListener<Faq> faqChangeListener;
    private final Map<Faq, ObservableBoolean> selectedMap = new HashMap<>();

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
        selectedMap.clear();
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
        if (faq != null && selectedMap.containsKey(faq))
            selectedMap.get(faq).set(false);
    }

    public void toolbarDeleteMode(Faq currentFaq) {
        if (!previousFaq.equals(currentFaq))
            unselectFaq(previousFaq);

        selectedMap.get(currentFaq).set(true);
        previousFaq = currentFaq;
        getView().changeToDeletingMode();
    }

    public void resetToDefaultState() {
        unselectFaq(previousFaq);
        getView().resetToolbar();
    }

    public ObservableBoolean getFaqSelected(Faq faq) {
        if (!selectedMap.containsKey(faq)) {
            selectedMap.put(faq, new ObservableBoolean(false));
        }
        return selectedMap.get(faq);
    }

    public Map<Faq, ObservableBoolean> getIsSelected() {
        return selectedMap;
    }
}
