package com.eventyay.organizer.data.attendee;

import androidx.annotation.NonNull;

import io.reactivex.Completable;
import io.reactivex.Observable;

public interface AttendeeRepository {

    @NonNull
    Observable<Attendee> getAttendee(long attendeeId, boolean reload);

    @NonNull
    Observable<Attendee> getAttendees(long eventId, boolean reload);

    @NonNull
    Observable<Attendee> getAttendeesPagewise(long eventId, long pageNumber, boolean reload);

    @NonNull
    Observable<Attendee> getAttendeesUnderOrder(String orderIdentifier, long orderId, boolean reload);

    Completable scheduleToggle(Attendee attendee);

    @NonNull
    Observable<Attendee> toggleAttendeeCheckStatus(Attendee attendee);

    @NonNull
    Observable<Attendee> getPendingCheckIns();

    @NonNull
    Observable<Long> getCheckedInAttendees(long eventId);

}
