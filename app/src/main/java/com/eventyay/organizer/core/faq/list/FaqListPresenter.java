package com.eventyay.organizer.core.faq.list;

import androidx.databinding.ObservableBoolean;

import com.raizlabs.android.dbflow.structure.BaseModel;

import com.eventyay.organizer.common.mvp.presenter.AbstractDetailPresenter;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.db.DatabaseChangeListener;
import com.eventyay.organizer.data.db.DbFlowDatabaseChangeListener;
import com.eventyay.organizer.data.faq.Faq;
import com.eventyay.organizer.data.faq.FaqRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import static com.eventyay.organizer.common.rx.ViewTransformers.dispose;
import static com.eventyay.organizer.common.rx.ViewTransformers.disposeCompletable;
import static com.eventyay.organizer.common.rx.ViewTransformers.emptiable;
import static com.eventyay.organizer.common.rx.ViewTransformers.progressiveErroneous;
import static com.eventyay.organizer.common.rx.ViewTransformers.progressiveErroneousCompletable;
import static com.eventyay.organizer.common.rx.ViewTransformers.progressiveErroneousRefresh;

public class FaqListPresenter extends AbstractDetailPresenter<Long, FaqListView> {

    private final List<Faq> faqs = new ArrayList<>();
    private Faq previousFaq = new Faq();
    private final FaqRepository faqRepository;
    private final DatabaseChangeListener<Faq> faqChangeListener;
    private final Map<Faq, ObservableBoolean> selectedMap = new ConcurrentHashMap<>();
    private boolean isContextualModeActive;

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
                selectedMap.remove(faq);
                Logger.logSuccess(faq);
            }, Logger::logError);
    }

    public void deleteSelectedFaq() {
        Observable.fromIterable(selectedMap.entrySet())
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneous(getView()))
            .subscribe(entry -> {
                if (entry.getValue().get()) {
                    deleteFaq(entry.getKey());
                }
                loadFaqs(false);
                getView().showMessage("FAQs Deleted Successfully");
            }, Logger::logError);
    }

    public void unselectFaq(Faq faq) {
        if (faq != null && selectedMap.containsKey(faq))
            selectedMap.get(faq).set(false);
    }

    public void resetToDefaultState() {
        isContextualModeActive = false;
        unSelectFaqList();
        getView().exitContextualMenuMode();
    }

    public void onSingleSelect(Faq currentFaq) {
        if (isContextualModeActive) {
            if (countSelected() == 1 && getFaqSelected(currentFaq).get()) {
                selectedMap.get(currentFaq).set(false);
                getView().exitContextualMenuMode();
            } else if (getFaqSelected(currentFaq).get()) {
                selectedMap.get(currentFaq).set(false);
            } else {
                previousFaq = currentFaq;
                selectedMap.get(currentFaq).set(true);
            }
        }
    }

    public void onLongSelect(Faq currentFaq) {
        if (!isContextualModeActive) {
            getView().enterContextualMenuMode();
        }
        if (!previousFaq.equals(currentFaq)) {
            unselectFaq(previousFaq);
        }
        selectedMap.get(currentFaq).set(true);
        previousFaq = currentFaq;
        isContextualModeActive = true;
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

    public void unSelectFaqList() {
        for (Faq faq  : selectedMap.keySet()) {
            unselectFaq(faq);
        }
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis") // Inevitable DD anomaly
    private int countSelected() {
        int count = 0;
        for (Faq faq : selectedMap.keySet()) {
            if (selectedMap.get(faq).get())
                count++;
        }
        return count;
    }
}

