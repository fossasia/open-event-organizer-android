package org.fossasia.openevent.app.data.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.jasminb.jsonapi.IntegerIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.fossasia.openevent.app.data.db.configuration.OrgaDatabase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode(of = {"id", "email"})
@Type("user")
@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy.class)
@Table(database = OrgaDatabase.class, allFields = true)
public class User {
    @Id(IntegerIdHandler.class)
    @PrimaryKey
    public int id;
    public boolean isAdmin;
    public String lastName;
    public String instagramUrl;
    public boolean isSuperAdmin;
    public String thumbnailImageUrl;
    public String createdAt;
    public String lastAccessedAt;
    public String email;
    public String iconImageUrl;
    public String contact;
    public String deletedAt;
    public String smallImageUrl;
    public String facebookUrl;
    public String details;
    public boolean isVerified;
    public String firstName;
    public String avatarUrl;
    public String twitterUrl;
    public String googlePlusUrl;

    public User() {}

    public User(String email) {
        this.email = email;
    }
}
