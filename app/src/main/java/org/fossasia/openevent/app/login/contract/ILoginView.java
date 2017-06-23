package org.fossasia.openevent.app.login.contract;

import java.util.Set;

public interface ILoginView {

    void showProgressBar(boolean show);

    void onLoginSuccess();

    void onLoginError(String error);

    void attachEmails(Set<String> emails);

}
