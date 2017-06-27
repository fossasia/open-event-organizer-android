package org.fossasia.openevent.app.data.db.configuration;

import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.fossasia.openevent.app.data.models.License;

@Database(
    name = OrgaDatabase.NAME,
    version = OrgaDatabase.VERSION,
    insertConflict = ConflictAction.REPLACE,
    updateConflict= ConflictAction.REPLACE,
    foreignKeyConstraintsEnforced = true
)
public class OrgaDatabase {
    public static final String NAME = "orga_database";
    // To be bumped after each schema change and migration addition
    public static final int VERSION = 2;

    // Migration version is less than or equal to Database version
    // Older version migrations are not to be removed
    @Migration(version = 2, database = OrgaDatabase.class)
    public static class MigrationTo2 extends AlterTableMigration<License> {

        public MigrationTo2(Class<License> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            // There is no way to rename columns in SQLite, so we'll add new columns instead
            addColumn(SQLiteType.TEXT, "logo");
            addColumn(SQLiteType.TEXT, "compactLogo");
            addColumn(SQLiteType.TEXT, "url");
        }
    }
}
