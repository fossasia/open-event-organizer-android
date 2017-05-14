package org.fossasia.openevent.app.contract.view;

public interface LoginView {

    void showProgressBar(boolean show);

    void onLoginSuccess();

    void onLoginError(String error);

}
