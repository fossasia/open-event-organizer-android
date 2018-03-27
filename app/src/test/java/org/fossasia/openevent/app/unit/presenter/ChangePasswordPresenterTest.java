package org.fossasia.openevent.app.unit.presenter;

import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.IAuthModel;
import org.fossasia.openevent.app.data.models.ChangePassword;
import org.fossasia.openevent.app.data.network.HostSelectionInterceptor;
import org.fossasia.openevent.app.core.organizer.password.ChangePasswordPresenter;
import org.fossasia.openevent.app.core.organizer.password.IChangePasswordView;
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
import static org.mockito.Mockito.verify;


@RunWith(JUnit4.class)
public class ChangePasswordPresenterTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock private IAuthModel authModel;
    @Mock private IChangePasswordView changePasswordView;
    private final HostSelectionInterceptor interceptor = new HostSelectionInterceptor();

    private ChangePasswordPresenter changePasswordPresenter;

    private static final String OLD_PASSWORD = "oldTest";
    private static final String OLD_WRONG_PASSWORD = "oldWrongTest";
    private static final String NEW_PASSWORD = "newTest";
    private static final String CONFIRM_NEW_PASSWORD = "newTest";
    private static final String CONFIRM_NEW_WRONG_PASSWORD = "newWrongTest";

    private static final ChangePassword CHANGE_PASSWORD = new ChangePassword(OLD_PASSWORD, NEW_PASSWORD, CONFIRM_NEW_PASSWORD);

    @Before
    public void setUp() {
        changePasswordPresenter = new ChangePasswordPresenter(authModel, interceptor);
        changePasswordPresenter.attach(changePasswordView);
    }

    @Test
    public void shouldDetachViewOnStop() {
        assertNotNull(changePasswordPresenter.getView());

        changePasswordPresenter.detach();

        assertTrue(changePasswordPresenter.getDisposable().isDisposed());
    }

    @Test
    public void shouldChangePasswordSuccessfully() {
        Mockito.when(authModel.changePassword(CHANGE_PASSWORD))
            .thenReturn(Completable.complete());

        InOrder inOrder = Mockito.inOrder(authModel, changePasswordView);

        changePasswordPresenter.start();
        changePasswordPresenter.changePasswordRequest(OLD_PASSWORD, NEW_PASSWORD, CONFIRM_NEW_PASSWORD);

        inOrder.verify(authModel).changePassword(CHANGE_PASSWORD);
        inOrder.verify(changePasswordView).showProgress(true);
        inOrder.verify(changePasswordView).onSuccess(any());
        inOrder.verify(changePasswordView).showProgress(false);
    }

    @Test
    public void shouldShowChangePasswordError() {
        String error = "Test Error";
        Mockito.when(authModel.changePassword(CHANGE_PASSWORD))
            .thenReturn(Completable.error(Logger.TEST_ERROR));

        InOrder inOrder = Mockito.inOrder(authModel, changePasswordView);

        changePasswordPresenter.start();
        changePasswordPresenter.changePasswordRequest(OLD_PASSWORD, NEW_PASSWORD, CONFIRM_NEW_PASSWORD);

        inOrder.verify(authModel).changePassword(CHANGE_PASSWORD);
        inOrder.verify(changePasswordView).showProgress(true);
        inOrder.verify(changePasswordView).showError(error);
        inOrder.verify(changePasswordView).showProgress(false);
    }

    @Test
    public void shouldAcceptSameNewAndConfirmPassword() {
        Mockito.when(authModel.changePassword(CHANGE_PASSWORD))
            .thenReturn(Completable.complete());

        changePasswordPresenter.start();
        changePasswordPresenter.changePasswordRequest(OLD_PASSWORD, NEW_PASSWORD, CONFIRM_NEW_PASSWORD);

        verify(changePasswordView, Mockito.never()).showError(any());
        verify(authModel).changePassword(CHANGE_PASSWORD);
    }

    @Test
    public void shouldShowErrorOnDifferentNewAndConfirmPassword() {
        Mockito.when(authModel.changePassword(CHANGE_PASSWORD))
            .thenReturn(Completable.complete());

        changePasswordPresenter.start();
        changePasswordPresenter.changePasswordRequest(OLD_PASSWORD, NEW_PASSWORD, CONFIRM_NEW_WRONG_PASSWORD);

        verify(changePasswordView).showError(any());
        verify(authModel, Mockito.never()).changePassword(any());
    }

    @Test
    public void shouldAcceptCorrectOldPassword() {
        Mockito.when(authModel.changePassword(CHANGE_PASSWORD))
            .thenReturn(Completable.complete());

        InOrder inOrder = Mockito.inOrder(authModel, changePasswordView);

        changePasswordPresenter.start();
        changePasswordPresenter.changePasswordRequest(OLD_PASSWORD, NEW_PASSWORD, CONFIRM_NEW_PASSWORD);

        inOrder.verify(authModel).changePassword(CHANGE_PASSWORD);
        inOrder.verify(changePasswordView).showProgress(true);
        inOrder.verify(changePasswordView).onSuccess(any());
        inOrder.verify(changePasswordView).showProgress(false);
    }

    @Test
    public void shouldShowErrorOnWrongOldPassword() {
        String error = "Test Error";

        Mockito.when(authModel.changePassword(new ChangePassword(any(), NEW_PASSWORD, CONFIRM_NEW_PASSWORD)))
            .thenReturn(Completable.error(Logger.TEST_ERROR));

        Mockito.when(authModel.changePassword(CHANGE_PASSWORD))
            .thenReturn(Completable.complete());

        InOrder inOrder = Mockito.inOrder(authModel, changePasswordView);

        changePasswordPresenter.start();
        changePasswordPresenter.changePasswordRequest(OLD_WRONG_PASSWORD, NEW_PASSWORD, CONFIRM_NEW_PASSWORD);

        inOrder.verify(authModel).changePassword(any());
        inOrder.verify(changePasswordView).showError(error);
    }
}

