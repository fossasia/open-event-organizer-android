package org.fossasia.openevent.app.login;

import android.support.annotation.VisibleForTesting;

import org.fossasia.openevent.app.common.BasePresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.contract.ILoginModel;
import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.data.network.HostSelectionInterceptor;
import org.fossasia.openevent.app.login.contract.ILoginPresenter;
import org.fossasia.openevent.app.login.contract.ILoginView;
import org.fossasia.openevent.app.utils.Constants;

import java.util.Set;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.disposeCompletable;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneousCompletable;

public class LoginPresenter extends BasePresenter<ILoginView> implements ILoginPresenter {

    private ILoginModel loginModel;
    private IUtilModel utilModel;

    @Inject
    public LoginPresenter(ILoginModel loginModel, IUtilModel utilModel) {
        this.loginModel = loginModel;
        this.utilModel = utilModel;
    }

    @Override
    public void attach(ILoginView loginView) {
        super.attach(loginView);
    }

    @Override
    public void start() {
        if(getView() == null)
            return;

        if(loginModel.isLoggedIn()) {
            getView().onSuccess("Successfully logged in");
            return;
        }

        Set<String> emailList = getEmailList();
        if(emailList != null)
            getView().attachEmails(emailList);
    }

    @Override
    public void detach() {
        super.detach();
    }

    @Override
    public void login(String email, String password) {
        if(getView() ==  null)
            return;

        loginModel.login(email, password)
            .compose(disposeCompletable(getDisposable()))
            .compose(progressiveErroneousCompletable(getView()))
            .subscribe(() -> getView().onSuccess("Successfully Logged In"), Logger::logError);
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
        return super.getView();
    }

}
