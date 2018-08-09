package com.eventyay.organizer.robo.ui;

import com.eventyay.organizer.robo.TestApplication;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.annotation.Config;

@SuppressWarnings({"PMD.AbstractClassWithoutAnyMethod", "PMD.AbstractClassWithoutAbstractMethod"})
@RunWith(ParameterizedRobolectricTestRunner.class)
@Config(sdk = Config.OLDEST_SDK, application = TestApplication.class)
public abstract class BaseParameterTest {

}
