package org.fossasia.openevent.app.main;

import org.fossasia.openevent.app.data.contract.ILoginModel;
import org.fossasia.openevent.app.main.contract.IMainPresenter;
import org.fossasia.openevent.app.main.contract.IMainView;

import javax.inject.Inject;

import timber.log.Timber;

public class MainPresenter implements IMainPresenter {

    private IMainView mainView;
    private ILoginModel loginModel;

    @Inject
    public MainPresenter(ILoginModel loginModel) {
        this.loginModel = loginModel;
    }

    @Override
    public void attach(IMainView mainView) {
        this.mainView = mainView;
    }

    @Override
    public void detach() {
        mainView = null;
    }

    @Override
    public void logout() {
        if (mainView == null)
            return;

        loginModel.logout()
            .subscribe(() -> {
                if (mainView != null)
                    mainView.onLogout();
            }, throwable -> {
                Timber.e(throwable);
                if (mainView != null)
                    mainView.showError(throwable.getMessage());
            });
    }
}
