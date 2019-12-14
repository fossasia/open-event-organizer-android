package com.eventyay.organizer.data.sponsor;

import io.reactivex.Completable;
import io.reactivex.Observable;

public interface SponsorRepository {

    Observable<Sponsor> getSponsors(long eventId, boolean reload);

    Observable<Sponsor> getSponsor(long sponsorId, boolean reload);

    Observable<Sponsor> createSponsor(Sponsor sponsor);

    Observable<Sponsor> updateSponsor(Sponsor sponsor);

    Completable deleteSponsor(long id);
}
