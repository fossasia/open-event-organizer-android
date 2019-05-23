package com.eventyay.organizer.core.faq.list;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.eventyay.organizer.common.ContextManager;
import com.eventyay.organizer.common.livedata.SingleEventLiveData;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.db.DatabaseChangeListener;
import com.eventyay.organizer.data.db.DbFlowDatabaseChangeListener;
import com.eventyay.organizer.data.faq.Faq;
import com.eventyay.organizer.data.faq.FaqRepository;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class FaqListViewModel extends ViewModel {

    private final List<Faq> faqs = new ArrayList<>();
    private Faq previousFaq = new Faq();
    private final FaqRepository faqRepository;
    public final DatabaseChangeListener<Faq> faqChangeListener;
    private final Map<Faq, ObservableBoolean> selectedMap = new ConcurrentHashMap<>();
    private boolean isContextualModeActive;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final SingleEventLiveData<Boolean> progress = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> error = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> success = new SingleEventLiveData<>();
    private final SingleEventLiveData<Void> dismiss = new SingleEventLiveData<>();
    private final SingleEventLiveData<List<Faq>> faqsLiveData = new SingleEventLiveData<>();
    private final SingleEventLiveData<Void> exitContextualMenuMode = new SingleEventLiveData<>();
    private final SingleEventLiveData<Void> enterContextualMenuMode = new SingleEventLiveData<>();

    private long eventId;

    @Inject
    public FaqListViewModel(FaqRepository faqRepository, DatabaseChangeListener<Faq> faqChangeListener) {
        this.faqRepository = faqRepository;
        this.faqChangeListener = faqChangeListener;

        eventId = ContextManager.getSelectedEvent().getId();
        listenChanges();
    }

    public LiveData<Boolean> getProgress() {
        return progress;
    }

    public LiveData<String> getSuccess() {
        return success;
    }

    public LiveData<Void> getDismiss() {
        return dismiss;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<List<Faq>> getFaqsLiveData() {
        return faqsLiveData;
    }

    public LiveData<Void> getExitContextualMenuModeLiveData() {
        return exitContextualMenuMode;
    }

    public LiveData<Void> getEnterContextualMenuModeLiveData() {
        return enterContextualMenuMode;
    }

    public Map<Faq, ObservableBoolean> getSelectedMap() {
        return selectedMap;
    }

    public DatabaseChangeListener<Faq> getFaqChangeListener() {
        return faqChangeListener;
    }

    public void loadFaqs(boolean forceReload) {

        compositeDisposable.add(
            getFaqSource(forceReload)
                .doOnSubscribe(disposable -> progress.setValue(true))
                .doFinally(() -> progress.setValue(false))
                .toList()
                .subscribe(loadedFaqs -> {
                    faqs.clear();
                    faqs.addAll(loadedFaqs);
                    success.setValue("FAQs Loaded Successfully");
                    faqsLiveData.setValue(loadedFaqs);
                }, Logger::logError));
    }

    private Observable<Faq> getFaqSource(boolean forceReload) {
        if (!forceReload && !faqs.isEmpty())
            return Observable.fromIterable(faqs);
        else {
            return faqRepository.getFaqs(eventId, forceReload);
        }
    }

    private void listenChanges() {
        faqChangeListener.startListening();
        faqChangeListener.getNotifier()
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
            .doOnSubscribe(disposable -> progress.setValue(true))
            .doFinally(() -> progress.setValue(false))
            .subscribe(() -> {
                selectedMap.remove(faq);
                Logger.logSuccess(faq);
            }, Logger::logError);
    }

    public void deleteSelectedFaq() {
        Observable.fromIterable(selectedMap.entrySet())
            .doOnSubscribe(disposable -> progress.setValue(true))
            .doFinally(() -> progress.setValue(false))
            .subscribe(entry -> {
                if (entry.getValue().get()) {
                    deleteFaq(entry.getKey());
                }
                loadFaqs(false);
                success.setValue("FAQs Deleted Successfully");
            }, Logger::logError);
    }

    public void unselectFaq(Faq faq) {
        if (faq != null && selectedMap.containsKey(faq))
            selectedMap.get(faq).set(false);
    }

    public void resetToDefaultState() {
        isContextualModeActive = false;
        unSelectFaqList();
        exitContextualMenuMode.call();
    }

    public void onSingleSelect(Faq currentFaq) {
        if (isContextualModeActive) {
            if (countSelected() == 1 && getFaqSelected(currentFaq).get()) {
                selectedMap.get(currentFaq).set(false);
                exitContextualMenuMode.call();
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
            enterContextualMenuMode.call();
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
        for (Faq faq : selectedMap.keySet()) {
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
