package org.fossasia.openevent.app.data.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
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
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Table(database = OrgaDatabase.class, allFields = true)
public class User {
    @PrimaryKey
    public int id;
    public String lastAccessTime;
    public String signupTime;
    @ForeignKey(
        saveForeignKeyModel = true,
        deleteForeignKeyModel = true,
        onDelete = ForeignKeyAction.CASCADE)
    public UserDetail userDetail;
    public String email;

    public User() {}

    public User(String email) {
        this.email = email;
    }
}
