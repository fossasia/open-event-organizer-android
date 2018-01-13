package org.fossasia.openevent.app.module.auth.forgot.password.token.request.contract;

import org.fossasia.openevent.app.common.app.lifecycle.contract.presenter.IPresenter;
import org.fossasia.openevent.app.common.data.models.RequestToken;

public interface IForgotPasswordPresenter extends IPresenter<IForgotPasswordView> {

    void requestToken();

    void setBaseUrl(String url, boolean shouldSetDefaultUrl);

    RequestToken getEmailId();
}
