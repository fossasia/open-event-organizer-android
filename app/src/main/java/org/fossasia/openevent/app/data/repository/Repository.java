package org.fossasia.openevent.app.data.repository;

import com.raizlabs.android.dbflow.sql.language.property.Property;

import org.fossasia.openevent.app.common.Function;
import org.fossasia.openevent.app.data.IUtilModel;
import org.fossasia.openevent.app.data.db.IDatabaseRepository;
import org.fossasia.openevent.app.data.network.EventService;
import org.fossasia.openevent.app.utils.Utils;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

/**
 * General Repository class. To be generified in future
 */
class Repository {

    protected IDatabaseRepository databaseRepository;
    protected EventService eventService;

    protected IUtilModel utilModel;

    Repository(IUtilModel utilModel, IDatabaseRepository databaseRepository, EventService eventService) {
        this.utilModel = utilModel;
        this.databaseRepository = databaseRepository;
        this.eventService = eventService;
    }

    final <T, R> Completable syncSave(Class<T> clazz, List<T> items, Function<T, R> idMapper, Property<R> id) {
        return Observable.fromIterable(items)
            .map(idMapper::apply)
            .toList()
            .flatMapCompletable(ids -> databaseRepository.delete(clazz, id.notIn(ids)))
            .concatWith(databaseRepository.saveList(clazz, items));
    }

    String getAuthorization() {
        return Utils.formatToken(utilModel.getToken());
    }

}
