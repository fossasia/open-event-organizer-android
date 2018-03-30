package org.fossasia.openevent.app.core.auth.forgot.submit;

import android.support.annotation.VisibleForTesting;

import org.fossasia.openevent.app.BuildConfig;
import org.fossasia.openevent.app.common.mvp.presenter.BasePresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.IAuthModel;
import org.fossasia.openevent.app.data.models.SubmitToken;
import org.fossasia.openevent.app.data.network.HostSelectionInterceptor;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.disposeCompletable;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneousCompletable;

public class ResetPasswordByTokenPresenter extends BasePresenter<IResetPasswordByTokenView> {

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

    public SubmitToken getSubmitToken() {
        return submitToken;
    }

    public void submitRequest() {
        tokenSubmitModel.submitToken(submitToken)
            .compose(disposeCompletable(getDisposable()))
            .compose(progressiveErroneousCompletable(getView()))
            .subscribe(() -> getView().onSuccess("Password Changed Successfully"), Logger::logError);
    }

    public void setBaseUrl(String url, boolean shouldSetDefaultUrl) {
        String baseUrl = shouldSetDefaultUrl ? BuildConfig.DEFAULT_BASE_URL : url;
        interceptor.setInterceptor(baseUrl);
    }

    @VisibleForTesting
    public IResetPasswordByTokenView getView() {
        return super.getView();
    }

}
