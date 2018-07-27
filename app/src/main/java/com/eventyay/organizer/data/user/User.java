package com.eventyay.organizer.data.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.jasminb.jsonapi.IntegerIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;
import com.raizlabs.android.dbflow.annotation.ColumnIgnore;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import com.eventyay.organizer.data.db.configuration.OrgaDatabase;

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
@SuppressWarnings("PMD.TooManyFields")
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
    @ColumnIgnore
    public String password;
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

    public User() { }
}
