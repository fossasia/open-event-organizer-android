package org.fossasia.openevent.app.core.sponsor.list;

import com.raizlabs.android.dbflow.structure.BaseModel;

import org.fossasia.openevent.app.common.mvp.presenter.AbstractDetailPresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.db.DatabaseChangeListener;
import org.fossasia.openevent.app.data.db.DbFlowDatabaseChangeListener;
import org.fossasia.openevent.app.data.sponsor.Sponsor;
import org.fossasia.openevent.app.data.sponsor.SponsorRepository;

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

public class SponsorsPresenter extends AbstractDetailPresenter<Long, SponsorsView> {

    private final List<Sponsor> sponsors = new ArrayList<>();
    private final SponsorRepository sponsorRepository;
    private final DatabaseChangeListener<Sponsor> sponsorChangeListener;

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

    public void updateSponsor(Long sponsorId) {
        getView().openUpdateSponsorFragment(sponsorId);
    }

    private void listenChanges() {
        sponsorChangeListener.startListening();
        sponsorChangeListener.getNotifier()
            .compose(dispose(getDisposable()))
            .map(DbFlowDatabaseChangeListener.ModelChange::getAction)
            .filter(action -> action.equals(BaseModel.Action.INSERT) || action.equals(BaseModel.Action.UPDATE))
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

    public void deleteSponsor(Long sponsorId) {
        sponsorRepository
            .deleteSponsor(sponsorId)
            .compose(disposeCompletable(getDisposable()))
            .compose(progressiveErroneousCompletable(getView()))
            .subscribe(() -> {
                getView().showSponsorDeleted("Sponsor Deleted. Refreshing Items");
                loadSponsors(true);
            }, Logger::logError);
    }

    public void showDeleteAlertDialog(Long sponsorId) {
        getView().showAlertDialog(sponsorId);
    }


}
