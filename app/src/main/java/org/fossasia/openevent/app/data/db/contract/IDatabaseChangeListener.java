package org.fossasia.openevent.app.data.db.contract;

import org.fossasia.openevent.app.data.db.DatabaseChangeListener;

import io.reactivex.subjects.PublishSubject;

public interface IDatabaseChangeListener<T> {

    PublishSubject<DatabaseChangeListener.ModelChange<T>> getNotifier();

    void startListening();

    void stopListening();

}
