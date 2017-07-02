package org.fossasia.openevent.app.event.detail.contract;

public interface IEventDetailPresenter {

    void attach(IEventDetailView eventDetailView, long eventId);

    void start();

    void detach();

    void loadDetails(boolean forceReload);

}
