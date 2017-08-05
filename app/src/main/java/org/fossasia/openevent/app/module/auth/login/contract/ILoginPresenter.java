package org.fossasia.openevent.app.module.auth.login.contract;

import org.fossasia.openevent.app.common.app.lifecycle.contract.presenter.IPresenter;
import org.fossasia.openevent.app.common.data.models.dto.Login;
import org.fossasia.openevent.app.common.data.network.HostSelectionInterceptor;

public interface ILoginPresenter extends IPresenter<ILoginView> {

    Login getLogin();

    void login();

    void setBaseUrl(HostSelectionInterceptor interceptor, String url, boolean shouldSetDefaultUrl);

}
