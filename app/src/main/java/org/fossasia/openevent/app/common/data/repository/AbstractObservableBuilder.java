package org.fossasia.openevent.app.common.data.repository;

import android.support.annotation.NonNull;

import org.fossasia.openevent.app.common.data.contract.IUtilModel;
import org.fossasia.openevent.app.common.Constants;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

final class AbstractObservableBuilder<T> {

    private IUtilModel utilModel;
    private boolean reload;
    private Observable<T> diskObservable;
    private Observable<T> networkObservable;

    @Inject
    AbstractObservableBuilder(IUtilModel utilModel) {
        this.utilModel = utilModel;
    }

    AbstractObservableBuilder<T> reload(boolean reload) {
        this.reload = reload;

        return this;
    }

    AbstractObservableBuilder<T> withDiskObservable(Observable<T> diskObservable) {
        this.diskObservable = diskObservable;

        return this;
    }

    AbstractObservableBuilder<T> withNetworkObservable(Observable<T> networkObservable) {
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
        if (utilModel.isConnected())
            return networkObservable
                .doOnNext(item -> Timber.d("Loaded %s From Network on Thread %s",
                    item.getClass(), Thread.currentThread().getName()));
        else
            return Observable.error(new Throwable(Constants.NO_NETWORK));
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
                .compose(applySchedulers());
    }


}
