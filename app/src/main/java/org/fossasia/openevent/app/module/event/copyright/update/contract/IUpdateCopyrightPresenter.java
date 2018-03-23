package org.fossasia.openevent.app.module.event.copyright.update.contract;

import org.fossasia.openevent.app.common.app.lifecycle.contract.presenter.IPresenter;
import org.fossasia.openevent.app.common.data.models.Copyright;

public interface IUpdateCopyrightPresenter extends IPresenter<IUpdateCopyrightView> {

    void updateCopyright(Copyright copyright);
}
