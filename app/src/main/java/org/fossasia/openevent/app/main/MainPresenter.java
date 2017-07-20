package org.fossasia.openevent.app.main;

import org.fossasia.openevent.app.common.BasePresenter;
import org.fossasia.openevent.app.common.ContextManager;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.contract.IBus;
import org.fossasia.openevent.app.data.contract.ILoginModel;
import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.data.repository.contract.IEventRepository;
import org.fossasia.openevent.app.main.contract.IMainPresenter;
import org.fossasia.openevent.app.main.contract.IMainView;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.disposeCompletable;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.erroneousCompletable;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.erroneousResult;
import static org.fossasia.openevent.app.main.MainActivity.EVENT_KEY;

public class MainPresenter extends BasePresenter<IMainView> implements IMainPresenter {

    private final IUtilModel utilModel;
    private final ILoginModel loginModel;
    private final IEventRepository eventRepository;
    private final IBus bus;
    private final ContextManager contextManager;

    private final long storedEventId;

    @Inject
    public MainPresenter(IUtilModel utilModel, ILoginModel loginModel, IEventRepository eventRepository, IBus bus, ContextManager contextManager) {
        this.utilModel = utilModel;
        this.loginModel = loginModel;
        this.eventRepository = eventRepository;
        this.bus = bus;
        this.contextManager = contextManager;

        storedEventId = utilModel.getLong(EVENT_KEY, -1);
    }

    @Override
    public void attach(IMainView mainView) {
        super.attach(mainView);
    }

    @Override
    public void start() {
        getView().loadInitialPage(storedEventId);

        if (storedEventId != -1) {
            eventRepository
                .getEvent(storedEventId, false)
                .compose(dispose(getDisposable()))
                .compose(erroneousResult(getView()))
                .subscribe(Logger::logSuccess, Logger::logError);
        }

        bus.getSelectedEvent()
            .compose(dispose(getDisposable()))
            .compose(erroneousResult(getView()))
            .subscribe(event -> {
                utilModel.setLong(EVENT_KEY, event.getId());
                getView().loadInitialPage(event.getId());
            }, Logger::logError);
    }

    @Override
    public void detach() {
        super.detach();
    }

    @Override
    public void logout() {
        loginModel.logout()
            .compose(disposeCompletable(getDisposable()))
            .compose(erroneousCompletable(getView()))
            .subscribe(() -> {
                contextManager.clearOrganiser();
                getView().onLogout();
            }, Logger::logError);
    }
}
