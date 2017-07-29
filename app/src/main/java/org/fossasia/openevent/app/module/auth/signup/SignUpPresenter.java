package org.fossasia.openevent.app.module.auth.signup;

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

    private IAuthModel loginModel;

    @Inject
    public SignUpPresenter(IAuthModel loginModel) {
        this.loginModel = loginModel;
    }

    @Override
    public void start() {
        // doing nothing here for now.
    }

    @Override
    public void signUp(User newUser) {
        if(getView() ==  null)
            return;

        loginModel.signUp(newUser)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneous(getView()))
            .subscribe(user -> getView().onSuccess("Successfully Registered"), Logger::logError);
    }

    @Override
    public void setBaseUrl(HostSelectionInterceptor interceptor, String defaultUrl, String url, boolean shouldSetDefaultUrl) {
        if (shouldSetDefaultUrl) {
            interceptor.setInterceptor(defaultUrl);
        } else {
            interceptor.setInterceptor(url);
        }
    }
}
