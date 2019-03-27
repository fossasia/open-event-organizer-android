package com.eventyay.organizer.data.db.configuration;

import android.arch.persistence.room.Database;
import android.support.annotation.NonNull;

import com.eventyay.organizer.data.attendee.Attendee;
import com.eventyay.organizer.data.copyright.Copyright;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.faq.Faq;
import com.eventyay.organizer.data.feedback.Feedback;
import com.eventyay.organizer.data.order.Order;
import com.eventyay.organizer.data.order.OrderStatistics;
import com.eventyay.organizer.data.order.Statistics;
import com.eventyay.organizer.data.session.Session;
import com.eventyay.organizer.data.speaker.Speaker;
import com.eventyay.organizer.data.speakerscall.SpeakersCall;
import com.eventyay.organizer.data.sponsor.Sponsor;
import com.eventyay.organizer.data.ticket.Ticket;
import com.eventyay.organizer.data.tracks.Track;
import com.eventyay.organizer.data.user.User;

import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.ModelAdapter;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

@Database( version = OrgaDatabase.VERSION )
public final class OrgaDatabase {

    public static final String NAME = "orga_database";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS ";
    // To be bumped after each schema change and migration addition
    public static final int VERSION = 18;


    private OrgaDatabase() {
        // Never Called
    }

    // Migration version is less than or equal to Database version
    // Older version migrations are not to be removed
    @Migration(version = 4, database = OrgaDatabase.class)
    public static class MigrationTo4 extends BaseMigration {

        @Override
        public void migrate(@NonNull DatabaseWrapper databaseWrapper) {
            Timber.d("Running migration for DB version 4");

            Class<?>[] recreated = new Class[] {Attendee.class, Event.class, Order.class, Ticket.class, User.class};
            String[] deleted = new String[] {"CallForPapers", "Copyright", "License", "SocialLink", "UserDetail", "Version"};

            for (Class<?> recreate: recreated) {
                ModelAdapter modelAdapter = FlowManager.getModelAdapter(recreate);
                databaseWrapper.execSQL(DROP_TABLE + modelAdapter.getTableName());
                databaseWrapper.execSQL(modelAdapter.getCreationQuery());
            }

            for (String delete: deleted)
                databaseWrapper.execSQL(DROP_TABLE + delete);
        }
    }

    @Migration(version = 5, database = OrgaDatabase.class)
    public static class MigrationTo5 extends AlterTableMigration<Attendee> {

        public MigrationTo5(Class<Attendee> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            super.onPreMigrate();
            addColumn(SQLiteType.INTEGER, "checking");
        }
    }

    @Migration(version = 6, database = OrgaDatabase.class)
    public static class MigrationTo6 extends AlterTableMigration<Order> {

        public MigrationTo6(Class<Order> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            super.onPreMigrate();
            List<String> addedColumns = Arrays.asList("address", "zipcode", "city", "state", "country",
                "expMonth", "expYear", "transactionId", "discountCodeId", "brand", "last4");

            for (String column : addedColumns)
                addColumn(SQLiteType.TEXT, column);
        }
    }

    @Migration(version = 7, database = OrgaDatabase.class)
    public static class MigrationTo7 extends AlterTableMigration<Attendee> {

        public MigrationTo7(Class<Attendee> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            super.onPreMigrate();
            List<String> addedColumns = Arrays.asList("blog", "homeAddress", "workAddress", "jobTitle",
                "country", "taxBusinessInfo", "phone", "gender", "company", "workPhone", "birthDate",
                "twitter", "facebook", "github", "website", "shippingAddress", "billingAddress");

            for (String column : addedColumns)
                addColumn(SQLiteType.TEXT, column);
        }
    }

    @Migration(version = 8, database = OrgaDatabase.class)
    public static class MigrationTo8 extends AlterTableMigration<Attendee> {

        public MigrationTo8(Class<Attendee> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            super.onPreMigrate();

            addColumn(SQLiteType.TEXT, "checkinTimes");
        }
    }

    @Migration(version = 9, database = OrgaDatabase.class)
    public static class MigrationTo9 extends BaseMigration {

        @Override
        public void migrate(@NonNull DatabaseWrapper databaseWrapper) {
            Timber.d("Running migration for DB version 9");

            Class<?>[] recreated = new Class[] {Faq.class, Copyright.class};

            for (Class<?> recreate: recreated) {
                ModelAdapter modelAdapter = FlowManager.getModelAdapter(recreate);
                databaseWrapper.execSQL("DROP TABLE IF EXISTS " + modelAdapter.getTableName());
                databaseWrapper.execSQL(modelAdapter.getCreationQuery());
            }
        }
    }

    @Migration(version = 10, database = OrgaDatabase.class)
    public static class MigrationTo10 extends BaseMigration {

        @Override
        public void migrate(@NonNull DatabaseWrapper databaseWrapper) {
            Timber.d("Running migration for DB version 10");

            Class<?>[] recreated = new Class[] {Feedback.class};

            for (Class<?> recreate: recreated) {
                ModelAdapter modelAdapter = FlowManager.getModelAdapter(recreate);
                databaseWrapper.execSQL(DROP_TABLE + modelAdapter.getTableName());
                databaseWrapper.execSQL(modelAdapter.getCreationQuery());
            }
        }
    }

    @Migration(version = 11, database = OrgaDatabase.class)
    public static class MigrationTo11 extends BaseMigration {

        @Override
        public void migrate(@NonNull DatabaseWrapper databaseWrapper) {
            Timber.d("Running migration for DB version 11");

            Class<?>[] recreated = new Class[] {Track.class};

            for (Class<?> recreate: recreated) {
                ModelAdapter modelAdapter = FlowManager.getModelAdapter(recreate);
                databaseWrapper.execSQL(DROP_TABLE + modelAdapter.getTableName());
                databaseWrapper.execSQL(modelAdapter.getCreationQuery());
            }
        }
    }

    @Migration(version = 12, database = OrgaDatabase.class)
    public static class MigrationTo12 extends BaseMigration {

        @Override
        public void migrate(@NonNull DatabaseWrapper databaseWrapper) {
            Timber.d("Running migration for DB version 12");

            Class<?>[] recreated = new Class[] {Session.class};

            for (Class<?> recreate: recreated) {
                ModelAdapter modelAdapter = FlowManager.getModelAdapter(recreate);
                databaseWrapper.execSQL(DROP_TABLE + modelAdapter.getTableName());
                databaseWrapper.execSQL(modelAdapter.getCreationQuery());
            }
        }
    }

    @Migration(version = 13, database = OrgaDatabase.class)
    public static class MigrationTo13 extends BaseMigration {

        @Override
        public void migrate(@NonNull DatabaseWrapper databaseWrapper) {
            Timber.d("Running migration for DB version 13");

            Class<?>[] recreated = new Class[] {Sponsor.class};

            for (Class<?> recreate: recreated) {
                ModelAdapter modelAdapter = FlowManager.getModelAdapter(recreate);
                databaseWrapper.execSQL(DROP_TABLE + modelAdapter.getTableName());
                databaseWrapper.execSQL(modelAdapter.getCreationQuery());
            }
        }
    }

    @Migration(version = 14, database = OrgaDatabase.class)
    public static class MigrationTo14 extends BaseMigration {

        @Override
        public void migrate(@NonNull DatabaseWrapper databaseWrapper) {
            Timber.d("Running migration for DB version 14");

            Class<?>[] recreated = new Class[] {Speaker.class};

            for (Class<?> recreate: recreated) {
                ModelAdapter modelAdapter = FlowManager.getModelAdapter(recreate);
                databaseWrapper.execSQL(DROP_TABLE + modelAdapter.getTableName());
                databaseWrapper.execSQL(modelAdapter.getCreationQuery());
            }
        }
    }

    @Migration(version = 15, database = OrgaDatabase.class)
    public static class MigrationTo15 extends BaseMigration {

        @Override
        public void migrate(@NonNull DatabaseWrapper databaseWrapper) {
            Timber.d("Running migration for DB version 15");

            Class<?>[] recreated = new Class[] {SpeakersCall.class};

            for (Class<?> recreate: recreated) {
                ModelAdapter modelAdapter = FlowManager.getModelAdapter(recreate);
                databaseWrapper.execSQL(DROP_TABLE + modelAdapter.getTableName());
                databaseWrapper.execSQL(modelAdapter.getCreationQuery());
            }
        }
    }

    @Migration(version = 16, database = OrgaDatabase.class)
    public static class MigrationTo16 extends BaseMigration {

        @Override
        public void migrate(@NonNull DatabaseWrapper databaseWrapper) {
            Timber.d("Running migration for DB version 16");

            Class<?>[] recreated = new Class[] {OrderStatistics.class, Statistics.class};

            for (Class<?> recreate: recreated) {
                ModelAdapter modelAdapter = FlowManager.getModelAdapter(recreate);
                databaseWrapper.execSQL(DROP_TABLE + modelAdapter.getTableName());
                databaseWrapper.execSQL(modelAdapter.getCreationQuery());
            }
        }
    }

    @Migration(version = 17, database = OrgaDatabase.class)
    public static class MigrationTo17 extends AlterTableMigration<Attendee> {

        public MigrationTo17(Class<Attendee> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            super.onPreMigrate();

            String column = "checkoutTimes";
            addColumn(SQLiteType.TEXT, column);
        }
    }

    @Migration(version = 18, database = OrgaDatabase.class)
    public static class MigrationTo18 extends AlterTableMigration<Event> {

        public MigrationTo18(Class<Event> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            super.onPreMigrate();

            List<String> addedColumns = Arrays.asList("ticketsSold", "ticketsAvailable", "revenue");

            for (String column : addedColumns)
                addColumn(SQLiteType.TEXT, column);
        }
    }

}
