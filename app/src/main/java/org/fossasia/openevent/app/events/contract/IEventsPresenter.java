package org.fossasia.openevent.app.events.contract;

import org.fossasia.openevent.app.common.contract.presenter.IBasePresenter;
import org.fossasia.openevent.app.data.models.Event;

import java.util.List;

public interface IEventsPresenter extends IBasePresenter<IEventsView> {

    List<Event> getEvents();

    void loadUserEvents(boolean forceReload);

    void loadOrganiser(boolean forceReload);

}
