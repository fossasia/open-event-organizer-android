package org.fossasia.openevent.app.common.data.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.jasminb.jsonapi.LongIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import com.raizlabs.android.dbflow.annotation.ColumnIgnore;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.fossasia.openevent.app.common.data.db.configuration.OrgaDatabase;
import org.fossasia.openevent.app.common.data.models.contract.IHeaderProvider;
import org.fossasia.openevent.app.common.data.models.delegates.EventAnalyticsDelegate;
import org.fossasia.openevent.app.common.data.models.delegates.EventDelegate;
import org.fossasia.openevent.app.common.data.models.delegates.contract.IEventDelegate;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Delegate;

@Data
@Builder
@Type("event")
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy.class)
@Table(database = OrgaDatabase.class, allFields = true)
@SuppressWarnings({ "PMD.ExcessivePublicCount", "PMD.TooManyFields" })
public class Event implements Comparable<Event>, IHeaderProvider {

    @Delegate(types = IEventDelegate.class, excludes = Excluding.class)
    private final EventDelegate eventDelegate = new EventDelegate(this);

    @Delegate(types = EventAnalyticsDelegate.class)
    public final EventAnalyticsDelegate analytics = new EventAnalyticsDelegate();

    @Id(LongIdHandler.class)
    @PrimaryKey
    public long id;
    public String paymentCountry;
    public String paypalEmail;
    public String thumbnailImageUrl;
    public String schedulePublishedOn;
    public String paymentCurrency;
    public String organizerDescription;
    public boolean isMapShown;
    public String originalImageUrl;
    public String onsiteDetails;
    public String organizerName;
    public boolean canPayByStripe;
    public String largeImageUrl;
    public String timezone;
    public boolean canPayOnsite;
    public String deletedAt;
    public String ticketUrl;
    public boolean canPayByPaypal;
    public String locationName;
    public boolean isSponsorsEnabled;
    public boolean hasOrganizerInfo;
    public boolean isSessionsSpeakersEnabled;
    public String privacy;
    public String codeOfConduct;
    public String state;
    public double latitude;
    public String startsAt;
    public String searchableLocationName;
    public boolean isTicketingEnabled;
    public boolean canPayByCheque;
    public String description;
    public String pentabarfUrl;
    public String xcalUrl;
    public String logoUrl;
    public String externalEventUrl;
    public boolean isTaxEnabled;
    public String iconImageUrl;
    public String icalUrl;
    public String name;
    public boolean canPayByBank;
    public String endsAt;
    public String createdAt;
    public double longitude;
    public String bankDetails;
    public String chequeDetails;
    public String identifier;

    public boolean isComplete;

    @ColumnIgnore
    @Relationship("tickets")
    public List<Ticket> tickets;

    public Event() { }

    // One to Many implementation
    @JsonIgnore
    @OneToMany(methods = {OneToMany.Method.SAVE}, variableName = "tickets")
    List<Ticket> getEventTickets() {
        return eventDelegate.getEventTickets();
    }

    private interface Excluding {
        List<Ticket> getEventTickets();
    }
}
