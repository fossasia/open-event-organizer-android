package com.eventyay.organizer.data.user;

import com.eventyay.organizer.data.event.ImageData;
import com.eventyay.organizer.data.event.ImageUrl;
import io.reactivex.Observable;


public interface UserRepository {

    Observable<User> updateUser(User user);

    Observable<ImageUrl> uploadImage(ImageData imageData);

    Observable<User> getOrganizer(boolean reload);
}
