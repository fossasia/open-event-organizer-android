package org.fossasia.openevent.app.module.organizer.password.contract;

import org.fossasia.openevent.app.common.app.lifecycle.contract.presenter.IPresenter;
import org.fossasia.openevent.app.common.data.models.ChangePassword;

public interface IChangePasswordPresenter extends IPresenter<IChangePasswordView> {

    void changePasswordRequest(String oldPassword, String newPassword, String confirmPassword);

    void setBaseUrl(String url, boolean shouldSetDefaultUrl);

    ChangePassword getChangePasswordObject();
}
