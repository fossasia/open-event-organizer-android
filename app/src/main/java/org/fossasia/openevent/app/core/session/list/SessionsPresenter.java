package org.fossasia.openevent.app.core.session.list;

import android.databinding.ObservableBoolean;

import com.raizlabs.android.dbflow.structure.BaseModel;

import org.fossasia.openevent.app.common.mvp.presenter.AbstractDetailPresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.db.DatabaseChangeListener;
import org.fossasia.openevent.app.data.db.DbFlowDatabaseChangeListener;
import org.fossasia.openevent.app.data.session.Session;
import org.fossasia.openevent.app.data.session.SessionRepository;

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
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneous;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneousRefresh;

public class SessionsPresenter extends AbstractDetailPresenter<Long, SessionsView> {

    private final List<Session> sessions = new ArrayList<>();
    private final Map<Session, ObservableBoolean> selectedMap = new HashMap<>();
    private static ObservableBoolean selectedState = new ObservableBoolean(true);
    private final SessionRepository sessionRepository;
    private final DatabaseChangeListener<Session> sessionChangeListener;

    @Inject
    public SessionsPresenter(SessionRepository sessionRepository, DatabaseChangeListener<Session> sessionChangeListener) {
        this.sessionRepository = sessionRepository;
        this.sessionChangeListener = sessionChangeListener;
    }

    @Override
    public void start() {
        loadSessions(false);
        listenChanges();
    }

    @Override
    public void detach() {
        super.detach();
        sessionChangeListener.stopListening();
        selectedMap.clear();
    }

    public void loadSessions(boolean forceReload) {
        getSessionSource(forceReload)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneousRefresh(getView(), forceReload))
            .toList()
            .compose(emptiable(getView(), sessions))
            .subscribe(Logger::logSuccess, Logger::logError);
    }

    private Observable<Session> getSessionSource(boolean forceReload) {
        if (!forceReload && !sessions.isEmpty() && isRotated())
            return Observable.fromIterable(sessions);
        else {
            return sessionRepository.getSessions(getId(), forceReload);
        }
    }

    private void listenChanges() {
        sessionChangeListener.startListening();
        sessionChangeListener.getNotifier()
            .compose(dispose(getDisposable()))
            .map(DbFlowDatabaseChangeListener.ModelChange::getAction)
            .filter(action -> action.equals(BaseModel.Action.INSERT) || action.equals(BaseModel.Action.DELETE))
            .subscribeOn(Schedulers.io())
            .subscribe(sessionModelChange -> loadSessions(false), Logger::logError);
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public void deleteSession(Map<Session, ObservableBoolean> selectedMap, Session session) {
        sessionRepository
            .deleteSession(session.getId())
            .compose(disposeCompletable(getDisposable()))
            .doFinally(() -> selectedMap.get(session).set(false))
            .subscribe(() -> Logger.logSuccess(session), Logger::logError);
    }

    public void deleteSessions(Map<Session, ObservableBoolean> selectedMap) {
        Observable.fromIterable(selectedMap.entrySet())
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneous(getView()))
            .doFinally(() -> getView().showMessage("Sessions Deleted"))
            .subscribe(entry -> {
             if (entry.getValue().get()) {
                 deleteSession(selectedMap, entry.getKey());
             }
            }, Logger::logError);
    }

    public void deleteSelectedSessions() {
        deleteSessions(selectedMap);
    }

    public ObservableBoolean getSessionSelected(Session session) {
        if (!selectedMap.containsKey(session)) {
            selectedMap.put(session, new ObservableBoolean(false));
        }
        return selectedMap.get(session);
    }

    public Map<Session, ObservableBoolean> getSelectedMap() {
        return selectedMap;
    }

    public void toolbarDeleteMode(Session currentSession) {
        if (getSessionSelected(currentSession).equals(selectedState)) {
            selectedMap.get(currentSession).set(false);
        } else {
            selectedMap.get(currentSession).set(true);
        }

        getView().changeToDeletingMode();
    }

    public void resetToDefaultState() {
        for (Map.Entry<Session, ObservableBoolean> entry: selectedMap.entrySet()) {
            if (entry.getValue().equals(selectedState)) {
                entry.getValue().set(false);
            }
        }

        getView().resetToolbar();
    }
}
