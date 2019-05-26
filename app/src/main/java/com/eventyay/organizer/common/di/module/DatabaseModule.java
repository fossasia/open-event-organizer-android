package com.eventyay.organizer.common.di.module;

import android.app.Application;

import androidx.room.Room;

import com.eventyay.organizer.data.db.configuration.OrgaRoomDatabase;
import com.eventyay.organizer.data.faq.FaqDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DatabaseModule {

    private OrgaRoomDatabase orgaRoomDatabase;

    public DatabaseModule(Application application) {
        orgaRoomDatabase = Room.databaseBuilder(application, OrgaRoomDatabase.class, "OrgaRoomDatabase.db").build();
    }

    @Singleton
    @Provides
    OrgaRoomDatabase providesOrgaRoomDatabase() {
        return orgaRoomDatabase;
    }

    @Singleton
    @Provides
    FaqDao providesFaqDao(OrgaRoomDatabase orgaRoomDatabase) {
        return orgaRoomDatabase.faqDao();
    }
}
