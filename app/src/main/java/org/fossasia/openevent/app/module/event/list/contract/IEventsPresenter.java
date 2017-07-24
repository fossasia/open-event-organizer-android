package org.fossasia.openevent.app.module.event.list.contract;

import org.fossasia.openevent.app.common.app.lifecycle.contract.presenter.IPresenter;
import org.fossasia.openevent.app.common.data.models.Event;

import java.util.List;

public interface IEventsPresenter extends IPresenter<IEventsView> {

    List<Event> getEvents();

    void loadUserEvents(boolean forceReload);

    void loadOrganiser(boolean forceReload);

}
