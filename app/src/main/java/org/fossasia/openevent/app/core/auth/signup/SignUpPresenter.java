package org.fossasia.openevent.app.core.auth.signup;

import org.fossasia.openevent.app.BuildConfig;
import org.fossasia.openevent.app.common.mvp.presenter.BasePresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.IAuthModel;
import org.fossasia.openevent.app.data.models.User;
import org.fossasia.openevent.app.data.network.HostSelectionInterceptor;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneous;

public class SignUpPresenter extends BasePresenter<ISignUpView> {

    private final IAuthModel authModel;
    private final HostSelectionInterceptor interceptor;
    private final User user = new User();

    @Inject
    public SignUpPresenter(IAuthModel authModel, HostSelectionInterceptor interceptor) {
        this.authModel = authModel;
        this.interceptor = interceptor;
    }

    @Override
    public void start() {
        // doing nothing here for now.
    }

    public User getUser() {
        return user;
    }

    public void signUp() {
        authModel.signUp(user)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneous(getView()))
            .subscribe(user -> getView().onSuccess("Successfully Registered"), Logger::logError);
    }

    public void setBaseUrl(String url, boolean shouldSetDefaultUrl) {
        String baseUrl = shouldSetDefaultUrl ? BuildConfig.DEFAULT_BASE_URL : url;
        interceptor.setInterceptor(baseUrl);
    }

    public boolean arePasswordsEqual(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            getView().showError("Passwords don't match!");
            return false;
        }
        return true;
    }
}
