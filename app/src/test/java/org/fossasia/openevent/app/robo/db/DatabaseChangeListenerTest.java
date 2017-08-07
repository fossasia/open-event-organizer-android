package org.fossasia.openevent.app.robo.db;

import android.app.Application;

import com.raizlabs.android.dbflow.structure.BaseModel;

import org.fossasia.openevent.app.BuildConfig;
import org.fossasia.openevent.app.common.data.db.DatabaseChangeListener;
import org.fossasia.openevent.app.common.data.db.DatabaseRepository;
import org.fossasia.openevent.app.common.data.models.dto.SimpleModel;
import org.fossasia.openevent.app.common.data.models.dto.SimpleModel_Table;
import org.fossasia.openevent.app.robo.rule.DatabaseTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, application = Application.class)
public class DatabaseChangeListenerTest {

    @Rule
    public final DatabaseTestRule dbRule = DatabaseTestRule.create();

    private DatabaseRepository databaseRepository;
    private DatabaseChangeListener<SimpleModel> databaseChangeListener;
    private Observable<DatabaseChangeListener.ModelChange<SimpleModel>> notifier;

    @Before
    public void setUp() {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
        databaseRepository = new DatabaseRepository();
        databaseChangeListener = new DatabaseChangeListener<>(SimpleModel.class);
        databaseChangeListener.startListening();
        notifier = databaseChangeListener.getNotifier();
    }

    @Test
    public void testInsert() throws InterruptedException {
        List<DatabaseChangeListener.ModelChange<SimpleModel>> changes = new ArrayList<>();
        changes.add(new DatabaseChangeListener.ModelChange<>(DatabaseRepositoryTest.MODEL, BaseModel.Action.INSERT));
        changes.add(new DatabaseChangeListener.ModelChange<>(DatabaseRepositoryTest.MODEL, BaseModel.Action.SAVE));

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

}
