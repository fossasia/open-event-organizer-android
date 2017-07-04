package org.fossasia.openevent.app.main.contract;

public interface IMainPresenter {

    void attach(IMainView mainView);

    void detach();

    void logout();
}
