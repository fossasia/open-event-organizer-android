package org.fossasia.openevent.app.data.db.contract;

import com.raizlabs.android.dbflow.sql.language.SQLOperator;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

public interface IDatabaseRepository {

    <T extends BaseModel> Observable<T> getItem(Class<T> typeClass, SQLOperator... conditions);

    <T extends BaseModel> Observable<T> getAllItems(Class<T> typeClass);

    <T extends BaseModel> Completable save(T item);

    <T extends BaseModel> Completable saveList(Class<T> itemClass, List<T> items);

}
