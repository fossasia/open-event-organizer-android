package org.fossasia.openevent.app.module.organizer.password;

import android.support.annotation.VisibleForTesting;

import org.fossasia.openevent.app.BuildConfig;
import org.fossasia.openevent.app.common.app.lifecycle.presenter.BasePresenter;
import org.fossasia.openevent.app.common.app.rx.Logger;
import org.fossasia.openevent.app.common.data.contract.IAuthModel;
import org.fossasia.openevent.app.common.data.models.ChangePassword;
import org.fossasia.openevent.app.common.data.network.HostSelectionInterceptor;
import org.fossasia.openevent.app.module.organizer.password.contract.IChangePasswordPresenter;
import org.fossasia.openevent.app.module.organizer.password.contract.IChangePasswordView;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.disposeCompletable;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.progressiveErroneousCompletable;

public class ChangePasswordPresenter extends BasePresenter<IChangePasswordView> implements IChangePasswordPresenter {

    private final IAuthModel changePasswordModel;
    private final HostSelectionInterceptor interceptor;
    private final ChangePassword organizerPasswordObject = new ChangePassword();

    @Inject
    public ChangePasswordPresenter(IAuthModel changePasswordModel, HostSelectionInterceptor interceptor) {
        this.changePasswordModel = changePasswordModel;
        this.interceptor = interceptor;
    }

    @Override
    public ChangePassword getChangePasswordObject() {
        return organizerPasswordObject;
    }

    @Override
    public void changePasswordRequest(String oldPassword, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            getView().showError("Passwords Do Not Match");
            return;
        }

        organizerPasswordObject.setOldPassword(oldPassword);
        organizerPasswordObject.setNewPassword(newPassword);
        organizerPasswordObject.setConfirmNewPassword(confirmPassword);

        changePasswordModel.changePassword(organizerPasswordObject)
            .compose(disposeCompletable(getDisposable()))
            .compose(progressiveErroneousCompletable(getView()))
            .subscribe(() -> getView().onSuccess("Password Changed Successfully"), Logger::logError);
    }

    @Override
    public void setBaseUrl(String url, boolean shouldSetDefaultUrl) {
        String baseUrl = shouldSetDefaultUrl ? BuildConfig.DEFAULT_BASE_URL : url;
        interceptor.setInterceptor(baseUrl);
    }

    @VisibleForTesting
    public IChangePasswordView getView() {
        return super.getView();
    }

    @Override
    public void start() {
        // Intentionally left blank
    }
}
