package org.fossasia.openevent.app.login.contract;

import org.fossasia.openevent.app.data.network.HostSelectionInterceptor;

public interface ILoginPresenter {

    void attach(ILoginView loginView);

    void detach();

    void login(String email, String password);

    void setBaseUrl(HostSelectionInterceptor interceptor, String defaultUrl, String url, boolean shouldSetDefaultUrl);

}
