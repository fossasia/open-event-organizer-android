package org.fossasia.openevent.app.module.auth.login;

import android.support.annotation.VisibleForTesting;

import org.fossasia.openevent.app.BuildConfig;
import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.common.app.lifecycle.presenter.BasePresenter;
import org.fossasia.openevent.app.common.app.rx.Logger;
import org.fossasia.openevent.app.common.data.contract.IAuthModel;
import org.fossasia.openevent.app.common.data.contract.ISharedPreferenceModel;
import org.fossasia.openevent.app.common.data.models.dto.Login;
import org.fossasia.openevent.app.common.data.network.HostSelectionInterceptor;
import org.fossasia.openevent.app.module.auth.login.contract.ILoginPresenter;
import org.fossasia.openevent.app.module.auth.login.contract.ILoginView;

import java.util.Set;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.disposeCompletable;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.progressiveErroneousCompletable;

public class LoginPresenter extends BasePresenter<ILoginView> implements ILoginPresenter {

    private final IAuthModel loginModel;
    private final ISharedPreferenceModel sharedPreferenceModel;
    private final Login login = new Login();

    @Inject
    public LoginPresenter(IAuthModel loginModel, ISharedPreferenceModel sharedPreferenceModel) {
        this.loginModel = loginModel;
        this.sharedPreferenceModel = sharedPreferenceModel;
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
    public Login getLogin() {
        return login;
    }

    @Override
    public void login() {
        loginModel.login(login)
            .compose(disposeCompletable(getDisposable()))
            .compose(progressiveErroneousCompletable(getView()))
            .subscribe(() -> getView().onSuccess("Successfully Logged In"), Logger::logError);
    }

    @Override
    public void setBaseUrl(HostSelectionInterceptor interceptor, String url, boolean shouldSetDefaultUrl) {
        String baseUrl = shouldSetDefaultUrl ? BuildConfig.DEFAULT_BASE_URL : url;
        interceptor.setInterceptor(baseUrl);
    }

    private Set<String> getEmailList() {
        return sharedPreferenceModel.getStringSet(Constants.SHARED_PREFS_SAVED_EMAIL, null);
    }

    @VisibleForTesting
    public ILoginView getView() {
        return super.getView();
    }

}
