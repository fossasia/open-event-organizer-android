package com.eventyay.organizer.data.db;

import io.reactivex.Observable;

public interface DatabaseChangeListener<T> {

    Observable<DbFlowDatabaseChangeListener.ModelChange<T>> getNotifier();

    void startListening();

    void stopListening();
}
