package org.fossasia.openevent.app.data.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.fossasia.openevent.app.data.db.configuration.OrgaDatabase;

@Table(database = OrgaDatabase.class, allFields = true)
public class License extends BaseModel {

    @PrimaryKey
    private long id;

    private String name;
    private String description;
    @JsonProperty("compact_logo")
    private String compactLogo;
    private String logo;
    private String url;
    @JsonProperty("long_name")
    private String longName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCompactLogo() {
        return compactLogo;
    }

    public void setCompactLogo(String compactLogo) {
        this.compactLogo = compactLogo;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
