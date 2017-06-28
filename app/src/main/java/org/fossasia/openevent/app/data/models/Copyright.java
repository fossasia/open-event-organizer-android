package org.fossasia.openevent.app.data.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.fossasia.openevent.app.data.db.configuration.OrgaDatabase;

@Table(database = OrgaDatabase.class, allFields = true)
public class Copyright extends BaseModel {

    @PrimaryKey
    public long id;

    private String holder;
    @JsonProperty("holder_url")
    private String holderUrl;
    @JsonProperty("licence")
    private String license;
    @JsonProperty("licence_url")
    private String licenseUrl;
    private String logo;
    private long year;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getHolder() {
        return holder;
    }

    public void setHolder(String holder) {
        this.holder = holder;
    }

    public String getHolderUrl() {
        return holderUrl;
    }

    public void setHolderUrl(String holderUrl) {
        this.holderUrl = holderUrl;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getLicenseUrl() {
        return licenseUrl;
    }

    public void setLicenseUrl(String licenseUrl) {
        this.licenseUrl = licenseUrl;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public long getYear() {
        return year;
    }

    public void setYear(long year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "Copyright{" +
            "id=" + id +
            ", holder='" + holder + '\'' +
            ", holderUrl='" + holderUrl + '\'' +
            ", license='" + license + '\'' +
            ", licenseUrl='" + licenseUrl + '\'' +
            ", logo='" + logo + '\'' +
            ", year=" + year +
            '}';
    }
}
