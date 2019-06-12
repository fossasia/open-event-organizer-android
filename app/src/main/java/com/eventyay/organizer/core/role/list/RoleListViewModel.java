package com.eventyay.organizer.core.role.list;

import androidx.lifecycle.LiveData;
import com.eventyay.organizer.common.livedata.SingleEventLiveData;
import androidx.lifecycle.ViewModel;

import com.eventyay.organizer.common.ContextManager;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.db.DatabaseChangeListener;
import com.eventyay.organizer.data.db.DbFlowDatabaseChangeListener;
import com.eventyay.organizer.data.role.RoleInvite;
import com.eventyay.organizer.data.role.RoleRepository;
import com.eventyay.organizer.utils.ErrorUtils;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class RoleListViewModel extends ViewModel {

    private final List<RoleInvite> roles = new ArrayList<>();
    private final RoleRepository roleRepository;
    private final DatabaseChangeListener<RoleInvite> roleListChangeListener;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final SingleEventLiveData<Boolean> progress = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> error = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> success = new SingleEventLiveData<>();
    private final SingleEventLiveData<List<RoleInvite>> rolesLiveData = new SingleEventLiveData<>();

    private long eventId;

    @Inject
    public RoleListViewModel(RoleRepository roleRepository, DatabaseChangeListener<RoleInvite> roleListChangeListener) {
        this.roleRepository = roleRepository;
        this.roleListChangeListener = roleListChangeListener;
    }

    public LiveData<Boolean> getProgress() {
        return progress;
    }

    public LiveData<String> getSuccess() {
        return success;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<List<RoleInvite>> getRolesLiveData() {
        return rolesLiveData;
    }

    public DatabaseChangeListener<RoleInvite> getRoleListChangeListener() {
        return roleListChangeListener;
    }

    public void loadRoles(boolean forceReload) {

        eventId = ContextManager.getSelectedEvent().getId();

        compositeDisposable.add(
            getRoleSource(forceReload)
                .doOnSubscribe(disposable -> progress.setValue(true))
                .doFinally(() -> progress.setValue(false))
                .toList()
                .subscribe(roleList -> {
                    roles.clear();
                    roles.addAll(roleList);
                    success.setValue("Roles Loaded Successfully");
                    rolesLiveData.setValue(roles);
                }, throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
    }

    private Observable<RoleInvite> getRoleSource(boolean forceReload) {
        if (!forceReload && !roles.isEmpty()) {
            return Observable.fromIterable(roles);
        } else {
            return roleRepository.getRoles(eventId, forceReload);
        }
    }

    public void listenChanges() {
        roleListChangeListener.startListening();
        roleListChangeListener.getNotifier()
            .map(DbFlowDatabaseChangeListener.ModelChange::getAction)
            .filter(action -> action.equals(BaseModel.Action.INSERT))
            .subscribeOn(Schedulers.io())
            .subscribe(roleModelChange -> loadRoles(false), Logger::logError);
    }

    public List<RoleInvite> getRoles() {
        return roles;
    }
}
