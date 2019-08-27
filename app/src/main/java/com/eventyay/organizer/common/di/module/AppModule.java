package com.eventyay.organizer.common.di.module;

import dagger.Module;

@Module(
        includes = {
            AndroidModule.class,
            RepoModule.class,
            ModelModule.class,
            NetworkModule.class,
            ViewModelModule.class
        })
public class AppModule {

    // Add misc dependencies

}
