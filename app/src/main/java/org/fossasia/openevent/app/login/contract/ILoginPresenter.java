package org.fossasia.openevent.app.login.contract;

import org.fossasia.openevent.app.common.contract.presenter.IPresenter;
import org.fossasia.openevent.app.data.network.HostSelectionInterceptor;

public interface ILoginPresenter extends IPresenter<ILoginView> {

    void login(String email, String password);

    void setBaseUrl(HostSelectionInterceptor interceptor, String defaultUrl, String url, boolean shouldSetDefaultUrl);

}
