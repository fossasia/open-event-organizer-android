package org.fossasia.openevent.app.common.data.models;

import android.databinding.ObservableBoolean;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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
import org.fossasia.openevent.app.common.data.models.dto.ObservableString;
import org.fossasia.openevent.app.common.utils.json.ObservableStringDeserializer;
import org.fossasia.openevent.app.common.utils.json.ObservableStringSerializer;

import java.io.File;
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
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@EqualsAndHashCode(exclude = { "eventDelegate", "analytics" })
@SuppressWarnings({ "PMD.ExcessivePublicCount", "PMD.TooManyFields" })
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE, creatorVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class Event implements Comparable<Event>, IHeaderProvider {

    private static final String DEFAULT_STRING = "example";
    private static final String DEFAULT_PROTOCOL = "http:";
    private static final String DEFAULT_URL = "www.example.com";
    private static final String DEFAULT_CURRENCY = "INR";
    public static final String STATE_DRAFT = "draft";
    public static final String STATE_PUBLISHED = "published";

    @Delegate(types = IEventDelegate.class, excludes = Excluding.class)
    private final EventDelegate eventDelegate = new EventDelegate(this);

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

    public boolean isComplete;

    @JsonSerialize(using = ToStringSerializer.class)
    public double latitude;
    @JsonSerialize(using = ToStringSerializer.class)
    public double longitude;
    @JsonSerialize(using = ToStringSerializer.class)
    public boolean canPayByStripe;
    @JsonSerialize(using = ToStringSerializer.class)
    public boolean canPayByCheque;
    @JsonSerialize(using = ToStringSerializer.class)
    public boolean canPayByBank;
    @JsonSerialize(using = ToStringSerializer.class)
    public boolean canPayByPaypal;
    @JsonSerialize(using = ToStringSerializer.class)
    public boolean canPayOnsite;
    @JsonSerialize(using = ToStringSerializer.class)
    public boolean isSponsorsEnabled;
    @JsonSerialize(using = ToStringSerializer.class)
    public boolean hasOrganizerInfo;

    @JsonProperty("is-sessions-speakers-enabled")
    @JsonSerialize(using = ToStringSerializer.class)
    public boolean isSessionsSpeakersEnabled;
    @JsonProperty("is-ticketing-enabled")
    @JsonSerialize(using = ToStringSerializer.class)
    public boolean isTicketingEnabled;
    @JsonProperty("is-tax-enabled")
    @JsonSerialize(using = ToStringSerializer.class)
    public boolean isTaxEnabled;
    @JsonProperty("is-map-shown")
    @JsonSerialize(using = ToStringSerializer.class)
    public boolean isMapShown;

    @JsonSerialize(using = ObservableStringSerializer.class)
    @JsonDeserialize(using = ObservableStringDeserializer.class)
    public ObservableString startsAt = new ObservableString();
    @JsonSerialize(using = ObservableStringSerializer.class)
    @JsonDeserialize(using = ObservableStringDeserializer.class)
    public ObservableString endsAt = new ObservableString();

    @JsonIgnore
    public final ObservableBoolean creating = new ObservableBoolean();
    @JsonIgnore
    public final ObservableBoolean deleting = new ObservableBoolean();

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

    public void setDefaults() {
        paymentCountry = DEFAULT_STRING;
        paypalEmail = DEFAULT_STRING;
        paymentCurrency = DEFAULT_CURRENCY;
        organizerDescription = DEFAULT_STRING;
        onsiteDetails = DEFAULT_STRING;
        organizerName = DEFAULT_STRING;
        locationName = DEFAULT_STRING;
        privacy = DEFAULT_STRING;
        codeOfConduct = DEFAULT_STRING;
        state = STATE_DRAFT;
        searchableLocationName = DEFAULT_STRING;
        description = DEFAULT_STRING;
        bankDetails = DEFAULT_STRING;
        chequeDetails = DEFAULT_STRING;
        originalImageUrl = DEFAULT_PROTOCOL + File.separator + File.separator + DEFAULT_URL;
        externalEventUrl = DEFAULT_PROTOCOL + File.separator + File.separator + DEFAULT_URL;
        logoUrl = DEFAULT_PROTOCOL + File.separator + File.separator + DEFAULT_URL;
        ticketUrl = DEFAULT_PROTOCOL + File.separator + File.separator + DEFAULT_URL;
    }
}
