package com.eventyay.organizer.data.copyright;

import androidx.annotation.NonNull;

import io.reactivex.Completable;
import io.reactivex.Observable;

public interface CopyrightRepository {

    @NonNull
    Observable<Copyright> createCopyright(Copyright copyright);

    @NonNull
    Observable<Copyright> getCopyright(long id, boolean reload);

    @NonNull
    Observable<Copyright> updateCopyright(Copyright copyright);

    @NonNull
    Completable deleteCopyright(long id);
}
