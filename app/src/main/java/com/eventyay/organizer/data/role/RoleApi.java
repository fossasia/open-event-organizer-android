package com.eventyay.organizer.data.role;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RoleApi {

    @POST("role-invites")
    Observable<RoleInvite> postRoleInvite(@Body RoleInvite roleInvite);

    @GET("events/{id}/role-invites")
    Observable<List<RoleInvite>> getRoles(@Path("id") long id);
}
