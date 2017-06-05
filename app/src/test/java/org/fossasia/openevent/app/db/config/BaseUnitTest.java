package org.fossasia.openevent.app.db.config;

import android.content.Context;

import org.fossasia.openevent.app.BuildConfig;
import org.fossasia.openevent.app.TestOrgaApplication;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class,
    application = TestOrgaApplication.class,
    assetDir = "build/intermediates/classes/test/")
public abstract class BaseUnitTest {

    @Rule
    public final DatabaseTestRule dbRule = DatabaseTestRule.create();

    public Context getContext() {
        return RuntimeEnvironment.application;
    }
}
