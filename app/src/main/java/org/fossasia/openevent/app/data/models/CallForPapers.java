package org.fossasia.openevent.app.data.models;

import com.google.gson.annotations.SerializedName;

public class CallForPapers {

    @SerializedName("announcement")
    private String announcement;
    @SerializedName("end_date")
    private String endDate;
    @SerializedName("privacy")
    private String privacy;
    @SerializedName("start_date")
    private String startDate;
    @SerializedName("timezone")
    private String timezone;

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

}
