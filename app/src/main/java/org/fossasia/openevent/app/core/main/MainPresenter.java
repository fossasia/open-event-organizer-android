package org.fossasia.openevent.app.core.main;

import com.f2prateek.rx.preferences2.RxSharedPreferences;

import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.common.ContextManager;
import org.fossasia.openevent.app.common.mvp.presenter.AbstractBasePresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.auth.AuthService;
import org.fossasia.openevent.app.data.Bus;
import org.fossasia.openevent.app.data.Preferences;
import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.data.user.User;
import org.fossasia.openevent.app.data.event.EventRepository;
import org.fossasia.openevent.app.data.user.UserRepository;
import org.fossasia.openevent.app.utils.CurrencyUtils;
import org.fossasia.openevent.app.utils.DateUtils;

import javax.inject.Inject;

import io.reactivex.Observable;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.disposeCompletable;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.erroneous;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.erroneousCompletable;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.erroneousResult;
import static org.fossasia.openevent.app.core.main.MainActivity.EVENT_KEY;

public class MainPresenter extends AbstractBasePresenter<MainView> {

    private final Preferences sharedPreferenceModel;
    private final AuthService loginModel;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RxSharedPreferences sharedPreferences;
    private final Bus bus;
    private final ContextManager contextManager;
    private final CurrencyUtils currencyUtils;

    private User organizer;

    @Inject
    @SuppressWarnings("checkstyle:parameternumber")
    public MainPresenter(Preferences sharedPreferenceModel, AuthService loginModel,
                         EventRepository eventRepository, Bus bus, RxSharedPreferences sharedPreferences,
                         ContextManager contextManager, UserRepository userRepository, CurrencyUtils currencyUtils) {
        this.sharedPreferenceModel = sharedPreferenceModel;
        this.loginModel = loginModel;
        this.eventRepository = eventRepository;
        this.bus = bus;
        this.sharedPreferences = sharedPreferences;
        this.contextManager = contextManager;
        this.userRepository = userRepository;
        this.currencyUtils = currencyUtils;
    }

    @Override
    public void start() {
        sharedPreferences.getBoolean(Constants.SHARED_PREFS_LOCAL_DATE)
            .asObservable()
            .compose(dispose(getDisposable()))
            .distinctUntilChanged()
            .doOnNext(changed -> getView().invalidateDateViews())
            .subscribe(DateUtils::setShowLocal);

        bus.getSelectedEvent()
            .compose(dispose(getDisposable()))
            .compose(erroneousResult(getView()))
            .subscribe(event -> {
                sharedPreferenceModel.setLong(EVENT_KEY, event.getId());
                ContextManager.setSelectedEvent(event);
                currencyUtils.getCurrencySymbol(event.getPaymentCurrency())
                    .subscribe(ContextManager::setCurrency, Logger::logError);
                showEvent(event);
            }, Logger::logError);

        getOrganizerObservable()
            .compose(dispose(getDisposable()))
            .subscribe(user -> {
                this.organizer = user;
                getView().showOrganizer(user);
            }, Logger::logError);

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

        eventRepository
            .getEvent(storedEventId, false)
            .compose(dispose(getDisposable()))
            .compose(erroneous(getView()))
            .subscribe(bus::pushSelectedEvent, Logger::logError);
    }

    private void showEvent(Event event) {
        getView().setEventId(event.getId());
        getView().showDashboard();
    }

    private Observable<User> getOrganizerObservable() {
        if (organizer != null && isRotated())
            return Observable.just(organizer);
        else
            return userRepository.getOrganizer(false);
    }

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
