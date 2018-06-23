package org.fossasia.openevent.app.core.main;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;

import org.fossasia.openevent.app.data.user.User;
import org.fossasia.openevent.app.data.user.UserRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.reactivex.Observable;

@RunWith(JUnit4.class)
public class OrganizerViewModelTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Mock
    private UserRepository userRepository;
    @Mock
    Observer<User> organizer;

    private OrganizerViewModel organizerViewModel;
    private static final User ORGANIZER = new User();

    @Before
    public void setUp() {
        organizerViewModel = new OrganizerViewModel(userRepository);
    }

    @Test
    public void shouldLoadOrganizerSuccessfully() {
        Mockito.when(userRepository.getOrganizer(false))
            .thenReturn(Observable.just(ORGANIZER));

        InOrder inOrder = Mockito.inOrder(userRepository, organizer);

        organizerViewModel.getOrganizer().observeForever(organizer);

        organizerViewModel.getOrganizer();

        inOrder.verify(userRepository).getOrganizer(false);
        inOrder.verify(organizer).onChanged(ORGANIZER);
    }
}
