package com.eventyay.organizer.data.db.configuration;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.eventyay.organizer.data.faq.Faq;
import com.eventyay.organizer.data.faq.FaqDao;

@Database(entities = Faq.class, version = OrgaDatabase.VERSION)
@TypeConverters(EventIdConverter.class)
public abstract class OrgaRoomDatabase extends RoomDatabase {

    private static final String DB_NAME = "OrgaRoomDatabase.db";
    private static volatile OrgaRoomDatabase instance;

    // To be bumped after each schema change and migration addition
    public static final int VERSION = 1;


    public OrgaRoomDatabase() {
        // Never Called
    }

    public static synchronized OrgaRoomDatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static OrgaRoomDatabase create(final Context context) {
        return Room.databaseBuilder(context, OrgaRoomDatabase.class, DB_NAME)
            .build();
    }

    public abstract FaqDao faqDao();
}
