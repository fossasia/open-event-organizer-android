package org.fossasia.openevent.app.data.models;

import android.databinding.ObservableField;
import android.databinding.ObservableLong;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Event implements Parcelable {

    @SerializedName("background_image")
    private String backgroundImage;
    @SerializedName("call_for_papers")
    private CallForPapers callForPapers;
    @SerializedName("code_of_conduct")
    private String codeOfConduct;
    @SerializedName("copyright")
    private Copyright copyright;
    @SerializedName("description")
    private String description;
    @SerializedName("email")
    private String email;
    @SerializedName("end_time")
    private String endTime;
    @SerializedName("event_url")
    private String eventUrl;
    @SerializedName("has_session_speakers")
    private Boolean hasSessionSpeakers;
    @SerializedName("id")
    private Long id;
    @SerializedName("identifier")
    private String identifier;
    @SerializedName("large")
    private String large;
    @SerializedName("latitude")
    private Double latitude;
    @SerializedName("licence_details")
    private License licenceDetails;
    @SerializedName("location_name")
    private String locationName;
    @SerializedName("logo")
    private String logo;
    @SerializedName("longitude")
    private Double longitude;
    @SerializedName("name")
    private String name;
    @SerializedName("organizer_description")
    private String organizerDescription;
    @SerializedName("organizer_name")
    private String organizerName;
    @SerializedName("placeholder_url")
    private String placeholderUrl;
    @SerializedName("privacy")
    private String privacy;
    @SerializedName("schedule_published_on")
    private String schedulePublishedOn;
    @SerializedName("searchable_location_name")
    private String searchableLocationName;
    @SerializedName("social_links")
    private List<SocialLink> socialLinks;
    @SerializedName("start_time")
    private String startTime;
    @SerializedName("state")
    private String state;
    @SerializedName("sub_topic")
    private String subTopic;
    @SerializedName("thumbnail")
    private String thumbnail;
    @SerializedName("ticket_url")
    private String ticketUrl;
    @SerializedName("tickets")
    private List<Ticket> tickets;
    @SerializedName("timezone")
    private String timezone;
    @SerializedName("topic")
    private String topic;
    @SerializedName("type")
    private String type;
    @SerializedName("version")
    private Version version;

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

    public Boolean getHasSessionSpeakers() {
        return hasSessionSpeakers;
    }

    public void setHasSessionSpeakers(Boolean hasSessionSpeakers) {
        this.hasSessionSpeakers = hasSessionSpeakers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public License getLicenceDetails() {
        return licenceDetails;
    }

    public void setLicenceDetails(License licenceDetails) {
        this.licenceDetails = licenceDetails;
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

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
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
            "backgroundImage='" + backgroundImage + '\'' +
            ", description='" + description + '\'' +
            ", email='" + email + '\'' +
            ", endTime='" + endTime + '\'' +
            ", eventUrl='" + eventUrl + '\'' +
            ", id=" + id +
            ", logo='" + logo + '\'' +
            ", name='" + name + '\'' +
            ", organizerName='" + organizerName + '\'' +
            ", placeholderUrl='" + placeholderUrl + '\'' +
            ", startTime='" + startTime + '\'' +
            ", thumbnail='" + thumbnail + '\'' +
            ", ticketUrl='" + ticketUrl + '\'' +
            ", tickets=" + tickets +
            ", timezone='" + timezone + '\'' +
            ", type='" + type + '\'' +
            ", version=" + version +
            '}';
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
        this.id = (Long) in.readValue(Long.class.getClassLoader());
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
}
