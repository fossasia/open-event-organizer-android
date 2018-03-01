package org.fossasia.openevent.app.common.data.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
public class Event implements Comparable<Event>, IHeaderProvider {

    public static final String STATE_DRAFT = "draft";
    public static final String STATE_PUBLISHED = "published";

    @Delegate(types = IEventDelegate.class, excludes = {Excluding.class})
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
    public Double latitude;
    public Double longitude;

    @JsonProperty("can-pay-by-stripe")
    public boolean canPayByStripe;
    @JsonProperty("can-pay-by-cheque")
    public boolean canPayByCheque;
    @JsonProperty("can-pay-by-bank")
    public boolean canPayByBank;
    @JsonProperty("can-pay-by-paypal")
    public boolean canPayByPaypal;
    @JsonProperty("can-pay-onsite")
    public boolean canPayOnsite;
    @JsonProperty("is-sponsors-enabled")
    public boolean isSponsorsEnabled;
    @JsonProperty("has-organizer-info")
    public boolean hasOrganizerInfo;
    @JsonProperty("is-sessions-speakers-enabled")
    public boolean isSessionsSpeakersEnabled;
    @JsonProperty("is-ticketing-enabled")
    public boolean isTicketingEnabled;
    @JsonProperty("is-tax-enabled")
    public boolean isTaxEnabled;
    @JsonProperty("is-map-shown")
    public boolean isMapShown;

    @JsonSerialize(using = ObservableStringSerializer.class)
    @JsonDeserialize(using = ObservableStringDeserializer.class)
    public ObservableString startsAt = new ObservableString();
    @JsonSerialize(using = ObservableStringSerializer.class)
    @JsonDeserialize(using = ObservableStringDeserializer.class)
    public ObservableString endsAt = new ObservableString();

    @ColumnIgnore
    @Relationship("tickets")
    public List<Ticket> tickets;

    @ColumnIgnore
    @Relationship("faqs")
    public List<Faq> faqs;

    public Event() { }


    // One to Many implementation
    @JsonIgnore
    @OneToMany(methods = {OneToMany.Method.SAVE}, variableName = "tickets")
    List<Ticket> getEventTickets() {
        return eventDelegate.getEventTickets();
    }

    @JsonIgnore
    @OneToMany(methods = {OneToMany.Method.SAVE}, variableName = "faqs")
    List<Faq> getEventFaqs() {
        return eventDelegate.getEventFaqs();
    }

    private interface Excluding {
        List<Ticket> getEventTickets();

        List<Faq> getEventFaqs();
    }

}
