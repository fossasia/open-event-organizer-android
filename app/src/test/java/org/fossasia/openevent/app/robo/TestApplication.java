package org.fossasia.openevent.app.robo;

import android.Manifest;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.OrgaProvider;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;

public class TestApplication extends OrgaApplication {

    @Override
    public void onCreate() {
        OrgaProvider.context = RuntimeEnvironment.application;

        // We want pervasive testing of the app
        Shadows.shadowOf(this).grantPermissions(Manifest.permission.CAMERA);

        super.onCreate();
    }

    @Override
    protected boolean isTestBuild() {
        return true;
    }
}
