package org.fossasia.openevent.app.data.db.contract;

import org.fossasia.openevent.app.data.db.DatabaseChangeListener;

import io.reactivex.Observable;

public interface IDatabaseChangeListener<T> {

    Observable<DatabaseChangeListener.ModelChange<T>> getNotifier();

    void startListening();

    void stopListening();

}
