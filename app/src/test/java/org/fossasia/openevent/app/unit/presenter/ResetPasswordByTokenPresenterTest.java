package org.fossasia.openevent.app.unit.presenter;

import org.fossasia.openevent.app.common.app.rx.Logger;
import org.fossasia.openevent.app.common.data.contract.IAuthModel;
import org.fossasia.openevent.app.common.data.models.SubmitToken;
import org.fossasia.openevent.app.common.data.network.HostSelectionInterceptor;
import org.fossasia.openevent.app.module.auth.forgot.password.token.submit.ResetPasswordByTokenPresenter;
import org.fossasia.openevent.app.module.auth.forgot.password.token.submit.contract.IResetPasswordByTokenView;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.reactivex.Completable;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@RunWith(JUnit4.class)
public class ResetPasswordByTokenPresenterTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock private IResetPasswordByTokenView resetPasswordByTokenView;
    @Mock private IAuthModel authModel;
    private final HostSelectionInterceptor interceptor = new HostSelectionInterceptor();

    private ResetPasswordByTokenPresenter resetPasswordByTokenPresenter;

    private static final String TOKEN = "330080303746871156724079532103783727154";
    private static final String PASSWORD = "password";
    private static final String CONFIRM_PASSWORD = "password";
    private static final SubmitToken SUBMIT_TOKEN = new SubmitToken(TOKEN, PASSWORD, CONFIRM_PASSWORD);

    @Before
    public void setUp() {
        resetPasswordByTokenPresenter = new ResetPasswordByTokenPresenter(authModel, interceptor);
        resetPasswordByTokenPresenter.attach(resetPasswordByTokenView);
        resetPasswordByTokenPresenter.getSubmitToken().setPassword(PASSWORD);
        resetPasswordByTokenPresenter.getSubmitToken().setToken(TOKEN);
        resetPasswordByTokenPresenter.getSubmitToken().setConfirmPassword(CONFIRM_PASSWORD);
    }

    @Test
    public void shouldDetachViewOnStop() {
        assertNotNull(resetPasswordByTokenPresenter.getView());

        resetPasswordByTokenPresenter.detach();

        assertTrue(resetPasswordByTokenPresenter.getDisposable().isDisposed());
    }

    @Test
    public void shouldSubmitTokenSuccessfully() {
        Mockito.when(authModel.submitToken(SUBMIT_TOKEN))
            .thenReturn(Completable.complete());

        InOrder inOrder = Mockito.inOrder(authModel, resetPasswordByTokenView);

        resetPasswordByTokenPresenter.start();
        resetPasswordByTokenPresenter.submitRequest();

        inOrder.verify(authModel).submitToken(SUBMIT_TOKEN);
        inOrder.verify(resetPasswordByTokenView).showProgress(true);
        inOrder.verify(resetPasswordByTokenView).onSuccess(any());
        inOrder.verify(resetPasswordByTokenView).showProgress(false);
    }

    @Test
    public void shouldShowSubmitTokenError() {
        String error = "Test Error";
        Mockito.when(authModel.submitToken(SUBMIT_TOKEN))
            .thenReturn(Completable.error(Logger.TEST_ERROR));

        InOrder inOrder = Mockito.inOrder(authModel, resetPasswordByTokenView);

        resetPasswordByTokenPresenter.start();
        resetPasswordByTokenPresenter.submitRequest();

        inOrder.verify(authModel).submitToken(SUBMIT_TOKEN);
        inOrder.verify(resetPasswordByTokenView).showProgress(true);
        inOrder.verify(resetPasswordByTokenView).showError(error);
        inOrder.verify(resetPasswordByTokenView).showProgress(false);
    }
}
