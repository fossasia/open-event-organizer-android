package org.fossasia.openevent.app.common.di.module;

import dagger.Module;

@Module(includes = {
    AndroidModule.class,
    RepoModule.class,
    ModelModule.class,
    NetworkModule.class
})
public class AppModule {

    // Add misc dependencies

}
