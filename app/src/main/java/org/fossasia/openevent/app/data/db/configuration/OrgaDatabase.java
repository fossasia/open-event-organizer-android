package org.fossasia.openevent.app.data.db.configuration;

import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.Database;

@Database(
    name = OrgaDatabase.NAME,
    version = OrgaDatabase.VERSION,
    insertConflict = ConflictAction.REPLACE,
    updateConflict= ConflictAction.REPLACE,
    foreignKeyConstraintsEnforced = true
)
public class OrgaDatabase {
    public static final String NAME = "orga_database";
    // To be dumped after each schema change and migration addition
    public static final int VERSION = 1;
}
