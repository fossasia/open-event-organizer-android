package com.eventyay.organizer.common.mvp.presenter;

public interface Presenter<V> extends BasePresenter {

    void attach(V view);
}
