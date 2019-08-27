package com.eventyay.organizer.data.notification;

import io.reactivex.Observable;
import java.util.List;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface NotificationApi {

    @GET("/v1/users/{user_id}/notifications")
    Observable<List<Notification>> getNotifications(@Path("user_id") int user_id);
}
