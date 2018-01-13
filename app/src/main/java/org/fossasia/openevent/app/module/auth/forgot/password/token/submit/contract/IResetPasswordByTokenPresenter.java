package org.fossasia.openevent.app.module.auth.forgot.password.token.submit.contract;

import org.fossasia.openevent.app.common.app.lifecycle.contract.presenter.IPresenter;
import org.fossasia.openevent.app.common.data.models.SubmitToken;

public interface IResetPasswordByTokenPresenter extends IPresenter<IResetPasswordByTokenView> {

    void submitRequest();

    SubmitToken getSubmitToken();

    void setBaseUrl(String url, boolean shouldSetDefaultUrl);

}
