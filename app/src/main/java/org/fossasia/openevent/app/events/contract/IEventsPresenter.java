package org.fossasia.openevent.app.events.contract;

import org.fossasia.openevent.app.common.contract.presenter.IPresenter;
import org.fossasia.openevent.app.data.models.Event;

import java.util.List;

public interface IEventsPresenter extends IPresenter<IEventsView> {

    List<Event> getEvents();

    void loadUserEvents(boolean forceReload);

    void loadOrganiser(boolean forceReload);

}
