package org.fossasia.openevent.app.data.user;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

public interface UserApi {

    @PATCH("users/{id}")
    Observable<User> patchUser(@Path("id") long id, @Body User user);

    @GET("users/{id}")
    Observable<User> getOrganizer(@Path("id") long id);
}
