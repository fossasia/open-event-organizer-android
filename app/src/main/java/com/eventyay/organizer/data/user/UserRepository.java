package com.eventyay.organizer.data.user;

import com.eventyay.organizer.data.image.ImageData;
import com.eventyay.organizer.data.image.ImageUrl;
import io.reactivex.Observable;

public interface UserRepository {

    Observable<User> updateUser(User user);

    Observable<User> getOrganizer(boolean reload);

    Observable<ImageUrl> uploadOrganizerImage(ImageData imageData);
}
