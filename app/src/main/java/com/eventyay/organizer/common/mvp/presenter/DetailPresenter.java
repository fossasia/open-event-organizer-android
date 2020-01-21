package com.eventyay.organizer.common.mvp.presenter;

public interface DetailPresenter<K, V> extends BasePresenter {

    void attach(K key, V view);
}
