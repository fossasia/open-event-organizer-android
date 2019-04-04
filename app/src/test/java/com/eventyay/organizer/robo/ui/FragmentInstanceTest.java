package com.eventyay.organizer.robo.ui;

import androidx.fragment.app.Fragment;

import com.eventyay.organizer.common.ContextManager;
import com.eventyay.organizer.core.attendee.list.AttendeesFragment;
import com.eventyay.organizer.core.auth.reset.ResetPasswordFragment;
import com.eventyay.organizer.core.auth.login.LoginFragment;
import com.eventyay.organizer.core.auth.signup.SignUpFragment;
import com.eventyay.organizer.core.event.dashboard.EventDashboardFragment;
import com.eventyay.organizer.core.event.list.EventListFragment;
import com.eventyay.organizer.core.settings.SettingsFragment;
import com.eventyay.organizer.core.ticket.create.CreateTicketFragment;
import com.eventyay.organizer.core.ticket.list.TicketsFragment;
import com.eventyay.organizer.data.event.Event;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.robolectric.ParameterizedRobolectricTestRunner;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;

@Ignore
@SuppressWarnings({"PMD.JUnit4TestShouldUseAfterAnnotation", "PMD.JUnit4TestShouldUseBeforeAnnotation"})
public class FragmentInstanceTest<T extends Fragment> extends BaseParameterTest {

    private final Class<T> testFragmentClass;
    private final long id;

    public FragmentInstanceTest(Class<T> testFragmentClass, long id) {
        this.testFragmentClass = testFragmentClass;
        this.id = id;
        if (testFragmentClass == CreateTicketFragment.class) {
            setUpMockEvent();
        }
    }

    private void setUpMockEvent() {
        Event event = new Event();
        event.timezone = "UTC";
        event.endsAt = "2018-12-14T23:59:59.123456+00:00";
        ContextManager.setSelectedEvent(event);
    }

    @BeforeClass
    public static void setUp() {
        // UtilModel.blockNetwork();
    }

    @AfterClass
    public static void tearDown() {
        // UtilModel.releaseNetwork();
        ContextManager.setSelectedEvent(null);
    }

    @ParameterizedRobolectricTestRunner.Parameters(name = "InstantiateFragment = {0}")
    public static Collection<Object[]> data() {
        // TODO: Re-enable once Roboelectric is fixed
        return Arrays.asList(new Object[][]{
            {EventDashboardFragment.class, 1},
            {EventListFragment.class, -1},
            //{AttendeeCheckInFragment.class, 1},
            {SettingsFragment.class, -1},
            {CreateTicketFragment.class, -1},
            //{TicketDetailFragment.class, 1},
            {TicketsFragment.class, 1},
            {AttendeesFragment.class, 1},
            {LoginFragment.class, -1},
            {SignUpFragment.class, -1},
            {ResetPasswordFragment.class, -1}
        });
    }

    private Fragment getFragment() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (id == -1) {
            return (Fragment) testFragmentClass.getDeclaredMethod("newInstance").invoke(null);
        } else {
            return (Fragment) testFragmentClass.getDeclaredMethod("newInstance", long.class).invoke(null, id);
        }
    }

    private static void testFragmentCreation(Fragment fragment) {
        // SupportFragmentController.of(fragment).create().start().pause().stop().destroy();
    }

    @Test
    public void shouldInstantiate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        testFragmentCreation(getFragment());
    }

}
