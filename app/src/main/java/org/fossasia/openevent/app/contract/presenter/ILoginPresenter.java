package org.fossasia.openevent.app.contract.presenter;

public interface ILoginPresenter {

    void attach();

    void detach();

    void login(String email, String password);

}
