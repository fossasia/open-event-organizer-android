package com.eventyay.organizer;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.NonNull;
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

import com.eventyay.organizer.common.di.AppInjector;
import com.eventyay.organizer.data.db.configuration.OrgaDatabase;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import io.sentry.Sentry;
import io.sentry.android.AndroidSentryClientFactory;
import io.sentry.event.BreadcrumbBuilder;
import timber.log.Timber;

public class OrgaApplication extends MultiDexApplication implements HasActivityInjector {

    private static final AtomicBoolean CREATED = new AtomicBoolean();

    private RefWatcher refWatcher;

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    /**
     * Reference watcher to be used in detecting leaks in Fragments
     *
     * @param context Context needed to access Application
     * @return ReferenceWatcher used to catch leaks in fragments
     */
    public static RefWatcher getRefWatcher(Context context) {
        return ((OrgaApplication) context.getApplicationContext()).refWatcher;
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

        refWatcher = setupLeakCanary();
        if (CREATED.getAndSet(true))
            return;

        AppInjector.init(this);

        if (isTestBuild())
            return;

        initializeDatabase(this);
        AndroidThreeTen.init(this);

        if (BuildConfig.DEBUG) {
            Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                    .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                    .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                    .build());

            StrictMode.ThreadPolicy.Builder policyBuilder = new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyDeath();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                /**
                 * https://medium.com/@elye.project/walk-through-hell-with-android-strictmode-7e8605168032
                 * "If you really like penaltyDeath(). Perhaps we could turn that permitDiskReads() by default.
                 * Most of the detected violation from other sources that really need suppression are Disk Reading error."
                 */
                policyBuilder
                    .permitDiskReads();
            }
            StrictMode.setThreadPolicy(policyBuilder.build());
            StrictMode.setVmPolicy(
                new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());

            Timber.plant(new Timber.DebugTree());
        } else {
            // Sentry DSN must be defined as environment variable
            // https://docs.sentry.io/clients/java/config/#setting-the-dsn-data-source-name
            Sentry.init(new AndroidSentryClientFactory(getApplicationContext()));

            Timber.plant(new ReleaseLogTree());
        }
    }

    protected RefWatcher setupLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return RefWatcher.DISABLED;
        }
        return LeakCanary.install(this);
    }

    protected boolean isTestBuild() {
        return false;
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }

    private static class ReleaseLogTree extends Timber.Tree {

        @Override
        protected void log(int priority, String tag, @NonNull String message, Throwable throwable) {
            if (priority == Log.DEBUG || priority == Log.VERBOSE)
                return;

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
