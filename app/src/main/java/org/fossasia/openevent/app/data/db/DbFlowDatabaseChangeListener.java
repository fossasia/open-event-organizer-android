package org.fossasia.openevent.app.data.db;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.raizlabs.android.dbflow.runtime.DirectModelNotifier;
import com.raizlabs.android.dbflow.structure.BaseModel;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.ReplaySubject;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class DbFlowDatabaseChangeListener<T> implements DatabaseChangeListener<T> {

    private final Class<T> classType;
    private ReplaySubject<ModelChange<T>> replaySubject;
    private Disposable disposable;

    private DirectModelNotifier.ModelChangedListener<T> modelModelChangedListener;

    public DbFlowDatabaseChangeListener(Class<T> modelClass) {
        classType = modelClass;
    }

    @Override
    public Observable<ModelChange<T>> getNotifier() {
        return replaySubject
            .doOnSubscribe(disposable -> this.disposable = disposable)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void startListening() {
        if (disposable == null || disposable.isDisposed())
            replaySubject = ReplaySubject.create();

        modelModelChangedListener = new DirectModelNotifier.ModelChangedListener<T>() {

            @Override
            public void onTableChanged(@Nullable Class<?> aClass, @NonNull BaseModel.Action action) {
                replaySubject.onNext(new ModelChange<>(null, action));
            }

            @Override
            public void onModelChanged(@NonNull T model, @NonNull BaseModel.Action action) {
                replaySubject.onNext(new ModelChange<>(model, action));
            }
        };

        DirectModelNotifier.get().registerForModelChanges(classType, modelModelChangedListener);
    }

    @Override
    public void stopListening() {
        if (modelModelChangedListener != null)
            DirectModelNotifier.get().unregisterForModelChanges(classType, modelModelChangedListener);
        replaySubject.onComplete();
    }

    // Internal ModelChange

    @Getter
    @AllArgsConstructor
    public static class ModelChange<T> {
        private final T model;
        private final BaseModel.Action action;

        @Override @SuppressWarnings("EqualsHashCode")
        public boolean equals(Object obj) {
            return obj instanceof ModelChange && ((ModelChange) obj).action.equals(action) && ((ModelChange) obj).model.equals(model);
        }
    }

}
