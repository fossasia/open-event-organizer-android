package com.eventyay.organizer.data.roles;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RoleApi {

    @POST("role-invites")
    Observable<RoleInvite> postRoleInvite(@Body RoleInvite roleInvite);
}
