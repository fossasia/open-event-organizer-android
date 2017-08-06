package org.fossasia.openevent.app.common.data.db;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.raizlabs.android.dbflow.runtime.DirectModelNotifier;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.fossasia.openevent.app.common.data.db.contract.IDatabaseChangeListener;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import lombok.Data;

public class DatabaseChangeListener<T> implements IDatabaseChangeListener<T> {

    private final PublishSubject<ModelChange<T>> publishSubject = PublishSubject.create();
    private final Class<T> classType;

    private DirectModelNotifier.ModelChangedListener<T> modelModelChangedListener;

    public DatabaseChangeListener(Class<T> modelClass) {
        classType = modelClass;
    }

    public Observable<ModelChange<T>> getNotifier() {
        return publishSubject
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    public void startListening() {
        modelModelChangedListener = new DirectModelNotifier.ModelChangedListener<T>() {

            @Override
            public void onTableChanged(@Nullable Class<?> aClass, @NonNull BaseModel.Action action) {
                // No action to be taken
            }

            @Override
            public void onModelChanged(@NonNull T model, @NonNull BaseModel.Action action) {
                publishSubject.onNext(new ModelChange<>(model, action));
            }
        };

        DirectModelNotifier.get().registerForModelChanges(classType, modelModelChangedListener);
    }

    public void stopListening() {
        if (modelModelChangedListener != null)
            DirectModelNotifier.get().unregisterForModelChanges(classType, modelModelChangedListener);
    }

    // Internal ModelChange

    @Data
    public static class ModelChange<T> {
        private final T model;
        private final BaseModel.Action action;

        public ModelChange(T model, BaseModel.Action action) {
            this.model = model;
            this.action = action;
        }

        public T getModel() {
            return model;
        }

        public BaseModel.Action getAction() {
            return action;
        }
    }

}
