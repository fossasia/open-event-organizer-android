package org.fossasia.openevent.app.login.contract;

public interface ILoginView {

    void showProgressBar(boolean show);

    void onLoginSuccess();

    void onLoginError(String error);

}
