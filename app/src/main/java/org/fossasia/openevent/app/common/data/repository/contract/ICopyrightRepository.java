package org.fossasia.openevent.app.common.data.repository.contract;

import android.support.annotation.NonNull;

import org.fossasia.openevent.app.common.data.models.Copyright;

import io.reactivex.Observable;

public interface ICopyrightRepository {

    @NonNull
    Observable<Copyright> createCopyright(Copyright copyright);

    @NonNull
    Observable<Copyright> getCopyright(long id, boolean reload);
}
