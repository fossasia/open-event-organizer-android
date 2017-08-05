package org.fossasia.openevent.app.module.auth.signup;

import org.fossasia.openevent.app.BuildConfig;
import org.fossasia.openevent.app.common.app.lifecycle.presenter.BasePresenter;
import org.fossasia.openevent.app.common.app.rx.Logger;
import org.fossasia.openevent.app.common.data.contract.IAuthModel;
import org.fossasia.openevent.app.common.data.models.User;
import org.fossasia.openevent.app.common.data.network.HostSelectionInterceptor;
import org.fossasia.openevent.app.module.auth.signup.contract.ISignUpPresenter;
import org.fossasia.openevent.app.module.auth.signup.contract.ISignUpView;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.progressiveErroneous;

public class SignUpPresenter extends BasePresenter<ISignUpView> implements ISignUpPresenter {

    private final IAuthModel authModel;
    private final User user = new User();

    @Inject
    public SignUpPresenter(IAuthModel authModel) {
        this.authModel = authModel;
    }

    @Override
    public void start() {
        // doing nothing here for now.
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void signUp() {
        authModel.signUp(user)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneous(getView()))
            .subscribe(user -> getView().onSuccess("Successfully Registered"), Logger::logError);
    }

    @Override
    public void setBaseUrl(HostSelectionInterceptor interceptor, String url, boolean shouldSetDefaultUrl) {
        String baseUrl = shouldSetDefaultUrl ? BuildConfig.DEFAULT_BASE_URL : url;
        interceptor.setInterceptor(baseUrl);
    }
}
