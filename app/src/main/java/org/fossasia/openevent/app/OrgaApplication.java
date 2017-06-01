package org.fossasia.openevent.app;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import timber.log.Timber;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class OrgaApplication extends Application {

    private RefWatcher refWatcher;

    /**
     * Reference watcher to be used in detecting leaks in Fragments
     * @param context Context needed to access Application
     * @return ReferenceWatcher used to catch leaks in fragments
     */
    public static RefWatcher getRefWatcher(Context context) {
        OrgaApplication application = (OrgaApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        refWatcher = LeakCanary.install(this);

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
