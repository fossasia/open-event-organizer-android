package org.fossasia.openevent.app.event.detail.contract;

public interface IEventDetailView {

    void showProgressBar(boolean show);

    void showEventName(String name);

    void showDates(String start, String end);

    void showTime(String time);

    void showQuantityInfo(long quantity, long total);

    void showAttendeeInfo(long checkedIn, long total);

    void showEventLoadError(String error);

}
