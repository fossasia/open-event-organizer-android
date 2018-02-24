package org.fossasia.openevent.app.module.event.create.contract;

import org.fossasia.openevent.app.common.app.lifecycle.contract.presenter.IPresenter;
import org.fossasia.openevent.app.common.data.models.Event;

public interface ICreateEventPresenter extends IPresenter<ICreateEventView> {

    Event getEvent();

    void createEvent();

}

