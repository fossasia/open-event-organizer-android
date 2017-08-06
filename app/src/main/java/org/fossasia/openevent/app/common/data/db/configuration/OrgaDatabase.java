package org.fossasia.openevent.app.common.data.db.configuration;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.ModelAdapter;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import org.fossasia.openevent.app.common.data.models.Attendee;
import org.fossasia.openevent.app.common.data.models.Event;
import org.fossasia.openevent.app.common.data.models.Order;
import org.fossasia.openevent.app.common.data.models.Ticket;
import org.fossasia.openevent.app.common.data.models.User;

import timber.log.Timber;

@Database(
    name = OrgaDatabase.NAME,
    version = OrgaDatabase.VERSION,
    insertConflict = ConflictAction.REPLACE,
    updateConflict = ConflictAction.REPLACE,
    foreignKeyConstraintsEnforced = true
)
public final class OrgaDatabase {

    private OrgaDatabase() {
        // Never Called
    }

    public static final String NAME = "orga_database";
    // To be bumped after each schema change and migration addition
    public static final int VERSION = 4;

    // Migration version is less than or equal to Database version
    // Older version migrations are not to be removed
    @Migration(version = 4, database = OrgaDatabase.class)
    public static class MigrationTo4 extends BaseMigration {

        @Override
        public void migrate(@NonNull DatabaseWrapper databaseWrapper) {
            Timber.d("Running migration for DB version 4");

            Class[] recreated = new Class[] {Attendee.class, Event.class, Order.class, Ticket.class, User.class};
            String[] deleted = new String[] {"CallForPapers", "Copyright", "License", "SocialLink", "UserDetail", "Version"};

            for (Class recreate: recreated) {
                ModelAdapter modelAdapter = FlowManager.getModelAdapter(recreate);
                databaseWrapper.execSQL("DROP TABLE IF EXISTS " + modelAdapter.getTableName());
                databaseWrapper.execSQL(modelAdapter.getCreationQuery());
            }

            for (String delete: deleted)
                databaseWrapper.execSQL("DROP TABLE IF EXISTS " + delete);
        }
    }
}
