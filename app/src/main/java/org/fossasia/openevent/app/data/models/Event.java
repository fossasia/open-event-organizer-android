package org.fossasia.openevent.app.data.models;

import android.databinding.ObservableField;
import android.databinding.ObservableFloat;
import android.databinding.ObservableLong;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.ColumnIgnore;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.fossasia.openevent.app.data.db.configuration.OrgaDatabase;
import org.fossasia.openevent.app.utils.DateUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.List;


@Table(database = OrgaDatabase.class, allFields = true)
public class Event implements Comparable<Event> {

    @PrimaryKey
    private long id;

    // Foreign Key Section - Lazy Load
    // Need to explicitly associate the fields to save them

    @ForeignKey(stubbedRelationship = true, saveForeignKeyModel = true)
    @JsonProperty("call_for_papers")
    private CallForPapers callForPapers;

    @ForeignKey(stubbedRelationship = true, saveForeignKeyModel = true)
    private Copyright copyright;

    @ForeignKey(stubbedRelationship = true, saveForeignKeyModel = true)
    @JsonProperty("licence_details")
    private License licenseDetails;

    @ForeignKey(stubbedRelationship = true, saveForeignKeyModel = true)
    private Version version;

    @ColumnIgnore
    @JsonProperty("social_links")
    List<SocialLink> socialLinks;

    @ColumnIgnore
    List<Ticket> tickets;

    // Images
    @JsonProperty("background_image")
    private String backgroundImage;
    private String logo;
    private String large;
    private String thumbnail;
    @JsonProperty("placeholder_url")
    private String placeholderUrl;

    // Event Info
    private String identifier;
    private String name;
    private String description;
    private String email;
    private double latitude;
    private double longitude;
    @JsonProperty("location_name")
    private String locationName;
    @JsonProperty("searchable_location_name")
    private String searchableLocationName;
    @JsonProperty("start_time")
    private String startTime;
    @JsonProperty("end_time")
    private String endTime;
    private String timezone;
    private String topic;
    @JsonProperty("sub_topic")
    private String subTopic;
    private String type;
    private String state;
    @JsonProperty("event_url")
    private String eventUrl;
    @JsonProperty("has_session_speakers")
    private boolean hasSessionSpeakers;
    @JsonProperty("code_of_conduct")
    private String codeOfConduct;
    private String privacy;
    @JsonProperty("schedule_published_on")
    private String schedulePublishedOn;
    @JsonProperty("ticket_url")
    private String ticketUrl;

    @JsonProperty("organizer_description")
    private String organizerDescription;
    @JsonProperty("organizer_name")
    private String organizerName;

    // Tells if the event saved is complete ( with tickets )
    private boolean isComplete;

    // For Data Binding
    public final ObservableField<String> startDate = new ObservableField<>();
    public final ObservableField<String> endDate = new ObservableField<>();
    public final ObservableField<String> eventStartTime = new ObservableField<>();

    public final ObservableLong totalAttendees = new ObservableLong();
    public final ObservableLong totalTickets = new ObservableLong();
    public final ObservableLong checkedIn = new ObservableLong();
    public final ObservableFloat totalSale = new ObservableFloat();

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

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public CallForPapers getCallForPapers() {
        return callForPapers;
    }

    public void setCallForPapers(CallForPapers callForPapers) {
        this.callForPapers = callForPapers;
    }

    public void associateCallForPapers() {
        if (callForPapers != null)
            callForPapers.setId(id);
    }

    public String getCodeOfConduct() {
        return codeOfConduct;
    }

    public void setCodeOfConduct(String codeOfConduct) {
        this.codeOfConduct = codeOfConduct;
    }

    public Copyright getCopyright() {
        return copyright;
    }

    public void setCopyright(Copyright copyright) {
        this.copyright = copyright;
    }

    public void associateCopyright() {
        if (copyright != null)
            copyright.setId(id);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getEventUrl() {
        return eventUrl;
    }

    public void setEventUrl(String eventUrl) {
        this.eventUrl = eventUrl;
    }

    public boolean getHasSessionSpeakers() {
        return hasSessionSpeakers;
    }

    public void setHasSessionSpeakers(boolean hasSessionSpeakers) {
        this.hasSessionSpeakers = hasSessionSpeakers;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getLarge() {
        return large;
    }

    public void setLarge(String large) {
        this.large = large;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isHasSessionSpeakers() {
        return hasSessionSpeakers;
    }

    public License getLicenseDetails() {
        return licenseDetails;
    }

    public void setLicenseDetails(License licenseDetails) {
        this.licenseDetails = licenseDetails;
    }

    public void associateLicenseDetails() {
        if (licenseDetails != null)
            licenseDetails.setId(id);
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganizerDescription() {
        return organizerDescription;
    }

    public void setOrganizerDescription(String organizerDescription) {
        this.organizerDescription = organizerDescription;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

    public String getPlaceholderUrl() {
        return placeholderUrl;
    }

    public void setPlaceholderUrl(String placeholderUrl) {
        this.placeholderUrl = placeholderUrl;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public String getSchedulePublishedOn() {
        return schedulePublishedOn;
    }

    public void setSchedulePublishedOn(String schedulePublishedOn) {
        this.schedulePublishedOn = schedulePublishedOn;
    }

    public String getSearchableLocationName() {
        return searchableLocationName;
    }

    public void setSearchableLocationName(String searchableLocationName) {
        this.searchableLocationName = searchableLocationName;
    }

    public List<SocialLink> getSocialLinks() {
        return socialLinks;
    }

    public void setSocialLinks(List<SocialLink> socialLinks) {
        this.socialLinks = socialLinks;
    }

    public void associateSocialLinks() {
        if (socialLinks == null)
            return;

        for(SocialLink socialLink : socialLinks) {
            socialLink.setEvent(this);
        }
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSubTopic() {
        return subTopic;
    }

    public void setSubTopic(String subTopic) {
        this.subTopic = subTopic;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTicketUrl() {
        return ticketUrl;
    }

    public void setTicketUrl(String ticketUrl) {
        this.ticketUrl = ticketUrl;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public void associateTickets() {
        if (tickets == null)
            return;

        for (Ticket ticket : tickets) {
            ticket.setEvent(this);
        }
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Event{" +
            "id=" + id +
            ", callForPapers=" + callForPapers +
            ", copyright=" + copyright +
            ", licenseDetails=" + licenseDetails +
            ", version=" + version +
            ", socialLinks=" + socialLinks +
            ", tickets=" + tickets +
            ", backgroundImage='" + backgroundImage + '\'' +
            ", logo='" + logo + '\'' +
            ", large='" + large + '\'' +
            ", thumbnail='" + thumbnail + '\'' +
            ", placeholderUrl='" + placeholderUrl + '\'' +
            ", identifier='" + identifier + '\'' +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", email='" + email + '\'' +
            ", latitude=" + latitude +
            ", longitude=" + longitude +
            ", locationName='" + locationName + '\'' +
            ", searchableLocationName='" + searchableLocationName + '\'' +
            ", startTime='" + startTime + '\'' +
            ", endTime='" + endTime + '\'' +
            ", timezone='" + timezone + '\'' +
            ", topic='" + topic + '\'' +
            ", subTopic='" + subTopic + '\'' +
            ", type='" + type + '\'' +
            ", state='" + state + '\'' +
            ", eventUrl='" + eventUrl + '\'' +
            ", hasSessionSpeakers=" + hasSessionSpeakers +
            ", codeOfConduct='" + codeOfConduct + '\'' +
            ", privacy='" + privacy + '\'' +
            ", schedulePublishedOn='" + schedulePublishedOn + '\'' +
            ", ticketUrl='" + ticketUrl + '\'' +
            ", organizerDescription='" + organizerDescription + '\'' +
            ", organizerName='" + organizerName + '\'' +
            ", startDate=" + startDate +
            ", endDate=" + endDate +
            ", eventStartTime=" + eventStartTime +
            ", totalAttendees=" + totalAttendees +
            ", totalTickets=" + totalTickets +
            ", checkedIn=" + checkedIn +
            ", totalSales=" + totalSale +
            '}';
    }

    // One to Many implementation

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "tickets")
    public List<Ticket> getEventTickets() {
        if(tickets != null && !tickets.isEmpty())
            return tickets;

        tickets = SQLite.select()
            .from(Ticket.class)
            .where(Ticket_Table.event_id.eq(id))
            .queryList();

        return tickets;
    }

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "socialLinks")
    public List<SocialLink> getEventSocialLinks() {
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
        DateUtils dateUtils = new DateUtils();
        Date now = new Date();
        try {
            Date startDate = dateUtils.parse(startTime);
            Date endDate = dateUtils.parse(endTime);
            Date otherStartDate = dateUtils.parse(otherEvent.startTime);
            Date otherEndDate = dateUtils.parse(otherEvent.endTime);
            if (endDate.before(now) || otherEndDate.before(now)) { // one of them is past and other can be past or live or upcoming
                return endDate.after(otherEndDate) ? -1 : 1;
            } else {
                if (startDate.after(now) || otherStartDate.after(now)) { // one of them is upcoming other can be upcoming or live
                    return startDate.before(otherStartDate) ? -1 : 1;
                } else { // both are live
                    return startDate.after(otherStartDate) ? -1 : 1;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 1;
    }
}
