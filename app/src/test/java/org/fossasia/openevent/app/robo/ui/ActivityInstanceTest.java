package org.fossasia.openevent.app.robo.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;

import org.fossasia.openevent.app.BuildConfig;
import org.fossasia.openevent.app.common.data.UtilModel;
import org.fossasia.openevent.app.module.attendee.qrscan.ScanQRActivity;
import org.fossasia.openevent.app.module.auth.AuthActivity;
import org.fossasia.openevent.app.module.event.about.AboutEventActivity;
import org.fossasia.openevent.app.module.event.chart.ChartActivity;
import org.fossasia.openevent.app.module.main.MainActivity;
import org.fossasia.openevent.app.robo.TestApplication;
import org.fossasia.openevent.app.robo.rule.DatabaseTestRule;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collection;

@RunWith(ParameterizedRobolectricTestRunner.class)
@Config(constants = BuildConfig.class, application = TestApplication.class)
@SuppressWarnings({ "PMD.JUnit4TestShouldUseAfterAnnotation", "PMD.JUnit4TestShouldUseBeforeAnnotation" })
public class ActivityInstanceTest<T extends Activity> {

    @Rule
    public final DatabaseTestRule rule = DatabaseTestRule.create();

    private final Class<T> activityClass;
    private final String tag;
    private final Long id;

    public ActivityInstanceTest(Class<T> activityClass, String tag, Long id) {
        this.activityClass = activityClass;
        this.tag = tag;
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

    @ParameterizedRobolectricTestRunner.Parameters(name = "InstantiateActivity = {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { AuthActivity.class, null, null },
            { ScanQRActivity.class, MainActivity.EVENT_KEY, 1L },
            { MainActivity.class, null, null },
            { ChartActivity.class, null, null },
            {AboutEventActivity.class, AboutEventActivity.EVENT_ID, 1L }
        });
    }

    public static Configuration getOtherConfiguration(Activity activity) {
        int orientation = activity.getResources().getConfiguration().orientation;
        Configuration configuration = new Configuration();
        configuration.orientation = orientation == Configuration.ORIENTATION_LANDSCAPE ?
            Configuration.ORIENTATION_PORTRAIT : Configuration.ORIENTATION_LANDSCAPE;
        return configuration;
    }

    private static <T extends Activity> void testActivityCreation(Class<T> activityClass, Intent intent) {
        ActivityController<T> controller = Robolectric.buildActivity(activityClass, intent);
        Activity activity = controller.create().start().get();
        controller.configurationChange(getOtherConfiguration(activity));
        controller.pause().stop().destroy();
    }

    @Test
    public void shouldInstantiate() {
        if (tag == null) {
            testActivityCreation(activityClass, null);
        } else {
            Intent intent = new Intent();
            intent.putExtra(tag, id);
            testActivityCreation(activityClass, intent);
        }
    }

}
