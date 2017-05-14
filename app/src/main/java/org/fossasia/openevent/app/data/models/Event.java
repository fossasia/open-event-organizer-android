package org.fossasia.openevent.app.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Event {

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
    @SerializedName("TICKETS")
    private List<Ticket> tickets;
    @SerializedName("timezone")
    private String timezone;
    @SerializedName("topic")
    private String topic;
    @SerializedName("type")
    private String type;
    @SerializedName("version")
    private Version version;

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

}
