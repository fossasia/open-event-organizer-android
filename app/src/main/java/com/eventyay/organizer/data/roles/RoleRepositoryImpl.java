package com.eventyay.organizer.data.roles;

import com.eventyay.organizer.common.Constants;
import com.eventyay.organizer.data.Repository;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class RoleRepositoryImpl implements RoleRepository {

    private final RoleApi roleApi;
    private final Repository repository;

    @Inject
    public RoleRepositoryImpl(RoleApi roleApi, Repository repository) {
        this.roleApi = roleApi;
        this.repository = repository;
    }

    @Override
    public Observable<RoleInvite> sendRoleInvite(RoleInvite roleInvite) {
        if (!repository.isConnected()) {
            return Observable.error(new Throwable(Constants.NO_NETWORK));
        }

        return roleApi
            .postRoleInvite(roleInvite)
            .doOnNext(inviteSent -> Timber.d(String.valueOf(inviteSent)))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }
}
