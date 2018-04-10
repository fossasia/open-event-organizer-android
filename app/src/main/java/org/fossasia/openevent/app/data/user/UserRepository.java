package org.fossasia.openevent.app.data.user;

import io.reactivex.Observable;


public interface UserRepository {

    Observable<User> updateUser(User user);

    Observable<User> getOrganizer(boolean reload);
}
