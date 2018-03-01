package org.fossasia.openevent.app.module.faq.list.contract;

import org.fossasia.openevent.app.common.app.lifecycle.contract.presenter.IDetailPresenter;
import org.fossasia.openevent.app.common.data.models.Faq;

import java.util.List;

public interface IFaqListPresenter extends IDetailPresenter<Long, IFaqListView> {

    List<Faq> getFaqs();

    void loadFaqs(boolean forceReload);

}
