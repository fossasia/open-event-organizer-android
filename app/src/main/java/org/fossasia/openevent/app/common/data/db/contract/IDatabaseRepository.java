package org.fossasia.openevent.app.common.data.db.contract;

import com.raizlabs.android.dbflow.sql.language.SQLOperator;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

public interface IDatabaseRepository {

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
