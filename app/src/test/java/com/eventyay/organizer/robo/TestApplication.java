package com.eventyay.organizer.robo;

import android.Manifest;

import com.eventyay.organizer.OrgaApplication;
import com.eventyay.organizer.OrgaProvider;

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
