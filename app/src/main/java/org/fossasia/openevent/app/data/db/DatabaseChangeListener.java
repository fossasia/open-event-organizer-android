package org.fossasia.openevent.app.data.db;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.raizlabs.android.dbflow.runtime.DirectModelNotifier;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.fossasia.openevent.app.data.db.contract.IDatabaseChangeListener;

import io.reactivex.subjects.PublishSubject;

public class DatabaseChangeListener<T> implements IDatabaseChangeListener<T> {

    private PublishSubject<ModelChange<T>> publishSubject = PublishSubject.create();
    private DirectModelNotifier.ModelChangedListener<T> modelModelChangedListener;

    private Class<T> classType;

    public DatabaseChangeListener (Class<T> modelClass) {
        classType = modelClass;
    }

    public PublishSubject<ModelChange<T>> getNotifier() {
        return publishSubject;
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

    public static class ModelChange<T> {
        private T model;
        private BaseModel.Action action;

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

        @Override
        public String toString() {
            return "ModelChange{" +
                "model=" + model +
                ", action=" + action +
                '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ModelChange)) return false;

            ModelChange<?> that = (ModelChange<?>) o;

            return getModel() != null ? getModel().equals(that.getModel()) : that.getModel() == null && getAction() == that.getAction();

        }

        @Override
        public int hashCode() {
            int result = getModel() != null ? getModel().hashCode() : 0;
            result = 31 * result + (getAction().name() != null ? getAction().name().hashCode() : 0);
            return result;
        }
    }

}
