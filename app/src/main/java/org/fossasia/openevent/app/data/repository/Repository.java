package org.fossasia.openevent.app.data.repository;

import org.fossasia.openevent.app.data.IUtilModel;
import org.fossasia.openevent.app.data.db.IDatabaseRepository;
import org.fossasia.openevent.app.data.network.EventService;
import org.fossasia.openevent.app.utils.Utils;

/**
 * General Repository class. To be generified in future
 */
class Repository {

    protected IDatabaseRepository databaseRepository;
    protected EventService eventService;

    protected IUtilModel utilModel;

    Repository(IUtilModel utilModel, IDatabaseRepository databaseRepository, EventService eventService) {
        this.utilModel = utilModel;
        this.databaseRepository = databaseRepository;
        this.eventService = eventService;
    }

    String getAuthorization() {
        return Utils.formatToken(utilModel.getToken());
    }

}
