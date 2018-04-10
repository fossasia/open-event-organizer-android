package org.fossasia.openevent.app.core.presenter;

import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.user.User;
import org.fossasia.openevent.app.core.organizer.detail.OrganizerDetailPresenter;
import org.fossasia.openevent.app.core.organizer.detail.OrganizerDetailView;
import org.fossasia.openevent.app.data.user.UserRepositoryImpl;
import org.junit.After;
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

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class OrganizerDetailPresenterTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private OrganizerDetailView organizerDetailView;
    @Mock
    private UserRepositoryImpl userRepository;
    private OrganizerDetailPresenter organizerDetailPresenter;

    private static final User USER = new User();

    @Before
    public void setUp() {
        organizerDetailPresenter = new OrganizerDetailPresenter(userRepository);
        organizerDetailPresenter.attach(organizerDetailView);

        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldLoadOrganizerSuccessfully() {
        when(userRepository.getOrganizer(anyBoolean())).thenReturn(Observable.just(USER));

        organizerDetailPresenter.loadOrganizer(false);

        InOrder inOrder = Mockito.inOrder(organizerDetailView);

        inOrder.verify(organizerDetailView).showProgress(true);
        inOrder.verify(organizerDetailView).showResult(USER);
        inOrder.verify(organizerDetailView).showProgress(false);
    }

    @Test
    public void shouldShowErrorOnOrganizerLoadFailure() {
        when(userRepository.getOrganizer(anyBoolean())).thenReturn(Observable.error(new Throwable("Error")));

        organizerDetailPresenter.loadOrganizer(false);

        InOrder inOrder = Mockito.inOrder(organizerDetailView);

        inOrder.verify(organizerDetailView).showProgress(true);
        inOrder.verify(organizerDetailView).showError("Error");
        inOrder.verify(organizerDetailView).showProgress(false);
    }

    @Test
    public void shouldShowOrganizerDetailsOnSwipeRefreshSuccess() {
        when(userRepository.getOrganizer(true)).thenReturn(Observable.just(USER));

        organizerDetailPresenter.loadOrganizer(true);

        verify(organizerDetailView).showResult(USER);
    }

    @Test
    public void shouldShowErrorMessageOnSwipeRefreshError() {
        when(userRepository.getOrganizer(true)).thenReturn(Observable.error(Logger.TEST_ERROR));

        organizerDetailPresenter.loadOrganizer(true);

        verify(organizerDetailView).showError(Logger.TEST_ERROR.getMessage());
    }

    @Test
    public void testProgressbarOnSwipeRefreshSuccess() {
        when(userRepository.getOrganizer(true)).thenReturn(Observable.just(USER));

        organizerDetailPresenter.loadOrganizer(true);

        InOrder inOrder = Mockito.inOrder(organizerDetailView);

        inOrder.verify(organizerDetailView).showProgress(true);
        inOrder.verify(organizerDetailView).onRefreshComplete(true);
        inOrder.verify(organizerDetailView).showProgress(false);
    }

    @Test
    public void testProgressbarOnSwipeRefreshError() {
        when(userRepository.getOrganizer(true)).thenReturn(Observable.error(Logger.TEST_ERROR));

        organizerDetailPresenter.loadOrganizer(true);

        InOrder inOrder = Mockito.inOrder(organizerDetailView);

        inOrder.verify(organizerDetailView).showProgress(true);
        inOrder.verify(organizerDetailView).onRefreshComplete(false);
        inOrder.verify(organizerDetailView).showProgress(false);
    }
}
