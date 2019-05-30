package com.eventyay.organizer.core.roleinvites;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.eventyay.organizer.common.ContextManager;
import com.eventyay.organizer.common.livedata.SingleEventLiveData;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.roles.Role;
import com.eventyay.organizer.data.roles.RoleInvite;
import com.eventyay.organizer.data.roles.RoleRepository;
import com.eventyay.organizer.utils.ErrorUtils;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class RoleInviteViewModel extends ViewModel {

    private final RoleRepository roleRepository;
    private RoleInvite roleInvite = new RoleInvite();
    private Role role = new Role();

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final SingleEventLiveData<Boolean> progress = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> error = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> success = new SingleEventLiveData<>();
    private final SingleEventLiveData<Void> dismiss = new SingleEventLiveData<>();

    @Inject
    public RoleInviteViewModel(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public RoleInvite getRoleInvite() {
        return roleInvite;
    }

    public LiveData<Boolean> getProgress() {
        return progress;
    }

    public LiveData<String> getSuccess() {
        return success;
    }

    public LiveData<Void> getDismiss() {
        return dismiss;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void createRoleInvite(long roleId) {

        long eventId = ContextManager.getSelectedEvent().getId();
        Event event = new Event();
        event.setId(eventId);
        roleInvite.setEvent(event);
        role.setId(roleId);
        roleInvite.setRole(role);

        compositeDisposable.add(
            roleRepository
                .sendRoleInvite(roleInvite)
                .doOnSubscribe(disposable -> progress.setValue(true))
                .doFinally(() -> progress.setValue(false))
                .subscribe(sentRoleInvite -> {
                    success.setValue("Role Invite Sent");
                    dismiss.call();
                }, throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
    }
}
