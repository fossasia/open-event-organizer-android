package org.fossasia.openevent.app.data.models;

import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.fossasia.openevent.app.data.db.configuration.OrgaDatabase;

@Table(database = OrgaDatabase.class, allFields = true)
public class CallForPapers extends BaseModel {

    @PrimaryKey
    private long id;

    private String announcement;
    @SerializedName("end_date")
    private String endDate;
    private String privacy;
    @SerializedName("start_date")
    private String startDate;
    private String timezone;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    @Override
    public String toString() {
        return "CallForPapers{" +
            "id=" + id +
            ", announcement='" + announcement + '\'' +
            ", endDate='" + endDate + '\'' +
            ", privacy='" + privacy + '\'' +
            ", startDate='" + startDate + '\'' +
            ", timezone='" + timezone + '\'' +
            '}';
    }
}
