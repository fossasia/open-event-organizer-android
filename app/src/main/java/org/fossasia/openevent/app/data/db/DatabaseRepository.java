package org.fossasia.openevent.app.data.db;

import com.raizlabs.android.dbflow.rx2.language.RXSQLite;
import com.raizlabs.android.dbflow.sql.language.SQLOperator;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.fossasia.openevent.app.data.db.contract.IDatabaseRepository;

import io.reactivex.Completable;
import io.reactivex.Observable;

public class DatabaseRepository implements IDatabaseRepository {

    @Override
    public <T extends BaseModel> Observable<T> getItem(Class<T> typeClass, SQLOperator... conditions) {
        return RXSQLite.rx(SQLite.select()
            .from(typeClass)
            .where(conditions))
            .querySingle()
            .toObservable();
    }

    @Override
    public <T extends BaseModel> Observable<T> getAllItems(Class<T> typeClass) {
        return RXSQLite.rx(SQLite.select()
            .from(typeClass))
            .queryList()
            .flattenAsObservable(items -> items);
    }

    @Override
    public <T extends BaseModel> Completable save(T item) {
        return Completable.fromAction(item::save);
    }
}
