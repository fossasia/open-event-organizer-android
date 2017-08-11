package org.fossasia.openevent.app.common.data.repository.contract;

import android.support.annotation.NonNull;

import org.fossasia.openevent.app.common.data.models.Attendee;

import io.reactivex.Completable;
import io.reactivex.Observable;

public interface IAttendeeRepository {

    @NonNull
    Observable<Attendee> getAttendee(long attendeeId, boolean reload);

    @NonNull
    Observable<Attendee> getAttendees(long eventId, boolean reload);

    Completable scheduleToggle(Attendee attendee);

    @NonNull
    Observable<Attendee> toggleAttendeeCheckStatus(Attendee attendee);

    @NonNull
    Observable<Attendee> getPendingCheckIns();

    @NonNull
    Observable<Long> getCheckedInAttendees(long eventId);

}
