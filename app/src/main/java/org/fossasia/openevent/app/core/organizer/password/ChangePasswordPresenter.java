package org.fossasia.openevent.app.core.organizer.password;

import android.support.annotation.VisibleForTesting;

import org.fossasia.openevent.app.BuildConfig;
import org.fossasia.openevent.app.common.mvp.presenter.AbstractBasePresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.auth.AuthService;
import org.fossasia.openevent.app.data.auth.model.ChangePassword;
import org.fossasia.openevent.app.data.network.HostSelectionInterceptor;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.disposeCompletable;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneousCompletable;

public class ChangePasswordPresenter extends AbstractBasePresenter<ChangePasswordView> {

    private final AuthService changePasswordModel;
    private final HostSelectionInterceptor interceptor;
    private final ChangePassword organizerPasswordObject = new ChangePassword();

    @Inject
    public ChangePasswordPresenter(AuthService changePasswordModel, HostSelectionInterceptor interceptor) {
        this.changePasswordModel = changePasswordModel;
        this.interceptor = interceptor;
    }

    public ChangePassword getChangePasswordObject() {
        return organizerPasswordObject;
    }

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

    public void setBaseUrl(String url, boolean shouldSetDefaultUrl) {
        String baseUrl = shouldSetDefaultUrl ? BuildConfig.DEFAULT_BASE_URL : url;
        interceptor.setInterceptor(baseUrl);
    }

    @VisibleForTesting
    public ChangePasswordView getView() {
        return super.getView();
    }

    @Override
    public void start() {
        // Intentionally left blank
    }
}
