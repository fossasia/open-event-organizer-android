package org.fossasia.openevent.app;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.picasso.Picasso;

import org.fossasia.openevent.app.common.di.component.AppComponent;
import org.fossasia.openevent.app.common.di.component.DaggerAppComponent;
import org.fossasia.openevent.app.common.di.module.AndroidModule;

import javax.inject.Inject;

import timber.log.Timber;

public class OrgaApplication extends Application {

    private RefWatcher refWatcher;
    private AppComponent appComponent;

    @Inject
    Picasso picasso;

    /**
     * Reference watcher to be used in detecting leaks in Fragments
     * @param context Context needed to access Application
     * @return ReferenceWatcher used to catch leaks in fragments
     */
    public static RefWatcher getRefWatcher(Context context) {
        OrgaApplication application = (OrgaApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    public static AppComponent getAppComponent(Context context) {
        OrgaApplication application = (OrgaApplication) context.getApplicationContext();
        return application.appComponent;
    }

    public static void initializeDatabase(Context context) {
        FlowManager.init(context);
    }

    public static void destroyDatabase() {
        FlowManager.destroy();
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

        initializeDatabase(this);

        if (!isTestBuild()) {
            appComponent = DaggerAppComponent.builder()
                .androidModule(new AndroidModule(this))
                .build();

            appComponent.inject(this);

            Picasso.setSingletonInstance(picasso);

            if (BuildConfig.DEBUG) {
                Stetho.initializeWithDefaults(this);

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
    }

    public boolean isTestBuild() {
        return false;
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
