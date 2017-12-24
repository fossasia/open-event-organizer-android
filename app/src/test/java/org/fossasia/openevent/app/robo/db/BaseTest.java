package org.fossasia.openevent.app.robo.db;

import android.app.Application;

import org.fossasia.openevent.app.robo.rule.DatabaseTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Config.OLDEST_SDK, application = Application.class)
public abstract class BaseTest {

    @Rule
    public final DatabaseTestRule dbRule = DatabaseTestRule.create();

    @Before
    public final void before() {
        setUp();
    }

    public abstract void setUp();

}
