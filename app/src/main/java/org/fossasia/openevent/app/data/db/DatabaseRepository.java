package org.fossasia.openevent.app.data.db;

import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.rx2.language.RXSQLite;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLOperator;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.database.transaction.FastStoreModelTransaction;

import org.fossasia.openevent.app.data.db.configuration.OrgaDatabase;
import org.fossasia.openevent.app.data.db.contract.IDatabaseRepository;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import timber.log.Timber;

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
        return Completable.fromAction(item::save)
            .doOnComplete(() -> Timber.i("Saved item %s in database", item.getClass()));
    }

    @Override
    public <T extends BaseModel> Completable saveList(Class<T> itemClass, List<T> items) {
        return Completable.fromAction(() -> {
            DatabaseDefinition database = FlowManager.getDatabase(OrgaDatabase.class);
            FastStoreModelTransaction<T> transaction = FastStoreModelTransaction
                .insertBuilder(FlowManager.getModelAdapter(itemClass))
                .addAll(items)
                .build();

            database.executeTransaction(transaction);
        }).doOnComplete(() -> Timber.i("Saved items of type %s in database", itemClass));
    }

    @Override
    public <T extends BaseModel> Completable delete(Class<T> typeClass, SQLOperator... conditions) {
        return Completable.fromAction(() -> SQLite.delete(typeClass).where(conditions).execute());
    }

    @Override
    public <T extends BaseModel> Completable deleteAll(Class<T> typeClass) {
        return Completable.fromAction(() -> Delete.table(typeClass));
    }

    @SafeVarargs
    @Override
    public final Completable deleteAll(Class<? extends BaseModel>... typeClass) {
        return Completable.fromAction(() -> Delete.tables((Class<?>[]) typeClass));
    }
}
