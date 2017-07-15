package org.fossasia.openevent.app.login;

import android.support.annotation.VisibleForTesting;

import org.fossasia.openevent.app.data.contract.ILoginModel;
import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.data.network.HostSelectionInterceptor;
import org.fossasia.openevent.app.login.contract.ILoginPresenter;
import org.fossasia.openevent.app.login.contract.ILoginView;
import org.fossasia.openevent.app.utils.Constants;

import java.util.Set;

import javax.inject.Inject;

public class LoginPresenter implements ILoginPresenter {

    private ILoginView loginView;
    private ILoginModel loginModel;
    private IUtilModel utilModel;

    @Inject
    public LoginPresenter(ILoginModel loginModel, IUtilModel utilModel) {
        this.loginModel = loginModel;
        this.utilModel = utilModel;
    }

    @Override
    public void attach(ILoginView loginView) {
        this.loginView = loginView;

        if(loginView == null)
            return;

        if(loginModel.isLoggedIn()) {
            loginView.onSuccess("Successfully logged in");
            return;
        }

        if(getEmailList() != null)
            loginView.attachEmails(getEmailList());
    }

    @Override
    public void detach() {
        loginView = null;
    }

    @Override
    public void login(String email, String password) {
        if(loginView ==  null)
            return;

        loginView.showProgress(true);

        loginModel.login(email, password)
            .subscribe(() -> {
                if(loginView != null) {
                    loginView.onSuccess("Successfully Logged In");
                    loginView.showProgress(false);
                }
            }, throwable -> {
                if(loginView != null) {
                    loginView.showError(throwable.getMessage());
                    loginView.showProgress(false);
                }
            });
    }

    @Override
    public void setBaseUrl(HostSelectionInterceptor interceptor, String defaultUrl, String url, boolean shouldSetDefaultUrl) {
        if (shouldSetDefaultUrl) {
            interceptor.setInterceptor(defaultUrl);
        } else {
            interceptor.setInterceptor(url);
        }
    }

    private Set<String> getEmailList() {
        return utilModel.getStringSet(Constants.SHARED_PREFS_SAVED_EMAIL, null);
    }

    @VisibleForTesting
    public ILoginView getView() {
        return loginView;
    }

}
