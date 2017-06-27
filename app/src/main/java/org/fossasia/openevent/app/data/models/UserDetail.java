package org.fossasia.openevent.app.data.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.fossasia.openevent.app.data.db.configuration.OrgaDatabase;

@Table(database = OrgaDatabase.class, allFields = true)
public class UserDetail extends BaseModel {

    @PrimaryKey(autoincrement = true)
    private int id;

    private String contact;
    private String twitter;
    @JsonProperty("firstname")
    private String firstName;
    private String avatar;
    private String facebook;
    @JsonProperty("lastname")
    private String lastName;
    private String details;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "UserDetail{" +
            "id=" + id +
            ", contact='" + contact + '\'' +
            ", twitter='" + twitter + '\'' +
            ", firstName='" + firstName + '\'' +
            ", avatar='" + avatar + '\'' +
            ", facebook='" + facebook + '\'' +
            ", lastName='" + lastName + '\'' +
            ", details='" + details + '\'' +
            '}';
    }
}
