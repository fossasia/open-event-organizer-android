package org.fossasia.openevent.app.login;

import org.fossasia.openevent.app.data.contract.ILoginModel;
import org.fossasia.openevent.app.login.contract.ILoginPresenter;
import org.fossasia.openevent.app.login.contract.ILoginView;

import javax.inject.Inject;

public class LoginPresenter implements ILoginPresenter {

    private ILoginView loginView;
    private ILoginModel loginModel;

    @Inject
    public LoginPresenter(ILoginModel loginModel) {
        this.loginModel = loginModel;
    }

    @Override
    public void attach(ILoginView loginView) {
        this.loginView = loginView;
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
