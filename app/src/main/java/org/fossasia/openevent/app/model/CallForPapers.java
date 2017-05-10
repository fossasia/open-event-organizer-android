
package org.fossasia.openevent.app.model;


import com.google.gson.annotations.SerializedName;



public class CallForPapers {

    @SerializedName("announcement")
    private String mAnnouncement;
    @SerializedName("end_date")
    private String mEndDate;
    @SerializedName("privacy")
    private String mPrivacy;
    @SerializedName("start_date")
    private String mStartDate;
    @SerializedName("timezone")
    private String mTimezone;

    public String getAnnouncement() {
        return mAnnouncement;
    }

    public void setAnnouncement(String announcement) {
        mAnnouncement = announcement;
    }

    public String getEndDate() {
        return mEndDate;
    }

    public void setEndDate(String endDate) {
        mEndDate = endDate;
    }

    public String getPrivacy() {
        return mPrivacy;
    }

    public void setPrivacy(String privacy) {
        mPrivacy = privacy;
    }

    public String getStartDate() {
        return mStartDate;
    }

    public void setStartDate(String startDate) {
        mStartDate = startDate;
    }

    public String getTimezone() {
        return mTimezone;
    }

    public void setTimezone(String timezone) {
        mTimezone = timezone;
    }

}
