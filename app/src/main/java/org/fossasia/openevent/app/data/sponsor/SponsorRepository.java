package org.fossasia.openevent.app.data.sponsor;

import io.reactivex.Observable;

public interface SponsorRepository {

    Observable<Sponsor> getSponsors(long eventId, boolean reload);

    Observable<Sponsor> getSponsor(long sponsorId, boolean reload);

    Observable<Sponsor> createSponsor(Sponsor sponsor);

    Observable<Sponsor> updateSponsor(Sponsor sponsor);

}
