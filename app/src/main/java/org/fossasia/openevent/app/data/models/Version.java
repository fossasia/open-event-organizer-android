package org.fossasia.openevent.app.data.models;

import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.fossasia.openevent.app.data.db.configuration.OrgaDatabase;

@Table(database = OrgaDatabase.class, allFields = true)
public class Version extends BaseModel {

    @PrimaryKey
    @SerializedName("event_ver")
    private long eventVer;
    @SerializedName("microlocations_ver")
    private long microlocationsVer;
    @SerializedName("sessions_ver")
    private long sessionsVer;
    @SerializedName("speakers_ver")
    private long speakersVer;
    @SerializedName("sponsors_ver")
    private long sponsorsVer;
    @SerializedName("tracks_ver")
    private long tracksVer;

    public long getEventVer() {
        return eventVer;
    }

    public void setEventVer(long eventVer) {
        this.eventVer = eventVer;
    }

    public long getMicrolocationsVer() {
        return microlocationsVer;
    }

    public void setMicrolocationsVer(long microlocationsVer) {
        this.microlocationsVer = microlocationsVer;
    }

    public long getSessionsVer() {
        return sessionsVer;
    }

    public void setSessionsVer(long sessionsVer) {
        this.sessionsVer = sessionsVer;
    }

    public long getSpeakersVer() {
        return speakersVer;
    }

    public void setSpeakersVer(long speakersVer) {
        this.speakersVer = speakersVer;
    }

    public long getSponsorsVer() {
        return sponsorsVer;
    }

    public void setSponsorsVer(long sponsorsVer) {
        this.sponsorsVer = sponsorsVer;
    }

    public long getTracksVer() {
        return tracksVer;
    }

    public void setTracksVer(long tracksVer) {
        this.tracksVer = tracksVer;
    }

    @Override
    public String toString() {
        return "Version{" +
            "eventVer=" + eventVer +
            ", microlocationsVer=" + microlocationsVer +
            ", sessionsVer=" + sessionsVer +
            ", speakersVer=" + speakersVer +
            ", sponsorsVer=" + sponsorsVer +
            ", tracksVer=" + tracksVer +
            '}';
    }
}
