package com.eventyay.organizer.data.event;

import android.support.annotation.NonNull;

import io.reactivex.Observable;

public interface EventRepository {

    Observable<Event> getEvent(long eventId, boolean reload);

    @NonNull
    Observable<Event> getEvents(boolean reload);

    @NonNull
    Observable<Event> updateEvent(Event event);

    Observable<Event> createEvent(Event event);

    Observable<EventStatistics> getEventStatistics(long id);

    Observable<ImageUrl> uploadEventImage(ImageData imageData);
}
