package org.fossasia.openevent.app.data;

import com.raizlabs.android.dbflow.sql.language.property.Property;

import org.fossasia.openevent.app.common.Function;
import org.fossasia.openevent.app.data.db.DatabaseRepository;
import org.fossasia.openevent.app.data.network.ConnectionStatus;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import lombok.experimental.Delegate;

/**
 * General Repository class. To be generified in future
 */
@SuppressWarnings("MissingOverride")
public final class Repository implements DatabaseRepository, ConnectionStatus {

    private final ContextUtils utilModel;
    private final ConnectionStatus connectionStatus;
    private final AbstractObservable abstractObservable;
    @Delegate
    private final DatabaseRepository databaseRepository;

    @Inject
    public Repository(ContextUtils utilModel, ConnectionStatus connectionStatus,
                      AbstractObservable abstractObservable, DatabaseRepository databaseRepository) {
        this.utilModel = utilModel;
        this.connectionStatus = connectionStatus;
        this.abstractObservable = abstractObservable;
        this.databaseRepository = databaseRepository;
    }

    public <T, R> Completable syncSave(Class<T> clazz, List<T> items, Function<T, R> idMapper, Property<R> id) {
        return Observable.fromIterable(items)
            .map(idMapper::apply)
            .toList()
            .flatMapCompletable(ids -> databaseRepository.delete(clazz, id.notIn(ids)))
            .concatWith(databaseRepository.saveList(clazz, items));
    }

    public <T> AbstractObservable.AbstractObservableBuilder<T> observableOf(Class<T> clazz) {
        return abstractObservable.of(clazz);
    }

    @Override
    public boolean isConnected() {
        return connectionStatus.isConnected();
    }

    public Completable deleteDatabase() {
        return utilModel.deleteDatabase();
    }
}
