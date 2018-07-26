package com.eventyay.organizer.data.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.jasminb.jsonapi.LongIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import com.raizlabs.android.dbflow.annotation.ColumnIgnore;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import com.eventyay.organizer.common.model.HeaderProvider;
import com.eventyay.organizer.data.db.configuration.OrgaDatabase;
import com.eventyay.organizer.data.ticket.Ticket;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Delegate;

@Data
@Builder
@Type("event")
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy.class)
@Table(database = OrgaDatabase.class, allFields = true)
@EqualsAndHashCode(exclude = { "eventDelegate", "analytics" })
@SuppressWarnings({ "PMD.ExcessivePublicCount", "PMD.TooManyFields" })
public class Event implements Comparable<Event>, HeaderProvider {

    public static final String STATE_DRAFT = "draft";
    public static final String STATE_PUBLISHED = "published";

    @JsonIgnore
    @Delegate(types = EventDelegate.class)
    private final EventDelegateImpl eventDelegate = new EventDelegateImpl(this);

    @JsonIgnore
    @Delegate(types = EventAnalyticsDelegate.class)
    public final EventAnalyticsDelegate analytics = new EventAnalyticsDelegate();

    @Id(LongIdHandler.class)
    @PrimaryKey
    public Long id;
    public String paymentCountry;
    public String paypalEmail;
    public String thumbnailImageUrl;
    public String schedulePublishedOn;
    public String paymentCurrency;
    public String organizerDescription;
    public String originalImageUrl;
    public String onsiteDetails;
    public String organizerName;
    public String largeImageUrl;
    public String timezone;
    public String deletedAt;
    public String ticketUrl;
    public String locationName;
    public String privacy;
    public String codeOfConduct;
    public String state;
    public String searchableLocationName;
    public String description;
    public String pentabarfUrl;
    public String xcalUrl;
    public String logoUrl;
    public String externalEventUrl;
    public String iconImageUrl;
    public String icalUrl;
    public String name;
    public String createdAt;
    public String bankDetails;
    public String chequeDetails;
    public String identifier;
    public String startsAt;
    public String endsAt;
    public boolean isComplete;
    public Double latitude;
    public Double longitude;

    public boolean canPayByStripe;
    public boolean canPayByCheque;
    public boolean canPayByBank;
    public boolean canPayByPaypal;
    public boolean canPayOnsite;
    public boolean isSponsorsEnabled;
    public boolean hasOrganizerInfo;
    public boolean isSessionsSpeakersEnabled;
    public boolean isTicketingEnabled;
    public boolean isTaxEnabled;
    public boolean isMapShown;

    @ColumnIgnore
    @Relationship("tickets")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public List<Ticket> tickets;

    public Event() { }

}
