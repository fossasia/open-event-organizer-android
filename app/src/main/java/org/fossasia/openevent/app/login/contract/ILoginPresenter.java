package org.fossasia.openevent.app.login.contract;

public interface ILoginPresenter {

    void attach();

    void detach();

    void login(String email, String password);

}
