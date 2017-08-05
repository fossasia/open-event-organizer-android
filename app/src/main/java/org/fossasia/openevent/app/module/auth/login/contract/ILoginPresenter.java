package org.fossasia.openevent.app.module.auth.login.contract;

import org.fossasia.openevent.app.common.app.lifecycle.contract.presenter.IPresenter;
import org.fossasia.openevent.app.common.data.models.dto.Login;

public interface ILoginPresenter extends IPresenter<ILoginView> {

    Login getLogin();

    void login();

    void setBaseUrl(String url, boolean shouldSetDefaultUrl);

}
