package org.fossasia.openevent.app.data.repository.contract;

import android.support.annotation.NonNull;

import org.fossasia.openevent.app.data.models.Attendee;

import io.reactivex.Observable;

public interface IAttendeeRepository {

    @NonNull
    Observable<Attendee> getAttendee(long attendeeId, boolean reload);

    @NonNull
    Observable<Attendee> getAttendees(long eventId, boolean reload);

    @NonNull
    Observable<Attendee> toggleAttendeeCheckStatus(long eventId, long attendeeId);

    @NonNull
    Observable<Long> getCheckedInAttendees(long eventId);

}
