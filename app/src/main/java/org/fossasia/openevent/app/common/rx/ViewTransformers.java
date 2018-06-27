package org.fossasia.openevent.app.common.rx;

import org.fossasia.openevent.app.common.mvp.view.Emptiable;
import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.ItemResult;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.common.mvp.view.Refreshable;
import org.fossasia.openevent.app.utils.ErrorUtils;

import java.util.List;

import io.reactivex.CompletableTransformer;
import io.reactivex.ObservableTransformer;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

@SuppressWarnings("PMD.TooManyMethods") // Will break cohesion if refactored
public final class ViewTransformers {

    private ViewTransformers() {
        // Never Called
    }

    public static <T> ObservableTransformer<T, T> schedule() {
        return observable -> observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> ObservableTransformer<T, T> dispose(CompositeDisposable compositeDisposable) {
        return observable -> observable.doOnSubscribe(compositeDisposable::add);
    }

    public static CompletableTransformer disposeCompletable(CompositeDisposable compositeDisposable) {
        return completable -> completable.doOnSubscribe(compositeDisposable::add);
    }

    public static <T, V extends Erroneous> ObservableTransformer<T, T> erroneous(V view) {
        return observable -> observable.doOnError(throwable -> view.showError(ErrorUtils.getMessage(throwable).toString()));
    }

    public static <V extends Erroneous> CompletableTransformer erroneousCompletable(V view) {
        return completable -> completable.doOnError(throwable -> view.showError(ErrorUtils.getMessage(throwable).toString()));
    }

    private static <T, V extends Progressive> ObservableTransformer<T, T> progressive(V view) {
        return observable -> observable
                .doOnSubscribe(disposable -> view.showProgress(true))
                .doFinally(() -> view.showProgress(false));
    }

    private static <V extends Progressive> CompletableTransformer progressiveCompletable(V view) {
        return observable -> observable
            .doOnSubscribe(disposable -> view.showProgress(true))
            .doFinally(() -> view.showProgress(false));
    }

    public static <T, V extends ItemResult<T>> ObservableTransformer<T, T> result(V view) {
        return observable -> observable.doOnNext(view::showResult);
    }

    private static <T, V extends Refreshable> ObservableTransformer<T, T> refreshable(V view, boolean forceReload) {
        return observable ->
            observable
                .doOnComplete(() -> {
                    if (forceReload) view.onRefreshComplete(true);
                })
                .doOnError(throwable -> {
                    if (forceReload) view.onRefreshComplete(false);
                });
    }

    public static <T, V extends ItemResult<T> & Erroneous> ObservableTransformer<T, T> erroneousResult(V view) {
        return observable -> observable
            .compose(result(view))
            .compose(erroneous(view));
    }

    public static <T, V extends Progressive & Erroneous> ObservableTransformer<T, T> progressiveErroneous(V view) {
        return observable -> observable
            .compose(progressive(view))
            .compose(erroneous(view));
    }

    public static <V extends Progressive & Erroneous> CompletableTransformer progressiveErroneousCompletable(V view) {
        return observable -> observable
            .compose(progressiveCompletable(view))
            .compose(erroneousCompletable(view));
    }

    public static <T, V extends Progressive & Erroneous & ItemResult<T>> ObservableTransformer<T, T>
    progressiveErroneousResult(V view) {
        return observable -> observable
            .compose(progressiveErroneous(view))
            .compose(result(view));
    }

    public static <T, V extends Progressive & Erroneous & Refreshable> ObservableTransformer<T, T>
    progressiveErroneousRefresh(V view, boolean forceReload) {
        return observable -> observable
            .compose(progressiveErroneous(view))
            .compose(refreshable(view, forceReload));
    }

    public static <T, V extends Progressive & Erroneous & Refreshable & ItemResult<T>> ObservableTransformer<T, T>
    progressiveErroneousResultRefresh(V view, boolean forceReload) {
        return observable -> observable
            .compose(progressiveErroneousResult(view))
            .compose(refreshable(view, forceReload));
    }

    public static <T, V extends Progressive & Refreshable> ObservableTransformer<T, T>
    progressiveRefresh(V view, boolean forceReload) {
        return observable -> observable
            .compose(progressive(view))
            .compose(refreshable(view, forceReload));
    }

    public static <T, V extends Emptiable<T>> SingleTransformer<List<T>, List<T>> emptiable(V view, List<T> items) {
        return observable -> observable
            .doOnSubscribe(disposable -> view.showEmptyView(false))
            .doOnSuccess(list -> {
                items.clear();
                items.addAll(list);
                view.showResults(items);
            })
            .doFinally(() -> view.showEmptyView(items.isEmpty()));
    }

}
