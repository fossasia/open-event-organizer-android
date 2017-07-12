package org.fossasia.openevent.app.data.models;

import android.databinding.ObservableField;
import android.databinding.ObservableFloat;
import android.databinding.ObservableLong;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.raizlabs.android.dbflow.annotation.ColumnIgnore;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.fossasia.openevent.app.data.db.configuration.OrgaDatabase;
import org.fossasia.openevent.app.utils.DateService;

import java.util.List;

import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Table(database = OrgaDatabase.class, allFields = true)
public class Event implements Comparable<Event> {
    @PrimaryKey
    public long id;

    // Foreign Key Section - Lazy Load
    // Need to explicitly associate the fields to save them

    @ForeignKey(stubbedRelationship = true, saveForeignKeyModel = true)
    public CallForPapers callForPapers;

    @ForeignKey(stubbedRelationship = true, saveForeignKeyModel = true)
    public Copyright copyright;

    @ForeignKey(stubbedRelationship = true, saveForeignKeyModel = true)
    @JsonProperty("licence_details")
    public License licenseDetails;

    @ForeignKey(stubbedRelationship = true, saveForeignKeyModel = true)
    public Version version;

    @ColumnIgnore
    List<SocialLink> socialLinks;

    @ColumnIgnore
    List<Ticket> tickets;

    // Images
    public String backgroundImage;
    public String logo;
    public String large;
    public String thumbnail;
    public String placeholderUrl;

    // Event Info
    public String identifier;
    public String name;
    public String description;
    public String email;
    public double latitude;
    public double longitude;
    public String locationName;
    public String searchableLocationName;
    public String startTime;
    public String endTime;
    public String timezone;
    public String topic;
    public String subTopic;
    public String type;
    public String state;
    public String eventUrl;
    public boolean hasSessionSpeakers;
    public String codeOfConduct;
    public String privacy;
    public String schedulePublishedOn;
    public String ticketUrl;

    public String organizerDescription;
    public String organizerName;

    // Tells if the event saved is complete ( with tickets )
    public boolean isComplete;

    // For Data Binding
    public final ObservableField<String> startDate = new ObservableField<>();
    public final ObservableField<String> endDate = new ObservableField<>();
    public final ObservableField<String> eventStartTime = new ObservableField<>();

    public final ObservableLong totalAttendees = new ObservableLong();
    public final ObservableLong totalTickets = new ObservableLong();
    public final ObservableLong checkedIn = new ObservableLong();
    public final ObservableFloat totalSale = new ObservableFloat();

    public final ObservableLong freeTickets = new ObservableLong();
    public final ObservableLong paidTickets = new ObservableLong();
    public final ObservableLong donationTickets = new ObservableLong();

    public final ObservableLong soldFreeTickets = new ObservableLong();
    public final ObservableLong soldPaidTickets = new ObservableLong();
    public final ObservableLong soldDonationTickets = new ObservableLong();

    public Event() {}

    public Event(long id) {
        this.id = id;
    }

    public Event(long id, String startTime, String endTime) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    private void associateLinks() {
        associateCallForPapers();
        associateCopyright();
        associateLicenseDetails();
        associateSocialLinks();
        associateTickets();
    }

    private void associateCallForPapers() {
        if (callForPapers != null)
            callForPapers.setId(id);
    }

    private void associateCopyright() {
        if (copyright != null)
            copyright.setId(id);
    }

    private void associateLicenseDetails() {
        if (licenseDetails != null)
            licenseDetails.setId(id);
    }

    private void associateSocialLinks() {
        if (socialLinks == null)
            return;

        for(SocialLink socialLink : socialLinks) {
            socialLink.setEvent(this);
        }
    }

    private void associateTickets() {
        if (tickets == null)
            return;

        for (Ticket ticket : tickets) {
            ticket.setEvent(this);
        }
    }
    // One to Many implementation

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "tickets")
    protected List<Ticket> getEventTickets() {
        if(tickets != null && !tickets.isEmpty())
            return tickets;

        tickets = SQLite.select()
            .from(Ticket.class)
            .where(Ticket_Table.event_id.eq(id))
            .queryList();

        return tickets;
    }

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "socialLinks")
    protected List<SocialLink> getEventSocialLinks() {
        if(socialLinks != null && !socialLinks.isEmpty())
            return socialLinks;

        socialLinks = SQLite.select()
            .from(SocialLink.class)
            .where(SocialLink_Table.event_id.eq(id))
            .queryList();

        return socialLinks;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
        if (isComplete)
            associateLinks();
    }

    /**
     * Compare events for sorting
     * the list will be in order of live events, upcoming events, past events
     *
     * for both live events latest will be before in list
     * for both past events lately ended will be before in list
     * for both upcoming lately started will be before in list
     *
     * @param otherEvent event on right side in comparision
     * @return int
     */
    @Override
    public int compareTo(@NonNull Event otherEvent) {
        return DateService.compareEventDates(this, otherEvent);
    }
}
