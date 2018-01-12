package org.fossasia.openevent.app.module.auth.forgot.password.token.submit;

import android.support.annotation.VisibleForTesting;

import org.fossasia.openevent.app.BuildConfig;
import org.fossasia.openevent.app.common.app.lifecycle.presenter.BasePresenter;
import org.fossasia.openevent.app.common.app.rx.Logger;
import org.fossasia.openevent.app.common.data.contract.IAuthModel;
import org.fossasia.openevent.app.common.data.models.SubmitToken;
import org.fossasia.openevent.app.common.data.network.HostSelectionInterceptor;
import org.fossasia.openevent.app.module.auth.forgot.password.token.submit.contract.IResetPasswordByTokenPresenter;
import org.fossasia.openevent.app.module.auth.forgot.password.token.submit.contract.IResetPasswordByTokenView;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.disposeCompletable;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.progressiveErroneousCompletable;

public class ResetPasswordByTokenPresenter extends BasePresenter<IResetPasswordByTokenView> implements IResetPasswordByTokenPresenter {

    private final IAuthModel tokenSubmitModel;
    private final HostSelectionInterceptor interceptor;
    private final SubmitToken submitToken = new SubmitToken();

    @Inject
    public ResetPasswordByTokenPresenter(IAuthModel tokenSubmitModel,
                                         HostSelectionInterceptor interceptor) {
        this.tokenSubmitModel = tokenSubmitModel;
        this.interceptor = interceptor;
    }

    @Override
    public void start() {
        if (getView() == null)
            return;
    }

    @Override
    public SubmitToken getSubmitToken() {
        return submitToken;
    }

    @Override
    public void submitRequest() {
        tokenSubmitModel.submitToken(submitToken)
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
    public IResetPasswordByTokenView getView() {
        return super.getView();
    }

}
