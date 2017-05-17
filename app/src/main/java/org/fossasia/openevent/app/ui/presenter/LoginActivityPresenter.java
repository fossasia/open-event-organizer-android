package org.fossasia.openevent.app.ui.presenter;

import org.fossasia.openevent.app.contract.model.ILoginModel;
import org.fossasia.openevent.app.contract.presenter.ILoginPresenter;
import org.fossasia.openevent.app.contract.view.ILoginView;

public class LoginActivityPresenter implements ILoginPresenter {

    private ILoginView loginView;
    private ILoginModel loginModel;

    public LoginActivityPresenter(ILoginView loginView, ILoginModel loginModel) {
        this.loginView = loginView;
        this.loginModel = loginModel;
    }

    @Override
    public void attach() {
        if(loginView != null && loginModel.isLoggedIn())
            loginView.onLoginSuccess();
    }

    @Override
    public void detach() {
        loginView = null;
    }

    @Override
    public void login(String email, String password) {
        if(loginView ==  null)
            return;

        loginView.showProgressBar(true);

        loginModel.login(email, password)
            .subscribe(loginResponse -> {
                if(loginView != null) {
                    loginView.onLoginSuccess();
                    loginView.showProgressBar(false);
                }
            }, throwable -> {
                if(loginView != null) {
                    loginView.onLoginError(throwable.getMessage());
                    loginView.showProgressBar(false);
                }
            });
    }

    public ILoginView getView() {
        return loginView;
    }

}
