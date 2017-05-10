
package org.fossasia.fossasiaorgaandroidapp.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class EventDetails {

    @SerializedName("background_image")
    private String mBackgroundImage;
    @SerializedName("call_for_papers")
    private Object mCallForPapers;
    @SerializedName("code_of_conduct")
    private String mCodeOfConduct;
    @SerializedName("copyright")
    private Copyright mCopyright;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("email")
    private Object mEmail;
    @SerializedName("end_time")
    private String mEndTime;
    @SerializedName("event_url")
    private String mEventUrl;
    @SerializedName("has_session_speakers")
    private Boolean mHasSessionSpeakers;
    @SerializedName("id")
    private Long mId;
    @SerializedName("identifier")
    private String mIdentifier;
    @SerializedName("large")
    private String mLarge;
    @SerializedName("latitude")
    private Double mLatitude;
    @SerializedName("licence_details")
    private Object mLicenceDetails;
    @SerializedName("location_name")
    private String mLocationName;
    @SerializedName("logo")
    private String mLogo;
    @SerializedName("longitude")
    private Double mLongitude;
    @SerializedName("name")
    private String mName;
    @SerializedName("organizer_description")
    private String mOrganizerDescription;
    @SerializedName("organizer_name")
    private String mOrganizerName;
    @SerializedName("placeholder_url")
    private String mPlaceholderUrl;
    @SerializedName("privacy")
    private String mPrivacy;
    @SerializedName("schedule_published_on")
    private Object mSchedulePublishedOn;
    @SerializedName("searchable_location_name")
    private String mSearchableLocationName;
    @SerializedName("social_links")
    private List<Object> mSocialLinks;
    @SerializedName("start_time")
    private String mStartTime;
    @SerializedName("state")
    private String mState;
    @SerializedName("sub_topic")
    private String mSubTopic;
    @SerializedName("thumbnail")
    private String mThumbnail;
    @SerializedName("ticket_url")
    private String mTicketUrl;
    @SerializedName("tickets")
    private List<Ticket> mTickets;
    @SerializedName("timezone")
    private String mTimezone;
    @SerializedName("topic")
    private String mTopic;
    @SerializedName("type")
    private String mType;
    @SerializedName("version")
    private Version mVersion;

    public String getBackgroundImage() {
        return mBackgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        mBackgroundImage = backgroundImage;
    }

    public Object getCallForPapers() {
        return mCallForPapers;
    }

    public void setCallForPapers(Object callForPapers) {
        mCallForPapers = callForPapers;
    }

    public String getCodeOfConduct() {
        return mCodeOfConduct;
    }

    public void setCodeOfConduct(String codeOfConduct) {
        mCodeOfConduct = codeOfConduct;
    }

    public Copyright getCopyright() {
        return mCopyright;
    }

    public void setCopyright(Copyright copyright) {
        mCopyright = copyright;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public Object getEmail() {
        return mEmail;
    }

    public void setEmail(Object email) {
        mEmail = email;
    }

    public String getEndTime() {
        return mEndTime;
    }

    public void setEndTime(String endTime) {
        mEndTime = endTime;
    }

    public String getEventUrl() {
        return mEventUrl;
    }

    public void setEventUrl(String eventUrl) {
        mEventUrl = eventUrl;
    }

    public Boolean getHasSessionSpeakers() {
        return mHasSessionSpeakers;
    }

    public void setHasSessionSpeakers(Boolean hasSessionSpeakers) {
        mHasSessionSpeakers = hasSessionSpeakers;
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public String getIdentifier() {
        return mIdentifier;
    }

    public void setIdentifier(String identifier) {
        mIdentifier = identifier;
    }

    public String getLarge() {
        return mLarge;
    }

    public void setLarge(String large) {
        mLarge = large;
    }

    public Double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(Double latitude) {
        mLatitude = latitude;
    }

    public Object getLicenceDetails() {
        return mLicenceDetails;
    }

    public void setLicenceDetails(Object licenceDetails) {
        mLicenceDetails = licenceDetails;
    }

    public String getLocationName() {
        return mLocationName;
    }

    public void setLocationName(String locationName) {
        mLocationName = locationName;
    }

    public String getLogo() {
        return mLogo;
    }

    public void setLogo(String logo) {
        mLogo = logo;
    }

    public Double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(Double longitude) {
        mLongitude = longitude;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getOrganizerDescription() {
        return mOrganizerDescription;
    }

    public void setOrganizerDescription(String organizerDescription) {
        mOrganizerDescription = organizerDescription;
    }

    public String getOrganizerName() {
        return mOrganizerName;
    }

    public void setOrganizerName(String organizerName) {
        mOrganizerName = organizerName;
    }

    public String getPlaceholderUrl() {
        return mPlaceholderUrl;
    }

    public void setPlaceholderUrl(String placeholderUrl) {
        mPlaceholderUrl = placeholderUrl;
    }

    public String getPrivacy() {
        return mPrivacy;
    }

    public void setPrivacy(String privacy) {
        mPrivacy = privacy;
    }

    public Object getSchedulePublishedOn() {
        return mSchedulePublishedOn;
    }

    public void setSchedulePublishedOn(Object schedulePublishedOn) {
        mSchedulePublishedOn = schedulePublishedOn;
    }

    public String getSearchableLocationName() {
        return mSearchableLocationName;
    }

    public void setSearchableLocationName(String searchableLocationName) {
        mSearchableLocationName = searchableLocationName;
    }

    public List<Object> getSocialLinks() {
        return mSocialLinks;
    }

    public void setSocialLinks(List<Object> socialLinks) {
        mSocialLinks = socialLinks;
    }

    public String getStartTime() {
        return mStartTime;
    }

    public void setStartTime(String startTime) {
        mStartTime = startTime;
    }

    public String getState() {
        return mState;
    }

    public void setState(String state) {
        mState = state;
    }

    public String getSubTopic() {
        return mSubTopic;
    }

    public void setSubTopic(String subTopic) {
        mSubTopic = subTopic;
    }

    public String getThumbnail() {
        return mThumbnail;
    }

    public void setThumbnail(String thumbnail) {
        mThumbnail = thumbnail;
    }

    public String getTicketUrl() {
        return mTicketUrl;
    }

    public void setTicketUrl(String ticketUrl) {
        mTicketUrl = ticketUrl;
    }

    public List<Ticket> getTickets() {
        return mTickets;
    }

    public void setTickets(List<Ticket> tickets) {
        mTickets = tickets;
    }

    public String getTimezone() {
        return mTimezone;
    }

    public void setTimezone(String timezone) {
        mTimezone = timezone;
    }

    public String getTopic() {
        return mTopic;
    }

    public void setTopic(String topic) {
        mTopic = topic;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public Version getVersion() {
        return mVersion;
    }

    public void setVersion(Version version) {
        mVersion = version;
    }

}
