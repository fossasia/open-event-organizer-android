package org.fossasia.openevent.app.module.main;

import com.f2prateek.rx.preferences2.RxSharedPreferences;

import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.common.app.ContextManager;
import org.fossasia.openevent.app.common.app.lifecycle.presenter.BasePresenter;
import org.fossasia.openevent.app.common.app.rx.Logger;
import org.fossasia.openevent.app.common.data.contract.IAuthModel;
import org.fossasia.openevent.app.common.data.contract.IBus;
import org.fossasia.openevent.app.common.data.contract.ISharedPreferenceModel;
import org.fossasia.openevent.app.common.data.models.Event;
import org.fossasia.openevent.app.common.data.models.User;
import org.fossasia.openevent.app.common.data.repository.contract.IEventRepository;
import org.fossasia.openevent.app.common.utils.core.CurrencyUtils;
import org.fossasia.openevent.app.common.utils.core.DateUtils;
import org.fossasia.openevent.app.module.main.contract.IMainPresenter;
import org.fossasia.openevent.app.module.main.contract.IMainView;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.disposeCompletable;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.erroneous;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.erroneousCompletable;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.erroneousResult;
import static org.fossasia.openevent.app.module.main.MainActivity.EVENT_KEY;

public class MainPresenter extends BasePresenter<IMainView> implements IMainPresenter {

    private final ISharedPreferenceModel sharedPreferenceModel;
    private final IAuthModel loginModel;
    private final IEventRepository eventRepository;
    private final RxSharedPreferences sharedPreferences;
    private final IBus bus;
    private final ContextManager contextManager;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private User organizer;

    @Inject
    public MainPresenter(ISharedPreferenceModel sharedPreferenceModel, IAuthModel loginModel,
                         IEventRepository eventRepository, IBus bus, RxSharedPreferences sharedPreferences, ContextManager contextManager) {
        this.sharedPreferenceModel = sharedPreferenceModel;
        this.loginModel = loginModel;
        this.eventRepository = eventRepository;
        this.bus = bus;
        this.sharedPreferences = sharedPreferences;
        this.contextManager = contextManager;
    }

    @Override
    public void start() {
        compositeDisposable.add(sharedPreferences.getBoolean(Constants.SHARED_PREFS_LOCAL_DATE)
            .asObservable()
            .compose(dispose(getDisposable()))
            .distinctUntilChanged()
            .doOnNext(changed -> getView().invalidateDateViews())
            .subscribe(DateUtils::setShowLocal));

        compositeDisposable.add(bus.getSelectedEvent()
            .compose(dispose(getDisposable()))
            .compose(erroneousResult(getView()))
            .subscribe(event -> {
                sharedPreferenceModel.setLong(EVENT_KEY, event.getId());
                ContextManager.setSelectedEvent(event);
                compositeDisposable.add(CurrencyUtils.getCurrencySymbol(event.getPaymentCurrency())
                    .subscribe(ContextManager::setCurrency));
                showEvent(event);
            }, Logger::logError));

        compositeDisposable.add(getOrganizerObservable()
            .compose(dispose(getDisposable()))
            .subscribe(user -> {
                this.organizer = user;
                getView().showOrganizer(user);
            }, Logger::logError));

        long storedEventId = sharedPreferenceModel.getLong(EVENT_KEY, -1);

        if (storedEventId == -1)
            getView().showEventList();
        else
            showLoadedEvent(storedEventId);
    }

    private void showLoadedEvent(long storedEventId) {
        getView().setEventId(storedEventId);
        Event staticEvent = ContextManager.getSelectedEvent();

        if (staticEvent != null) {
            getView().showResult(staticEvent);
            if (!isRotated()) showEvent(staticEvent);
            return;
        }

        compositeDisposable.add(eventRepository
            .getEvent(storedEventId, false)
            .compose(dispose(getDisposable()))
            .compose(erroneous(getView()))
            .subscribe(bus::pushSelectedEvent, Logger::logError));
    }

    private void showEvent(Event event) {
        getView().setEventId(event.getId());
        getView().showDashboard();
    }

    private Observable<User> getOrganizerObservable() {
        if (organizer != null && isRotated())
            return Observable.just(organizer);
        else
            return eventRepository.getOrganiser(false);
    }

    @Override
    public void logout() {
        compositeDisposable.add(loginModel.logout()
            .compose(disposeCompletable(getDisposable()))
            .compose(erroneousCompletable(getView()))
            .subscribe(() -> {
                contextManager.clearOrganiser();
                getView().onLogout();
            }, Logger::logError));
    }

    @Override
    public void detach() {
        super.detach();
        compositeDisposable.dispose();
    }
}
