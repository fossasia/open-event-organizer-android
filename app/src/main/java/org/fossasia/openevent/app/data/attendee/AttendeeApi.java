package org.fossasia.openevent.app.data.attendee;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

public interface AttendeeApi {

    @GET("events/{id}/attendees?include=event&fields[event]=id&page[size]=0")
    Observable<List<Attendee>> getAttendees(@Path("id") long id);

    @PATCH("attendees/{attendee_id}?include=ticket,event,order&fields[event]=id&fields[ticket]=id")
    Observable<Attendee> patchAttendee(@Path("attendee_id") long attendeeId, @Body Attendee attendee);

}
