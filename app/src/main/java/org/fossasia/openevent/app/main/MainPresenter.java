package org.fossasia.openevent.app.main;

import org.fossasia.openevent.app.data.contract.IBus;
import org.fossasia.openevent.app.data.contract.ILoginModel;
import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.data.repository.contract.IEventRepository;
import org.fossasia.openevent.app.main.contract.IMainPresenter;
import org.fossasia.openevent.app.main.contract.IMainView;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

import static org.fossasia.openevent.app.main.MainActivity.EVENT_KEY;

public class MainPresenter implements IMainPresenter {

    private IMainView mainView;
    private IUtilModel utilModel;
    private ILoginModel loginModel;
    private IEventRepository eventRepository;
    private IBus bus;

    private final long storedEventId;

    private CompositeDisposable disposable = new CompositeDisposable();

    @Inject
    public MainPresenter(IUtilModel utilModel, ILoginModel loginModel, IEventRepository eventRepository, IBus bus) {
        this.utilModel = utilModel;
        this.loginModel = loginModel;
        this.eventRepository = eventRepository;
        this.bus = bus;

        storedEventId = utilModel.getLong(EVENT_KEY, -1);
    }

    @Override
    public void attach(IMainView mainView) {
        this.mainView = mainView;

        if (storedEventId != -1) {
            mainView.loadDashboard(storedEventId);

            disposable.add(eventRepository.getEvent(storedEventId, false)
                    .subscribe(mainView::showResult));
        }

        disposable.add(bus.getSelectedEvent()
            .subscribe(event -> {
                utilModel.setLong(EVENT_KEY, event.getId());
                mainView.loadDashboard(event.getId());
                mainView.showResult(event);
            }));
    }

    @Override
    public void detach() {
        mainView = null;
        disposable.dispose();
    }

    @Override
    public void logout() {
        disposable.add(loginModel.logout()
            .subscribe(() -> mainView.onLogout(),
                throwable -> {
                    Timber.e(throwable);
                    mainView.showError(throwable.getMessage());
            }));
    }
}
