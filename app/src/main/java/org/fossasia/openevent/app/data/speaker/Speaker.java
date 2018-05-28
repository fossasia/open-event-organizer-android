package org.fossasia.openevent.app.data.speaker;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.jasminb.jsonapi.LongIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.fossasia.openevent.app.data.db.configuration.OrgaDatabase;
import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.data.session.Session;
import org.fossasia.openevent.app.data.user.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@Type("speaker")
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"event", "user"})
@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy.class)
@Table(database = OrgaDatabase.class, allFields = true)
@SuppressWarnings("PMD.TooManyFields")
public class Speaker {
    @Id(LongIdHandler.class)
    @PrimaryKey
    public Long id;

    public String website;
    public String city;
    public String shortBiography;
    public String name;
    public String speakingExperience;
    public String country;
    public String twitter;
    public String linkedin;
    public String email;
    public String longBiography;
    public String mobile;
    public String github;
    public String facebook;
    public String gender;
    public String position;
    public String organisation;
    public String photoUrl;
    public String thumbnailImageUrl;
    public String smallImageUrl;
    public String iconImageUrl;
    public String location;
    public String heardFrom;
    public String sponsorshipRequired;
    public boolean isFeatured;

    @Relationship("event")
    @ForeignKey(stubbedRelationship = true, onDelete = ForeignKeyAction.CASCADE)
    public Event event;

    @Relationship("user")
    @ForeignKey(stubbedRelationship = true, onDelete = ForeignKeyAction.CASCADE)
    public User user;
}
