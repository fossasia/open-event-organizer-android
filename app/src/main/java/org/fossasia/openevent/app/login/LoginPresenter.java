package org.fossasia.openevent.app.login;

import org.fossasia.openevent.app.data.contract.ILoginModel;
import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.login.contract.ILoginPresenter;
import org.fossasia.openevent.app.login.contract.ILoginView;

public class LoginPresenter implements ILoginPresenter {

    private ILoginView loginView;
    private ILoginModel loginDataRepository;
    private IUtilModel utilModel;

    public LoginPresenter(ILoginView loginView, ILoginModel loginDateRepository, IUtilModel utilModel) {
        this.loginView = loginView;
        this.loginDataRepository = loginDateRepository;
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

        loginDataRepository.login(email, password)
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
