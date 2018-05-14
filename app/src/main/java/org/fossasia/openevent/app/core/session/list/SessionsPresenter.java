package org.fossasia.openevent.app.core.session.list;

import com.raizlabs.android.dbflow.structure.BaseModel;

import org.fossasia.openevent.app.common.mvp.presenter.AbstractDetailPresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.db.DatabaseChangeListener;
import org.fossasia.openevent.app.data.db.DbFlowDatabaseChangeListener;
import org.fossasia.openevent.app.data.session.Session;
import org.fossasia.openevent.app.data.session.SessionRepository;

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

public class SessionsPresenter extends AbstractDetailPresenter<Long, SessionsView> {

    private final List<Session> sessions = new ArrayList<>();
    private Session previousSession = new Session();
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


    public void deleteSession(Session session) {
        sessionRepository
            .deleteSession(session.getId())
            .compose(disposeCompletable(getDisposable()))
            .compose(progressiveErroneousCompletable(getView()))
            .doFinally(() -> unselectSession(session))
            .subscribe(() -> getView().showMessage("Session Deleted Successfully"), Logger::logError);
    }

    public void deleteSelectedSession() {
        deleteSession(previousSession);
    }

    public void unselectSession(Session session) {
        if (session != null)
            session.getSelected().set(false);
    }

    public void toolbarDeleteMode(Session currentSession) {
        if (!previousSession.equals(currentSession))
            unselectSession(previousSession);

        previousSession = currentSession;
        getView().changeToDeletingMode();
    }

    public void resetToDefaultState() {
        unselectSession(previousSession);
        getView().resetToolbar();
    }
}
