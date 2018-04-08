package org.fossasia.openevent.app.core.auth.forgot.request;

import android.support.annotation.VisibleForTesting;

import org.fossasia.openevent.app.BuildConfig;
import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.common.mvp.presenter.AbstractBasePresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.auth.AuthService;
import org.fossasia.openevent.app.data.Preferences;
import org.fossasia.openevent.app.data.auth.model.RequestToken;
import org.fossasia.openevent.app.data.network.HostSelectionInterceptor;

import java.util.Set;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.disposeCompletable;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneousCompletable;

public class ForgotPasswordPresenter extends AbstractBasePresenter<ForgotPasswordView> {

    private final AuthService forgotPasswordModel;
    private final Preferences sharedPreferenceModel;
    private final HostSelectionInterceptor interceptor;
    private final RequestToken forgotEmail = new RequestToken();

    @Inject
    public ForgotPasswordPresenter(AuthService loginModel, Preferences sharedPreferenceModel, HostSelectionInterceptor interceptor) {
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

    public RequestToken getEmailId() {
        return forgotEmail;
    }

    public void requestToken() {
        forgotPasswordModel.requestToken(forgotEmail)
            .compose(disposeCompletable(getDisposable()))
            .compose(progressiveErroneousCompletable(getView()))
            .subscribe(() -> getView().onSuccess("Token sent to Email"), Logger::logError);
    }

    public void setBaseUrl(String url, boolean shouldSetDefaultUrl) {
        String baseUrl = shouldSetDefaultUrl ? BuildConfig.DEFAULT_BASE_URL : url;
        interceptor.setInterceptor(baseUrl);
    }

    private Set<String> getEmailList() {
        return sharedPreferenceModel.getStringSet(Constants.SHARED_PREFS_SAVED_EMAIL, null);
    }

    @VisibleForTesting
    public ForgotPasswordView getView() {
        return super.getView();
    }
}
