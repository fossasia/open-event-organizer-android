package org.fossasia.openevent.app.contract.presenter;

public interface LoginPresenter {

    void attach();

    void detach();

    void login(String email, String password);

}
