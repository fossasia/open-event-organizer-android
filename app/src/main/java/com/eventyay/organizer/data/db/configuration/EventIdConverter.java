package com.eventyay.organizer.data.db.configuration;

import androidx.room.TypeConverter;
import com.eventyay.organizer.data.event.Event;

public class EventIdConverter {

    @TypeConverter
    public Long fromEvent(Event event) {
        return event.id;
    }

    @TypeConverter
    public Event toEvent(Long id) {
        return new Event();
    }
}
