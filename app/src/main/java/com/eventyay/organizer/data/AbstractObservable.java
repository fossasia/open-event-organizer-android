package com.eventyay.organizer.data;

import androidx.annotation.NonNull;

import com.eventyay.organizer.common.Constants;
import com.eventyay.organizer.data.network.ConnectionStatus;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public final class AbstractObservable {

    private final ConnectionStatus connectionStatus;

    public static final class AbstractObservableBuilder<T> {
        private final ConnectionStatus connectionStatus;
        private RateLimiter<String> rateLimiter;
        private boolean reload;
        private String rateLimiterKey;
        private Observable<T> diskObservable;
        private Observable<T> networkObservable;

        public AbstractObservableBuilder(ConnectionStatus connectionStatus) {
            this.connectionStatus = connectionStatus;
        }

        public AbstractObservableBuilder<T> reload(boolean reload) {
            this.reload = reload;

            return this;
        }

        public AbstractObservableBuilder<T> withRateLimiterConfig(String rateLimiterKey, RateLimiter<String> rateLimiter) {
            this.rateLimiter = rateLimiter;
            this.rateLimiterKey = rateLimiterKey;

            return this;
        }

        public AbstractObservableBuilder<T> withDiskObservable(Observable<T> diskObservable) {
            this.diskObservable = diskObservable;

            return this;
        }

        public AbstractObservableBuilder<T> withNetworkObservable(Observable<T> networkObservable) {
            this.networkObservable = networkObservable;

            return this;
        }

        @NonNull
        private Callable<Observable<T>> getReloadCallable() {
            return () -> {
                if (reload)
                    return Observable.empty();
                else
                    return diskObservable
                        .doOnNext(item -> Timber.d("Loaded %s From Disk on Thread %s",
                            item.getClass(), Thread.currentThread().getName()));
            };
        }

        @NonNull
        private Observable<T> getConnectionObservable() {
            if (connectionStatus.isConnected()) {
                if (reload || rateLimiter == null || rateLimiter.shouldFetch(rateLimiterKey)) {
                    return networkObservable
                        .doOnNext(item -> Timber.d("Loaded %s From Network on Thread %s",
                            item.getClass(), Thread.currentThread().getName()));
                }
                // this statement will only be executed when disk returned empty and data
                // was refreshed within the last 10 minutes.
                return Observable.empty();
            } else {
                if (rateLimiter != null)
                    rateLimiter.reset(rateLimiterKey);

                return Observable.error(new Throwable(Constants.NO_NETWORK));
            }
        }

        @NonNull
        private <V> ObservableTransformer<V, V> applySchedulers() {
            return observable -> observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        }

        @NonNull
        public Observable<T> build() {
            if (diskObservable == null || networkObservable == null)
                throw new IllegalStateException("Network or Disk observable not provided");

            return Observable
                .defer(getReloadCallable())
                .switchIfEmpty(getConnectionObservable())
                .toList()
                //.flatMap(items -> diskObservable.toList())
                .flattenAsObservable(items -> items)
                .compose(applySchedulers());
        }
    }

    @Inject
    public AbstractObservable(ConnectionStatus connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public <T> AbstractObservableBuilder<T> of(Class<T> clazz) {
        return new AbstractObservableBuilder<>(connectionStatus);
    }
}
