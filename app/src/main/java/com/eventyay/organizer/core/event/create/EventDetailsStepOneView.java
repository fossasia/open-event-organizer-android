package com.eventyay.organizer.core.event.create;


import java.util.List;

public interface EventDetailsStepOneView {

    List<String> getTimeZoneList();

    void setDefaultTimeZone(int index);

}
