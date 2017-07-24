package org.fossasia.openevent.app.module.login;

import android.support.annotation.VisibleForTesting;

import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.common.app.lifecycle.presenter.BasePresenter;
import org.fossasia.openevent.app.common.app.rx.Logger;
import org.fossasia.openevent.app.common.data.contract.ILoginModel;
import org.fossasia.openevent.app.common.data.contract.ISharedPreferenceModel;
import org.fossasia.openevent.app.common.data.network.HostSelectionInterceptor;
import org.fossasia.openevent.app.module.login.contract.ILoginPresenter;
import org.fossasia.openevent.app.module.login.contract.ILoginView;

import java.util.Set;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.disposeCompletable;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.progressiveErroneousCompletable;

public class LoginPresenter extends BasePresenter<ILoginView> implements ILoginPresenter {

    private ILoginModel loginModel;
    private ISharedPreferenceModel sharedPreferenceModel;

    @Inject
    public LoginPresenter(ILoginModel loginModel, ISharedPreferenceModel sharedPreferenceModel) {
        this.loginModel = loginModel;
        this.sharedPreferenceModel = sharedPreferenceModel;
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
        return sharedPreferenceModel.getStringSet(Constants.SHARED_PREFS_SAVED_EMAIL, null);
    }

    @VisibleForTesting
    public ILoginView getView() {
        return super.getView();
    }

}
