package org.fossasia.openevent.app.common.mvp.presenter;

public interface Presenter<V> extends BasePresenter {

    void attach(V view);

}
