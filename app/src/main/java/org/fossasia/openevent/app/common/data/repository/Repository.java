package org.fossasia.openevent.app.common.data.repository;

import org.fossasia.openevent.app.common.data.db.contract.IDatabaseRepository;
import org.fossasia.openevent.app.common.data.contract.IUtilModel;
import org.fossasia.openevent.app.common.data.network.EventService;
import org.fossasia.openevent.app.common.utils.core.Utils;

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
