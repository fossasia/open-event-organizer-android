package org.fossasia.openevent.app.data.models;

import com.google.gson.annotations.SerializedName;

public class Version {

    @SerializedName("event_ver")
    private Long eventVer;
    @SerializedName("microlocations_ver")
    private Long microlocationsVer;
    @SerializedName("sessions_ver")
    private Long sessionsVer;
    @SerializedName("speakers_ver")
    private Long speakersVer;
    @SerializedName("sponsors_ver")
    private Long sponsorsVer;
    @SerializedName("tracks_ver")
    private Long tracksVer;

    public Long getEventVer() {
        return eventVer;
    }

    public void setEventVer(Long eventVer) {
        this.eventVer = eventVer;
    }

    public Long getMicrolocationsVer() {
        return microlocationsVer;
    }

    public void setMicrolocationsVer(Long microlocationsVer) {
        this.microlocationsVer = microlocationsVer;
    }

    public Long getSessionsVer() {
        return sessionsVer;
    }

    public void setSessionsVer(Long sessionsVer) {
        this.sessionsVer = sessionsVer;
    }

    public Long getSpeakersVer() {
        return speakersVer;
    }

    public void setSpeakersVer(Long speakersVer) {
        this.speakersVer = speakersVer;
    }

    public Long getSponsorsVer() {
        return sponsorsVer;
    }

    public void setSponsorsVer(Long sponsorsVer) {
        this.sponsorsVer = sponsorsVer;
    }

    public Long getTracksVer() {
        return tracksVer;
    }

    public void setTracksVer(Long tracksVer) {
        this.tracksVer = tracksVer;
    }

}
