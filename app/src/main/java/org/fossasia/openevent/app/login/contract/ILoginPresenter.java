package org.fossasia.openevent.app.login.contract;

public interface ILoginPresenter {

    void attach(ILoginView loginView);

    void detach();

    void login(String email, String password);

}
