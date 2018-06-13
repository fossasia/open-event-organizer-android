package org.fossasia.openevent.app.data.db;

import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.rx2.language.RXSQLite;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLOperator;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.ModelAdapter;
import com.raizlabs.android.dbflow.structure.database.transaction.FastStoreModelTransaction;

import org.fossasia.openevent.app.data.db.configuration.OrgaDatabase;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import timber.log.Timber;

public class DbFlowDatabaseRepository implements DatabaseRepository {

    @Inject
    public DbFlowDatabaseRepository() { }

    @Override
    public <T> Observable<T> getItems(Class<T> typeClass, SQLOperator... conditions) {
        return RXSQLite.rx(SQLite.select()
            .from(typeClass)
            .where(conditions))
            .queryList()
            .flattenAsObservable(items -> items);
    }

    @Override
    public <T> Observable<T> getAllItems(Class<T> typeClass) {
        return RXSQLite.rx(SQLite.select()
            .from(typeClass))
            .queryList()
            .flattenAsObservable(items -> items);
    }

    @Override
    public <T, V> Observable<T> getJoinedItems(Class<T> typeClass, Class<V> joinedClass,
                                               SQLOperator typeClassConditions,
                                               SQLOperator joinedClassConditions) {
        return RXSQLite.rx(SQLite.select()
            .from(typeClass)
            .leftOuterJoin(joinedClass)
            .on(typeClassConditions)
            .where(joinedClassConditions))
            .queryList()
            .flattenAsObservable(items -> items);
    }

    @Override
    public <T> Completable save(Class<T> classType, T item) {
        return Completable.fromAction(() -> {
            ModelAdapter<T> modelAdapter = FlowManager.getModelAdapter(classType);
            modelAdapter.save(item);
        })
            .doOnComplete(() -> Timber.i("Saved item %s in database", item.getClass()));
    }

    @Override
    public <T> Completable saveList(Class<T> itemClass, List<T> items) {
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
    public <T> Completable update(Class<T> classType, T item) {
        return Completable.fromAction(() -> {
            ModelAdapter<T> modelAdapter = FlowManager.getModelAdapter(classType);
            modelAdapter.update(item);
        })
            .doOnComplete(() -> Timber.i("Updated item of Type %s : ", item.getClass()));
    }

    @Override
    public <T> Completable delete(Class<T> typeClass, SQLOperator... conditions) {
        return Completable.fromAction(() -> SQLite.delete(typeClass).where(conditions).execute());
    }

    @Override
    public <T> Completable deleteAll(Class<T> typeClass) {
        return Completable.fromAction(() -> Delete.table(typeClass));
    }

    @Override
    public Completable deleteAll(Class<?>... typeClass) {
        return Completable.fromAction(() -> Delete.tables((Class<?>[]) typeClass));
    }
}
