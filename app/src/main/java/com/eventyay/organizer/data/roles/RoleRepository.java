package com.eventyay.organizer.data.roles;

import io.reactivex.Observable;

public interface RoleRepository {

    Observable<RoleInvite> sendRoleInvite(RoleInvite roleInvite);
}
