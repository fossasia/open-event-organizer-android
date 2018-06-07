package org.fossasia.openevent.app.core.sponsor.list;

import android.databinding.ObservableBoolean;
import android.support.annotation.VisibleForTesting;

import com.raizlabs.android.dbflow.structure.BaseModel;

import org.fossasia.openevent.app.common.mvp.presenter.AbstractDetailPresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.db.DatabaseChangeListener;
import org.fossasia.openevent.app.data.db.DbFlowDatabaseChangeListener;
import org.fossasia.openevent.app.data.sponsor.Sponsor;
import org.fossasia.openevent.app.data.sponsor.SponsorRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.disposeCompletable;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.emptiable;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneous;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneousRefresh;

public class SponsorsPresenter extends AbstractDetailPresenter<Long, SponsorsView> {

    private final List<Sponsor> sponsors = new ArrayList<>();
    private final SponsorRepository sponsorRepository;
    private final DatabaseChangeListener<Sponsor> sponsorChangeListener;
    private final Map<Long, ObservableBoolean> selectedSponsors = new ConcurrentHashMap<>();
    private boolean isToolbarActive;

    private static final int EDITABLE_AT_ONCE = 1;

    @Inject
    public SponsorsPresenter(SponsorRepository sponsorRepository, DatabaseChangeListener<Sponsor> sponsorChangeListener) {
        this.sponsorRepository = sponsorRepository;
        this.sponsorChangeListener = sponsorChangeListener;
    }

    @Override
    public void start() {
        loadSponsors(false);
        listenChanges();
    }

    @Override
    public void detach() {
        super.detach();
        sponsorChangeListener.stopListening();
    }

    public void updateSponsor() {
        for (Long id : selectedSponsors.keySet()) {
            if (selectedSponsors.get(id).get()) {
                getView().openUpdateSponsorFragment(id);
                selectedSponsors.get(id).set(false);
                resetToolbarDefaultState();
                return;
            }
        }
    }

    private void listenChanges() {
        sponsorChangeListener.startListening();
        sponsorChangeListener.getNotifier()
            .compose(dispose(getDisposable()))
            .map(DbFlowDatabaseChangeListener.ModelChange::getAction)
            .filter(action -> action.equals(BaseModel.Action.INSERT) || action.equals(BaseModel.Action.UPDATE) ||
                action.equals(BaseModel.Action.DELETE))
            .subscribeOn(Schedulers.io())
            .subscribe(sponsorModelChange -> loadSponsors(false), Logger::logError);
    }

    public void loadSponsors(boolean forceReload) {
        getSponsorSource(forceReload)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneousRefresh(getView(), forceReload))
            .toList()
            .compose(emptiable(getView(), sponsors))
            .subscribe(Logger::logSuccess, Logger::logError);
    }

    private Observable<Sponsor> getSponsorSource(boolean forceReload) {
        if (!forceReload && !sponsors.isEmpty() && isRotated())
            return Observable.fromIterable(sponsors);
        else
            return sponsorRepository.getSponsors(getId(), forceReload);
    }

    public List<Sponsor> getSponsors() {
        return sponsors;
    }

    @VisibleForTesting
    protected void deleteSponsor(Long sponsorId) {
        sponsorRepository
            .deleteSponsor(sponsorId)
            .compose(disposeCompletable(getDisposable()))
            .subscribe(() -> {
                selectedSponsors.remove(sponsorId);
                Logger.logSuccess(sponsorId);
            }, Logger::logError);
    }

    public void deleteSelectedSponsors() {
        Observable.fromIterable(selectedSponsors.entrySet())
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneous(getView()))
            .doFinally(() -> {
                getView().showMessage("Sponsors Deleted");
                resetToolbarDefaultState();
            })
            .subscribe(entry -> {
                if (entry.getValue().get()) {
                    deleteSponsor(entry.getKey());
                }
            }, Logger::logError);
    }

    public void longClick(Sponsor clickedSponsor) {
        if (isToolbarActive)
            click(clickedSponsor.getId());
        else {
            selectedSponsors.get(clickedSponsor.getId()).set(true);
            isToolbarActive = true;
            getView().changeToolbarMode(true, true);
        }
    }

    public void click(Long clickedSponsorId) {
        if (isToolbarActive) {

            if (countSelected() == 1 && isSponsorSelected(clickedSponsorId).get()) {
                selectedSponsors.get(clickedSponsorId).set(false);
                resetToolbarDefaultState();
            } else if (countSelected() == 2 && isSponsorSelected(clickedSponsorId).get()) {
                selectedSponsors.get(clickedSponsorId).set(false);
                getView().changeToolbarMode(true, true);
            } else if (isSponsorSelected(clickedSponsorId).get())
                selectedSponsors.get(clickedSponsorId).set(false);
            else
                selectedSponsors.get(clickedSponsorId).set(true);

            if (countSelected() > EDITABLE_AT_ONCE)
                getView().changeToolbarMode(false, true);
        }
    }

    public void resetToolbarDefaultState() {
        isToolbarActive = false;
        getView().changeToolbarMode(false, false);
    }

    public ObservableBoolean isSponsorSelected(Long sponsorId) {
        if (!selectedSponsors.containsKey(sponsorId))
            selectedSponsors.put(sponsorId, new ObservableBoolean(false));

        return selectedSponsors.get(sponsorId);
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis") // Inevitable DD anomaly
    private int countSelected() {
        int count = 0;
        for (Long id : selectedSponsors.keySet()) {
            if (selectedSponsors.get(id).get())
                count++;
        }
        return count;
    }

    @VisibleForTesting
    protected Map<Long, ObservableBoolean> getSelectedSponsors() {
        return selectedSponsors;
    }

}
