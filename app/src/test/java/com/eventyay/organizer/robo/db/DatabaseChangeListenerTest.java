package com.eventyay.organizer.robo.db;

import com.raizlabs.android.dbflow.structure.BaseModel;

import com.eventyay.organizer.data.db.DbFlowDatabaseChangeListener;
import com.eventyay.organizer.data.db.DbFlowDatabaseRepository;
import com.eventyay.organizer.common.model.SimpleModel;
import com.eventyay.organizer.common.model.SimpleModel_Table;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

public class DatabaseChangeListenerTest extends BaseTest {

    private DbFlowDatabaseRepository databaseRepository;
    private DbFlowDatabaseChangeListener<SimpleModel> databaseChangeListener;
    private Observable<DbFlowDatabaseChangeListener.ModelChange<SimpleModel>> notifier;

    @Override
    public void setUp() {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
        databaseRepository = new DbFlowDatabaseRepository();
        databaseChangeListener = new DbFlowDatabaseChangeListener<>(SimpleModel.class);
        databaseChangeListener.startListening();
        notifier = databaseChangeListener.getNotifier();
    }

    @Test
    public void testInsert() throws InterruptedException {
        List<DbFlowDatabaseChangeListener.ModelChange<SimpleModel>> changes = new ArrayList<>();
        changes.add(new DbFlowDatabaseChangeListener.ModelChange<>(DatabaseRepositoryTest.MODEL, BaseModel.Action.INSERT));
        changes.add(new DbFlowDatabaseChangeListener.ModelChange<>(DatabaseRepositoryTest.MODEL, BaseModel.Action.SAVE));

        databaseRepository.save(SimpleModel.class, DatabaseRepositoryTest.MODEL).subscribe();
        databaseChangeListener.stopListening();

        notifier
            .test()
            .assertValueSequence(changes);
    }

    @Test
    public void testUpdate() throws InterruptedException {
        databaseRepository.save(SimpleModel.class, DatabaseRepositoryTest.MODEL).subscribe();

        SimpleModel newModel = SimpleModel.fromModel(DatabaseRepositoryTest.MODEL);
        newModel.setDescription("Billy Bowden");

        databaseRepository.update(SimpleModel.class, newModel).subscribe();
        databaseChangeListener.stopListening();

        notifier
            .lastOrError()
            .test()
            .assertValue(value ->
                value.getAction().equals(BaseModel.Action.UPDATE) &&
                    value.getModel().equals(newModel)
            );
    }

    @Test
    public void testDelete() throws InterruptedException {
        databaseRepository.save(SimpleModel.class, DatabaseRepositoryTest.MODEL).subscribe();
        databaseRepository.delete(SimpleModel.class, SimpleModel_Table.id.eq(1L)).subscribe();
        databaseChangeListener.stopListening();

        notifier
            .lastOrError()
            .test()
            .assertValue(value ->
                value.getAction().equals(BaseModel.Action.DELETE)
            );
    }

    @Test
    public void testDisposed() {
        List<DbFlowDatabaseChangeListener.ModelChange<SimpleModel>> changes = new ArrayList<>();
        changes.add(new DbFlowDatabaseChangeListener.ModelChange<>(DatabaseRepositoryTest.MODEL, BaseModel.Action.INSERT));
        changes.add(new DbFlowDatabaseChangeListener.ModelChange<>(DatabaseRepositoryTest.MODEL, BaseModel.Action.SAVE));

        databaseChangeListener.stopListening();
        notifier.subscribe().dispose();
        databaseChangeListener.startListening();
        databaseRepository.save(SimpleModel.class, DatabaseRepositoryTest.MODEL).subscribe();
        databaseChangeListener.stopListening();

        databaseChangeListener.getNotifier()
            .test()
            .assertValueSequence(changes);
    }

}
