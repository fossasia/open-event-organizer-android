package org.fossasia.openevent.app.module.faq.create.contract;

import org.fossasia.openevent.app.common.app.lifecycle.contract.presenter.IPresenter;
import org.fossasia.openevent.app.common.data.models.Faq;

public interface ICreateFaqPresenter extends IPresenter<ICreateFaqView> {

    Faq getFaq();

    void createFaq();

}
