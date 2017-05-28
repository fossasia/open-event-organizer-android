package org.fossasia.openevent.app;

import android.app.Application;
import android.os.StrictMode;
import android.util.Log;

import timber.log.Timber;

public class OrgaApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy
                (new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyDeath()
                    .build());
            StrictMode.setVmPolicy
                (new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyDeath()
                    .build());

            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new ReleaseLogTree());
        }
    }

    private static class ReleaseLogTree extends Timber.Tree {

        @Override
        protected void log(int priority, String tag, String message, Throwable throwable) {
            if(priority == Log.DEBUG || priority == Log.VERBOSE)
                return;

            // Report to crashing SDK in future
            Timber.log(priority, tag, message, throwable);
        }
    }

}
