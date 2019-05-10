package com.eventyay.organizer.data.attendee;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AttendeeApi {

    @GET("events/{id}/attendees?include=order,ticket,event&fields[event]=id&fields[ticket]=id&page[size]=0")
    Observable<List<Attendee>> getAttendees(@Path("id") long id);

    @GET("events/{id}/attendees?include=order,ticket,event&fields[event]=id&fields[ticket]=id&page[size]=50")
    Observable<List<Attendee>> getAttendeesPagewise(@Path("id") long id, @Query("page[number]") long pagenumber);

    @GET("orders/{id}/attendees?include=order,ticket,event&fields[event]=id&fields[ticket]=id&page[size]=0")
    Observable<List<Attendee>> getAttendeesUnderOrder(@Path("id") String id);

    @PATCH("attendees/{attendee_id}?include=ticket,event,order&fields[event]=id&fields[ticket]=id")
    Observable<Attendee> patchAttendee(@Path("attendee_id") long attendeeId, @Body Attendee attendee);

}
