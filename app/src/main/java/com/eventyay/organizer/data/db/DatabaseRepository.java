package com.eventyay.organizer.data.db;

import com.raizlabs.android.dbflow.sql.language.SQLOperator;
import io.reactivex.Completable;
import io.reactivex.Observable;
import java.util.List;

public interface DatabaseRepository {

    <T> Observable<T> getItems(Class<T> typeClass, SQLOperator... conditions);

    <T> Observable<T> getAllItems(Class<T> typeClass);

    <T> Completable save(Class<T> classType, T item);

    <T> Completable saveList(Class<T> itemClass, List<T> items);

    <T> Completable update(Class<T> classType, T item);

    <T> Completable delete(Class<T> typeClass, SQLOperator... conditions);

    <T> Completable deleteAll(Class<T> typeClass);

    @SuppressWarnings("unchecked")
    Completable deleteAll(Class<?>... typeClass);
}
