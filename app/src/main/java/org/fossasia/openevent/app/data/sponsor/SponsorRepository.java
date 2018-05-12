package org.fossasia.openevent.app.data.sponsor;

import android.support.annotation.NonNull;

import io.reactivex.Observable;

public interface SponsorRepository {

    @NonNull
    Observable<Sponsor> getSponsors(long eventId, boolean reload);

    Observable<Sponsor> createSponsor(Sponsor sponsor);

}
