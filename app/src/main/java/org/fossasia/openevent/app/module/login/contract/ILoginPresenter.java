package org.fossasia.openevent.app.module.login.contract;

import org.fossasia.openevent.app.common.app.lifecycle.contract.presenter.IPresenter;
import org.fossasia.openevent.app.common.data.network.HostSelectionInterceptor;

public interface ILoginPresenter extends IPresenter<ILoginView> {

    void login(String email, String password);

    void setBaseUrl(HostSelectionInterceptor interceptor, String defaultUrl, String url, boolean shouldSetDefaultUrl);

}
