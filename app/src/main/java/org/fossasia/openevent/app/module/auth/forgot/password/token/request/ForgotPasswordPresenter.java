package org.fossasia.openevent.app.module.auth.forgot.password.token.request;

import android.support.annotation.VisibleForTesting;

import org.fossasia.openevent.app.BuildConfig;
import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.common.app.lifecycle.presenter.BasePresenter;
import org.fossasia.openevent.app.common.app.rx.Logger;
import org.fossasia.openevent.app.common.data.contract.IAuthModel;
import org.fossasia.openevent.app.common.data.contract.ISharedPreferenceModel;
import org.fossasia.openevent.app.common.data.models.RequestToken;
import org.fossasia.openevent.app.common.data.network.HostSelectionInterceptor;
import org.fossasia.openevent.app.module.auth.forgot.password.token.request.contract.IForgotPasswordPresenter;
import org.fossasia.openevent.app.module.auth.forgot.password.token.request.contract.IForgotPasswordView;

import java.util.Set;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.disposeCompletable;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.progressiveErroneousCompletable;

public class ForgotPasswordPresenter extends BasePresenter<IForgotPasswordView> implements IForgotPasswordPresenter {

    private final IAuthModel forgotPasswordModel;
    private final ISharedPreferenceModel sharedPreferenceModel;
    private final HostSelectionInterceptor interceptor;
    private final RequestToken forgotEmail = new RequestToken();

    @Inject
    public ForgotPasswordPresenter(IAuthModel loginModel, ISharedPreferenceModel sharedPreferenceModel, HostSelectionInterceptor interceptor) {
        this.forgotPasswordModel = loginModel;
        this.sharedPreferenceModel = sharedPreferenceModel;
        this.interceptor = interceptor;
    }

    @Override
    public void start() {
        if (getView() == null)
            return;

        Set<String> emailList = getEmailList();
        if (emailList != null)
            getView().attachEmails(emailList);
    }

    @Override
    public RequestToken getEmailId() {
        return forgotEmail;
    }

    @Override
    public void requestToken() {
        forgotPasswordModel.requestToken(forgotEmail)
            .compose(disposeCompletable(getDisposable()))
            .compose(progressiveErroneousCompletable(getView()))
            .subscribe(() -> getView().onSuccess("Token sent to Email"), Logger::logError);
    }

    @Override
    public void setBaseUrl(String url, boolean shouldSetDefaultUrl) {
        String baseUrl = shouldSetDefaultUrl ? BuildConfig.DEFAULT_BASE_URL : url;
        interceptor.setInterceptor(baseUrl);
    }

    private Set<String> getEmailList() {
        return sharedPreferenceModel.getStringSet(Constants.SHARED_PREFS_SAVED_EMAIL, null);
    }

    @VisibleForTesting
    public IForgotPasswordView getView() {
        return super.getView();
    }
}
