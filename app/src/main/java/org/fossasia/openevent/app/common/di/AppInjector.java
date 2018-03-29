package org.fossasia.openevent.app.common.di;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.common.di.component.DaggerAppComponent;

import dagger.android.AndroidInjection;
import dagger.android.support.AndroidSupportInjection;
import dagger.android.support.HasSupportFragmentInjector;

public final class AppInjector {

    private AppInjector() {
        // Prevent Instantiation
    }

    public static void init(OrgaApplication orgaApplication) {
        DaggerAppComponent
            .create()
            .inject(orgaApplication);
        orgaApplication
            .registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                    handleActivity(activity);
                }

                @Override
                public void onActivityStarted(Activity activity) {
                    // Do Nothing
                }

                @Override
                public void onActivityResumed(Activity activity) {
                    // Do Nothing
                }

                @Override
                public void onActivityPaused(Activity activity) {
                    // Do Nothing
                }

                @Override
                public void onActivityStopped(Activity activity) {
                    // Do Nothing
                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
                    // Do Nothing
                }

                @Override
                public void onActivityDestroyed(Activity activity) {
                    // Do Nothing
                }
            });
    }

    private static void handleActivity(Activity activity) {
        if (activity instanceof HasSupportFragmentInjector) {
            AndroidInjection.inject(activity);
        }
        if (activity instanceof FragmentActivity) {
            ((FragmentActivity) activity).getSupportFragmentManager()
                .registerFragmentLifecycleCallbacks(
                    new FragmentManager.FragmentLifecycleCallbacks() {
                        @Override
                        public void onFragmentCreated(FragmentManager fm, Fragment f,
                                                      Bundle savedInstanceState) {
                            if (f instanceof Injectable) {
                                AndroidSupportInjection.inject(f);
                            }
                        }
                    }, true);
        }
    }
}
