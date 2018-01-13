package org.fossasia.openevent.app.robo.ui;

import android.support.v4.app.Fragment;

import org.fossasia.openevent.app.common.data.UtilModel;
import org.fossasia.openevent.app.module.attendee.list.AttendeesFragment;
import org.fossasia.openevent.app.module.auth.forgot.password.token.request.ForgotPasswordFragment;
import org.fossasia.openevent.app.module.auth.forgot.password.token.submit.ResetPasswordByTokenFragment;
import org.fossasia.openevent.app.module.auth.login.LoginFragment;
import org.fossasia.openevent.app.module.auth.signup.SignUpFragment;
import org.fossasia.openevent.app.module.event.dashboard.EventDashboardFragment;
import org.fossasia.openevent.app.module.event.list.EventListFragment;
import org.fossasia.openevent.app.module.settings.SettingsFragment;
import org.fossasia.openevent.app.module.ticket.create.CreateTicketFragment;
import org.fossasia.openevent.app.module.ticket.list.TicketsFragment;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.shadows.support.v4.SupportFragmentController;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;

@SuppressWarnings({"PMD.JUnit4TestShouldUseAfterAnnotation", "PMD.JUnit4TestShouldUseBeforeAnnotation"})
public class FragmentInstanceTest<T extends Fragment> extends BaseParameterTest {

    private final Class<T> testFragmentClass;
    private final long id;

    public FragmentInstanceTest(Class<T> testFragmentClass, long id) {
        this.testFragmentClass = testFragmentClass;
        this.id = id;
    }

    @BeforeClass
    public static void setUp() {
        UtilModel.blockNetwork();
    }

    @AfterClass
    public static void tearDown() {
        UtilModel.releaseNetwork();
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
            {ForgotPasswordFragment.class, -1},
            {ResetPasswordByTokenFragment.class, -1}
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
        SupportFragmentController.of(fragment).create().start().pause().stop().destroy();
    }

    @Test
    public void shouldInstantiate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        testFragmentCreation(getFragment());
    }

}
