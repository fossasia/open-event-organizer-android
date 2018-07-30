package com.eventyay.organizer.robo.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;

import com.eventyay.organizer.core.auth.AuthActivity;
import com.eventyay.organizer.core.event.about.AboutEventActivity;
import com.eventyay.organizer.core.event.chart.ChartActivity;
import com.eventyay.organizer.core.event.create.CreateEventActivity;
import com.eventyay.organizer.core.main.MainActivity;
import com.eventyay.organizer.core.organizer.detail.OrganizerDetailActivity;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;

import java.util.Arrays;
import java.util.Collection;

@Ignore
public class ActivityInstanceTest<T extends Activity> extends BaseParameterTest {

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
        // UtilModel.blockNetwork();
    }

    @AfterClass
    public static void tearDown() {
        // UtilModel.releaseNetwork();
    }

    @ParameterizedRobolectricTestRunner.Parameters(name = "InstantiateActivity = {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
            {AuthActivity.class, null, null},
            {MainActivity.class, null, null},
            {ChartActivity.class, null, null},
            {AboutEventActivity.class, AboutEventActivity.EVENT_ID, 1L},
            {OrganizerDetailActivity.class, null, null},
            {CreateEventActivity.class, null, null}
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
