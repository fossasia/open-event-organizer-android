package org.fossasia.openevent.app.data.models;

import android.databinding.ObservableField;
import android.databinding.ObservableLong;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.ColumnIgnore;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.fossasia.openevent.app.data.db.configuration.OrgaDatabase;

import java.util.List;


@Table(database = OrgaDatabase.class, allFields = true)
public class Event extends BaseModel implements Parcelable {

    @PrimaryKey
    @SerializedName("id")
    private long id;

    // Foreign Key Section - Lazy Load
    // Need to explicitly associate the fields to save them

    @ForeignKey(stubbedRelationship = true, saveForeignKeyModel = true)
    @SerializedName("call_for_papers")
    private CallForPapers callForPapers;

    @ForeignKey(stubbedRelationship = true, saveForeignKeyModel = true)
    private Copyright copyright;

    @ForeignKey(stubbedRelationship = true, saveForeignKeyModel = true)
    @SerializedName("licence_details")
    private License licenseDetails;

    @ForeignKey(stubbedRelationship = true, saveForeignKeyModel = true)
    private Version version;

    @ColumnIgnore
    @SerializedName("social_links")
    List<SocialLink> socialLinks;

    @ColumnIgnore
    List<Ticket> tickets;

    // Images
    @SerializedName("background_image")
    private String backgroundImage;
    private String logo;
    private String large;
    private String thumbnail;
    @SerializedName("placeholder_url")
    private String placeholderUrl;

    // Event Info
    private String identifier;
    private String name;
    private String description;
    private String email;
    private double latitude;

    private double longitude;
    @SerializedName("location_name")
    private String locationName;
    @SerializedName("searchable_location_name")
    private String searchableLocationName;
    @SerializedName("start_time")
    private String startTime;
    @SerializedName("end_time")
    private String endTime;
    private String timezone;
    private String topic;
    @SerializedName("sub_topic")
    private String subTopic;
    private String type;
    private String state;
    @SerializedName("event_url")
    private String eventUrl;
    @SerializedName("has_session_speakers")
    private boolean hasSessionSpeakers;
    @SerializedName("code_of_conduct")
    private String codeOfConduct;
    private String privacy;
    @SerializedName("schedule_published_on")
    private String schedulePublishedOn;
    @SerializedName("ticket_url")
    private String ticketUrl;

    @SerializedName("organizer_description")
    private String organizerDescription;
    @SerializedName("organizer_name")
    private String organizerName;

    // Tells if the event saved is complete ( with tickets )
    private boolean isComplete = true;

    // For Data Binding
    public final ObservableField<String> startDate = new ObservableField<>();
    public final ObservableField<String> endDate = new ObservableField<>();
    public final ObservableField<String> eventStartTime = new ObservableField<>();

    public final ObservableLong totalAttendees = new ObservableLong();
    public final ObservableLong totalTickets = new ObservableLong();
    public final ObservableLong checkedIn = new ObservableLong();

    public Event() {}

    public Event(long id) {
        this.id = id;
    }

    @Override
    public boolean save() {
        associateCallForPapers();
        associateCopyright();
        associateLicenseDetails();
        associateSocialLinks();
        associateTickets();

        return super.save();
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

    // Parcelable Information - Only basic event info

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.backgroundImage);
        dest.writeString(this.description);
        dest.writeString(this.email);
        dest.writeString(this.endTime);
        dest.writeString(this.eventUrl);
        dest.writeValue(this.id);
        dest.writeString(this.large);
        dest.writeString(this.locationName);
        dest.writeString(this.logo);
        dest.writeString(this.name);
        dest.writeString(this.organizerName);
        dest.writeString(this.placeholderUrl);
        dest.writeString(this.startTime);
        dest.writeString(this.thumbnail);
        dest.writeString(this.ticketUrl);
        dest.writeString(this.timezone);
        dest.writeString(this.topic);
        dest.writeString(this.type);
    }

    protected Event(Parcel in) {
        this.backgroundImage = in.readString();
        this.description = in.readString();
        this.email = in.readString();
        this.endTime = in.readString();
        this.eventUrl = in.readString();
        this.id = (long) in.readValue(long.class.getClassLoader());
        this.large = in.readString();
        this.locationName = in.readString();
        this.logo = in.readString();
        this.name = in.readString();
        this.organizerName = in.readString();
        this.placeholderUrl = in.readString();
        this.startTime = in.readString();
        this.thumbnail = in.readString();
        this.ticketUrl = in.readString();
        this.timezone = in.readString();
        this.topic = in.readString();
        this.type = in.readString();
    }

    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel source) {
            return new Event(source);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }
}
