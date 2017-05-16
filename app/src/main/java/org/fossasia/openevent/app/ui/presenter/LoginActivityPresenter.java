package org.fossasia.openevent.app.ui.presenter;

import org.fossasia.openevent.app.contract.model.LoginModel;
import org.fossasia.openevent.app.contract.model.UtilModel;
import org.fossasia.openevent.app.contract.presenter.LoginPresenter;
import org.fossasia.openevent.app.contract.view.LoginView;

public class LoginActivityPresenter implements LoginPresenter {

    private LoginView loginView;
    private LoginModel loginModel;
    private UtilModel utilModel;

    public LoginActivityPresenter(LoginView loginView, LoginModel loginModel, UtilModel utilModel) {
        this.loginView = loginView;
        this.loginModel = loginModel;
        this.utilModel = utilModel;
    }

    @Override
    public void attach() {
        if(loginView != null && utilModel.isLoggedIn())
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

    public LoginView getView() {
        return loginView;
    }

}
