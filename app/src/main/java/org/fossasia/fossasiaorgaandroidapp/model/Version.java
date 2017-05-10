
package org.fossasia.fossasiaorgaandroidapp.model;

import com.google.gson.annotations.SerializedName;


public class Version {

    @SerializedName("event_ver")
    private Long mEventVer;
    @SerializedName("microlocations_ver")
    private Long mMicrolocationsVer;
    @SerializedName("sessions_ver")
    private Long mSessionsVer;
    @SerializedName("speakers_ver")
    private Long mSpeakersVer;
    @SerializedName("sponsors_ver")
    private Long mSponsorsVer;
    @SerializedName("tracks_ver")
    private Long mTracksVer;

    public Long getEventVer() {
        return mEventVer;
    }

    public void setEventVer(Long eventVer) {
        mEventVer = eventVer;
    }

    public Long getMicrolocationsVer() {
        return mMicrolocationsVer;
    }

    public void setMicrolocationsVer(Long microlocationsVer) {
        mMicrolocationsVer = microlocationsVer;
    }

    public Long getSessionsVer() {
        return mSessionsVer;
    }

    public void setSessionsVer(Long sessionsVer) {
        mSessionsVer = sessionsVer;
    }

    public Long getSpeakersVer() {
        return mSpeakersVer;
    }

    public void setSpeakersVer(Long speakersVer) {
        mSpeakersVer = speakersVer;
    }

    public Long getSponsorsVer() {
        return mSponsorsVer;
    }

    public void setSponsorsVer(Long sponsorsVer) {
        mSponsorsVer = sponsorsVer;
    }

    public Long getTracksVer() {
        return mTracksVer;
    }

    public void setTracksVer(Long tracksVer) {
        mTracksVer = tracksVer;
    }

}
