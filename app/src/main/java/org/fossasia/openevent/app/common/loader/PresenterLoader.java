package org.fossasia.openevent.app.common.loader;

import android.content.Context;
import android.support.v4.content.Loader;

import org.fossasia.openevent.app.common.contract.presenter.IBasePresenter;

public class PresenterLoader<T extends IBasePresenter> extends Loader<T> {

    private T presenter;

    public PresenterLoader(Context context, T presenter) {
        super(context);
        this.presenter = presenter;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        deliverResult(presenter);
    }

    @Override
    protected void onReset() {
        super.onReset();
        presenter.detach();
        presenter = null;
    }

    public T getPresenter() {
        return presenter;
    }
}
