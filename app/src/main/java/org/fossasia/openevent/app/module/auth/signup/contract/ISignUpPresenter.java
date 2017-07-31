package org.fossasia.openevent.app.module.auth.signup.contract;

import org.fossasia.openevent.app.common.app.lifecycle.contract.presenter.IPresenter;
import org.fossasia.openevent.app.common.data.models.User;
import org.fossasia.openevent.app.common.data.network.HostSelectionInterceptor;

public interface ISignUpPresenter extends IPresenter<ISignUpView> {

    void signUp(User newUser);

    void setBaseUrl(HostSelectionInterceptor interceptor, String defaultUrl, String url, boolean shouldSetDefaultUrl);

}
