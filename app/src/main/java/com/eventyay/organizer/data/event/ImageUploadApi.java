package com.eventyay.organizer.data.event;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ImageUploadApi {

    @POST("upload/image")
    Observable<ImageUrl> postOriginalImage(@Body ImageData data);
}
