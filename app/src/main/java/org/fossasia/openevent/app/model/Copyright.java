
package org.fossasia.openevent.app.model;


import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class Copyright {

    @SerializedName("holder")
    private String mHolder;
    @SerializedName("holder_url")
    private Object mHolderUrl;
    @SerializedName("licence")
    private String mLicence;
    @SerializedName("licence_url")
    private String mLicenceUrl;
    @SerializedName("logo")
    private String mLogo;
    @SerializedName("year")
    private Long mYear;

    public String getHolder() {
        return mHolder;
    }

    public void setHolder(String holder) {
        mHolder = holder;
    }

    public Object getHolderUrl() {
        return mHolderUrl;
    }

    public void setHolderUrl(Object holderUrl) {
        mHolderUrl = holderUrl;
    }

    public String getLicence() {
        return mLicence;
    }

    public void setLicence(String licence) {
        mLicence = licence;
    }

    public String getLicenceUrl() {
        return mLicenceUrl;
    }

    public void setLicenceUrl(String licenceUrl) {
        mLicenceUrl = licenceUrl;
    }

    public String getLogo() {
        return mLogo;
    }

    public void setLogo(String logo) {
        mLogo = logo;
    }

    public Long getYear() {
        return mYear;
    }

    public void setYear(Long year) {
        mYear = year;
    }

}
