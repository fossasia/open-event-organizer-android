package org.fossasia.openevent.app.core.main;

import com.f2prateek.rx.preferences2.RxSharedPreferences;

import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.common.ContextManager;
import org.fossasia.openevent.app.common.mvp.presenter.AbstractBasePresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.auth.AuthService;
import org.fossasia.openevent.app.utils.DateUtils;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.disposeCompletable;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.erroneousCompletable;

public class MainPresenter extends AbstractBasePresenter<MainView> {

    private final AuthService loginModel;
    private final RxSharedPreferences sharedPreferences;
    private final ContextManager contextManager;

    @Inject
    public MainPresenter(AuthService loginModel, RxSharedPreferences sharedPreferences,
                         ContextManager contextManager) {
        this.loginModel = loginModel;
        this.sharedPreferences = sharedPreferences;
        this.contextManager = contextManager;
    }

    @Override
    public void start() {
        sharedPreferences.getBoolean(Constants.SHARED_PREFS_LOCAL_DATE)
            .asObservable()
            .compose(dispose(getDisposable()))
            .distinctUntilChanged()
            .doOnNext(changed -> getView().invalidateDateViews())
            .subscribe(DateUtils::setShowLocal);
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
