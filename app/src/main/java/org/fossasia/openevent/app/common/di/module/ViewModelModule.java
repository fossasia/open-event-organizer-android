package org.fossasia.openevent.app.common.di.module;

import androidx.lifecycle.ViewModelProvider;

import org.fossasia.openevent.app.common.di.OrgaViewModelFactory;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class ViewModelModule {

    /**
     * Example entry:
     * TODO: Remove when a real entry is added
     *
     * @Binds
     * @IntoMap
     * @ViewModelKey(MainViewModel.class)
     * public abstract ViewModel bindMainViewModel(MainViewModel mainViewModel);
     */

    @Binds
    public abstract ViewModelProvider.Factory bindViewModelFactory(OrgaViewModelFactory factory);

}
