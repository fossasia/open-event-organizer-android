package org.fossasia.openevent.app;

import android.content.Context;
import android.os.StrictMode;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.raizlabs.android.dbflow.config.DatabaseConfig;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.runtime.DirectModelNotifier;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.picasso.Picasso;

import org.fossasia.openevent.app.common.app.di.component.AppComponent;
import org.fossasia.openevent.app.common.app.di.component.DaggerAppComponent;
import org.fossasia.openevent.app.common.app.di.module.AndroidModule;
import org.fossasia.openevent.app.common.data.db.configuration.OrgaDatabase;

import javax.inject.Inject;

import io.sentry.Sentry;
import io.sentry.android.AndroidSentryClientFactory;
import io.sentry.event.BreadcrumbBuilder;
import timber.log.Timber;

public class OrgaApplication extends MultiDexApplication {

    private static volatile AppComponent appComponent;
    private RefWatcher refWatcher;

    @Inject
    Picasso picasso;

    /**
     * Reference watcher to be used in detecting leaks in Fragments
     *
     * @param context Context needed to access Application
     * @return ReferenceWatcher used to catch leaks in fragments
     */
    public static RefWatcher getRefWatcher(Context context) {
        OrgaApplication application = (OrgaApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    public static AppComponent getAppComponent() {
        return appComponent;
    }

    public static void initializeDatabase(Context context) {
        FlowManager.init(new FlowConfig.Builder(context)
            .addDatabaseConfig(
                new DatabaseConfig.Builder(OrgaDatabase.class)
                    .modelNotifier(DirectModelNotifier.get())
                    .build()
            ).build());
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
            AndroidThreeTen.init(this);

            appComponent = DaggerAppComponent.builder()
                .androidModule(new AndroidModule())
                .build();

            appComponent.inject(this);

            Picasso.setSingletonInstance(picasso);

            if (BuildConfig.DEBUG) {
                Stetho.initializeWithDefaults(this);

                StrictMode.setThreadPolicy(
                    new StrictMode.ThreadPolicy.Builder()
                        .detectAll()
                        .penaltyDeath()
                        .build());
                StrictMode.setVmPolicy(
                    new StrictMode.VmPolicy.Builder()
                        .detectAll()
                        .penaltyDeath()
                        .build());

                Timber.plant(new Timber.DebugTree());
            } else {
                // Sentry DSN must be defined as environment variable
                // https://docs.sentry.io/clients/java/config/#setting-the-dsn-data-source-name
                Sentry.init(new AndroidSentryClientFactory(getApplicationContext()));

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
            if (priority == Log.DEBUG || priority == Log.VERBOSE)
                return;

            // Report to crashing SDK in future
            Timber.log(priority, tag, message, throwable);

            if (priority == Log.INFO) {
                Log.d("Sentry", "Sending sentry breadcrumb");
                Sentry.getContext().recordBreadcrumb(new BreadcrumbBuilder().setMessage(message).build());
            }

            if (priority == Log.ERROR) {
                if (throwable == null)
                    Sentry.capture(message);
                else
                    Sentry.capture(throwable);
                Log.d("Sentry", "Sending sentry error event " + message);
            }
        }
    }

}
